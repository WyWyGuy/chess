package server;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DatabaseAuthDAO;
import dataaccess.DatabaseGameDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson gson = new Gson();
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final GameDAO gameDAO = new DatabaseGameDAO();
    private final AuthDAO authDAO = new DatabaseAuthDAO();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> executeConnect(ctx, command);
                case MAKE_MOVE -> executeMakeMove(ctx, command);
                case LEAVE -> executeLeave(ctx, command);
                case RESIGN -> executeResign(ctx, command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeConnect(WsMessageContext ctx, UserGameCommand command) throws Exception {
        if (!authDAO.authExists(command.getAuthToken())) {
            ErrorMessage errorMessage = new ErrorMessage("Error: unauthorized");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            return;
        }
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            String username = auth.username();
            connectionManager.add(command.getGameID(), ctx.session, username);
            LoadGameMessage loadGame = new LoadGameMessage(gameDAO.getGame(command.getGameID()).game());
            ctx.session.getRemote().sendString(gson.toJson(loadGame));
            NotificationMessage notification = new NotificationMessage(username + " has connected to the game as " + command.getRole());
            connectionManager.broadcast(notification, command.getGameID(), ctx.session);
        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage("Error: could not connect to the game");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
        }
    }

    private void executeMakeMove(WsMessageContext ctx, UserGameCommand command) throws Exception {
        GameData game = gameDAO.getGame(command.getGameID());
        ChessMove moveRequest = command.getMove();
        ChessPosition start = moveRequest.getStartPosition();
        ChessPiece movingPiece = game.game().getBoard().getPiece(start);
        if (gameDAO.gameIsOver(command.getGameID())) {
            ErrorMessage errorMessage = new ErrorMessage("Error: The game has already ended!");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            return;
        }
        String username;
        try {
            username = authDAO.getAuth(command.getAuthToken()).username();
        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage("Error: unauthorized");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            return;
        }
        boolean playerIsWhite = Objects.equals(username, game.whiteUsername());
        boolean playerIsBlack = Objects.equals(username, game.blackUsername());
        if (!playerIsWhite && !playerIsBlack) {
            ErrorMessage errorMessage = new ErrorMessage("Error: unauthorized");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            return;
        }
        if ((game.game().getTeamTurn() == TeamColor.WHITE && !playerIsWhite) || (game.game().getTeamTurn() == TeamColor.BLACK && !playerIsBlack)) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Cannot play when it is not your turn!");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            return;
        }
        if (movingPiece == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Cannot perform move because there is no piece there!");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            return;
        }
        TeamColor colorAtStart = movingPiece.getTeamColor();
        if ((colorAtStart == TeamColor.WHITE && !playerIsWhite) || (colorAtStart == TeamColor.BLACK && !playerIsBlack)) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Cannot move a piece that is not yours!");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            return;
        }
        Collection<ChessMove> validMoves = game.game().validMoves(start);
        if (validMoves == null || !validMoves.contains(moveRequest)) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid move pattern!");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            return;
        }
        if (colorAtStart != game.game().getTeamTurn()) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid move pattern!");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            return;
        }
        game.game().makeMove(moveRequest);
        gameDAO.updateGame(command.getGameID(), game);
        LoadGameMessage loadGame = new LoadGameMessage(gameDAO.getGame(command.getGameID()).game());
        connectionManager.broadcast(loadGame, command.getGameID(), null);
        String pieceType = switch (movingPiece.getPieceType()) {
            case KING -> "king";
            case QUEEN -> "queen";
            case ROOK -> "rook";
            case KNIGHT -> "knight";
            case BISHOP -> "bishop";
            case PAWN -> "pawn";
        };
        String startStr = "" + (char) ('a' + moveRequest.getStartPosition().getColumn() - 1) + moveRequest.getStartPosition().getRow();
        String endStr = "" + (char) ('a' + moveRequest.getEndPosition().getColumn() - 1) + moveRequest.getEndPosition().getRow();
        ChessPiece.PieceType promotingTo = moveRequest.getPromotionPiece();
        String promotionText;
        if (promotingTo == null) {
            promotionText = "";
        } else {
            promotionText = ", promoting to " + switch (promotingTo) {
                case QUEEN -> "queen";
                case ROOK -> "rook";
                case KNIGHT -> "knight";
                case BISHOP -> "bishop";
                default -> throw new Exception("Illegal promotion");
            };
        }
        String text = command.getUsername() + " moves " + pieceType + " from " + startStr + " to " + endStr + promotionText;
        NotificationMessage notification = new NotificationMessage(text);
        connectionManager.broadcast(notification, command.getGameID(), ctx.session);
        boolean endGame = false;
        if (game.game().isInCheckmate(TeamColor.WHITE)) {
            NotificationMessage whiteCheckmateNotification = new NotificationMessage(game.whiteUsername() + " is in checkmate!");
            connectionManager.broadcast(whiteCheckmateNotification, command.getGameID(), null);
            endGame = true;
        }
        if (game.game().isInCheckmate(TeamColor.BLACK)) {
            NotificationMessage blackCheckmateNotification = new NotificationMessage(game.blackUsername() + " is in checkmate!");
            connectionManager.broadcast(blackCheckmateNotification, command.getGameID(), null);
            endGame = true;
        }
        if (!endGame) {
            if (game.game().isInCheck(TeamColor.WHITE)) {
                NotificationMessage whiteCheckNotification = new NotificationMessage(game.whiteUsername() + " is in check!");
                connectionManager.broadcast(whiteCheckNotification, command.getGameID(), null);
            }
            if (game.game().isInCheck(TeamColor.BLACK)) {
                NotificationMessage blackCheckNotification = new NotificationMessage(game.blackUsername() + " is in check!");
                connectionManager.broadcast(blackCheckNotification, command.getGameID(), null);
            }
        }
        if (game.game().isInStalemate(TeamColor.WHITE)) {
            NotificationMessage whiteStalemateNotification = new NotificationMessage(game.whiteUsername() + " is in stalemate!");
            connectionManager.broadcast(whiteStalemateNotification, command.getGameID(), null);
            endGame = true;
        }
        if (game.game().isInStalemate(TeamColor.BLACK)) {
            NotificationMessage blackStalemateNotification = new NotificationMessage(game.blackUsername() + " is in stalemate!");
            connectionManager.broadcast(blackStalemateNotification, command.getGameID(), null);
            endGame = true;
        }
        if (endGame) {
            gameDAO.markGameOver(command.getGameID());
        }
    }

    private void executeLeave(WsMessageContext ctx, UserGameCommand command) throws Exception {
        connectionManager.remove(ctx.session);
        GameData game = gameDAO.getGame(command.getGameID());
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        String username = auth.username();
        if (Objects.equals(game.whiteUsername(), username)) {
            gameDAO.updateWhitePlayer(command.getGameID(), null);
        }
        if (Objects.equals(game.blackUsername(), username)) {
            gameDAO.updateBlackPlayer(command.getGameID(), null);
        }
        NotificationMessage notification = new NotificationMessage(command.getUsername() + " has left the game");
        connectionManager.broadcast(notification, command.getGameID(), ctx.session);
    }

    private void executeResign(WsMessageContext ctx, UserGameCommand command) throws Exception {
        String username;
        try {
            username = authDAO.getAuth(command.getAuthToken()).username();
            if ((!Objects.equals(username, gameDAO.getGame(command.getGameID()).whiteUsername())) &&
                    (!Objects.equals(username, gameDAO.getGame(command.getGameID()).blackUsername()))) {
                throw new Exception();
            }
        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage("Error: unauthorized");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            return;
        }
        if (!gameDAO.gameIsOver(command.getGameID())) {
            gameDAO.markGameOver(command.getGameID());
            NotificationMessage notification = new NotificationMessage(command.getUsername() + " has resigned");
            connectionManager.broadcast(notification, command.getGameID(), null);
        } else {
            ErrorMessage errorMessage = new ErrorMessage("Error: The game has already ended!");
            ctx.session.getRemote().sendString(gson.toJson(errorMessage));
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        Integer gameID = connectionManager.getGame(ctx.session);
        String username = connectionManager.getUser(ctx.session);
        if (gameID == null || username == null) {
            return;
        }
        try {
            GameData game = gameDAO.getGame(gameID);
            if (Objects.equals(game.whiteUsername(), username)) {
                gameDAO.updateWhitePlayer(gameID, null);
            } else if (Objects.equals(game.blackUsername(), username)) {
                gameDAO.updateBlackPlayer(gameID, null);
            }
        } catch (Exception e) {
            // pass
        }
        try {
            NotificationMessage notification = new NotificationMessage(username + " has left the game");
            connectionManager.broadcast(notification, gameID, ctx.session);
        } catch (Exception e) {
            // pass
        }
        connectionManager.remove(ctx.session);
    }
}
