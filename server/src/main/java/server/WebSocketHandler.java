package server;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.DatabaseGameDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson gson = new Gson();
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final GameDAO gameDAO = new DatabaseGameDAO();

    @Override
    public void handleConnect(WsConnectContext ctx) {
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
        connectionManager.add(command.getGameID(), ctx.session);
        LoadGameMessage loadGame = new LoadGameMessage(gameDAO.getGame(command.getGameID()).game());
        ctx.session.getRemote().sendString(gson.toJson(loadGame));
        NotificationMessage notification = new NotificationMessage(command.getUsername() + " has connected to the game as " + command.getRole());
        connectionManager.broadcast(notification, command.getGameID(), ctx.session);
    }

    private void executeMakeMove(WsMessageContext ctx, UserGameCommand command) throws Exception {
        GameData game = gameDAO.getGame(command.getGameID());
        ChessMove moveRequest = command.getMove();
        ChessPosition start = moveRequest.getStartPosition();
        if (gameDAO.gameIsOver(command.getGameID())) {
            ErrorMessage error = new ErrorMessage("Error: The game has already ended!");
            ctx.session.getRemote().sendString(gson.toJson(error));
            return;
        }
        if (game.game().getTeamTurn() != command.getTeamColor()) {
            ErrorMessage error = new ErrorMessage("Error: Cannot play when it is not your turn!");
            ctx.session.getRemote().sendString(gson.toJson(error));
            return;
        }
        if (start == null) {
            ErrorMessage error = new ErrorMessage("Error: Cannot perform move because there is no piece there!");
            ctx.session.getRemote().sendString(gson.toJson(error));
            return;
        }
        TeamColor colorAtStart = game.game().getBoard().getPiece(start).getTeamColor();
        if (colorAtStart != command.getTeamColor()) {
            ErrorMessage error = new ErrorMessage("Error: Cannot move a piece that is not yours!");
            ctx.session.getRemote().sendString(gson.toJson(error));
            return;
        }
        Collection<ChessMove> validMoves = game.game().validMoves(start);
        if (!validMoves.contains(moveRequest)) {
            ErrorMessage error = new ErrorMessage("Error: Invalid move pattern!");
            ctx.session.getRemote().sendString(gson.toJson(error));
            return;
        }
        game.game().makeMove(moveRequest);
        gameDAO.updateGame(command.getGameID(), game);
        LoadGameMessage loadGame = new LoadGameMessage(gameDAO.getGame(command.getGameID()).game());
        connectionManager.broadcast(loadGame, command.getGameID(), null);
        String pieceType = switch (game.game().getBoard().getPiece(start).getPieceType()) {
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
        connectionManager.remove(command.getGameID(), ctx.session);
        if (gson.fromJson(command.getRole(), TeamColor.class) == TeamColor.WHITE) {
            gameDAO.updateWhitePlayer(command.getGameID(), null);
        }
        if (gson.fromJson(command.getRole(), TeamColor.class) == TeamColor.BLACK) {
            gameDAO.updateBlackPlayer(command.getGameID(), null);
        }
        NotificationMessage notification = new NotificationMessage(command.getUsername() + " has left to the game");
        connectionManager.broadcast(notification, command.getGameID(), ctx.session);
    }

    private void executeResign(WsMessageContext ctx, UserGameCommand command) throws Exception {
        if (!gameDAO.gameIsOver(command.getGameID())) {
            gameDAO.markGameOver(command.getGameID());
            NotificationMessage notification = new NotificationMessage(command.getUsername() + " has resigned");
            connectionManager.broadcast(notification, command.getGameID(), ctx.session);
        } else {
            ErrorMessage error = new ErrorMessage("Error: The game has already ended!");
            ctx.session.getRemote().sendString(gson.toJson(error));
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
    }
}
