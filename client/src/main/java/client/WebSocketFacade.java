package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import ui.UserInterface;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private Gson gson = new Gson();
    private final UserInterface ui;
    private String url;
    private UserGameCommand lastConnectCommand;

    public WebSocketFacade(UserInterface ui) {
        this.ui = ui;
    }

    public void connect(String url) throws Exception {
        this.url = url;
        if (session != null && session.isOpen()) {
            return;
        }
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, new URI(url));
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage provided = gson.fromJson(message, ServerMessage.class);

                switch (provided.getServerMessageType()) {
                    case NOTIFICATION -> handleNotification(gson.fromJson(message, NotificationMessage.class));
                    case LOAD_GAME -> handleLoadGame(gson.fromJson(message, LoadGameMessage.class));
                    case ERROR -> handleError(gson.fromJson(message, ErrorMessage.class));
                }
            }
        });
    }

    public void join(int gameID, String authToken, String username, boolean isWhite) throws Exception {
        lastConnectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, username, isWhite ? "white" : "black");
        ensureConnected();
        send(lastConnectCommand);
    }

    public void observe(int gameID, String authToken, String username) throws Exception {
        lastConnectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, username, "an observer");
        ensureConnected();
        send(lastConnectCommand);
    }

    public void leave(int gameID, String authToken, String username, ChessGame.TeamColor team) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, username, gson.toJson(team));
        ensureConnected();
        send(command);
    }

    public void resign(int gameID, String authToken, String username) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID, username, null);
        ensureConnected();
        send(command);
    }

    public void makeMove(int gameID, String authToken, String username, ChessMove moveRequest, boolean isWhite) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, username, null, moveRequest, (isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK));
        ensureConnected();
        send(command);
    }

    private void ensureConnected() throws Exception {
        if (session != null && session.isOpen()) {
            return;
        }
        if (url == null) {
            throw new IllegalStateException("WebSocket is not connected (no server URL). Re-join the game and try again.");
        }
        connect(url);
        if (lastConnectCommand != null) {
            send(lastConnectCommand);
        }
    }

    private synchronized void send(UserGameCommand command) throws IOException {
        if (session == null || !session.isOpen()) {
            throw new IOException("WebSocket connection is closed. Re-join the game and try again.");
        }
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    private void handleNotification(NotificationMessage notification) {
        System.out.println();
        System.out.println(notification.getMessage());
        System.out.print("Enter a command (type 'help' for a list of commands): ");
    }

    private void handleLoadGame(LoadGameMessage loadGame) {
        ui.curr_state = loadGame.getGame();
        System.out.println();
        ui.renderChessBoard(loadGame.getGame());
        System.out.print("Enter a command (type 'help' for a list of commands): ");
    }

    private void handleError(ErrorMessage error) {
        System.out.println();
        System.out.println(error.getError());
        System.out.print("Enter a command (type 'help' for a list of commands): ");
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        this.session = null;
        System.out.println();
        System.out.println("WebSocket disconnected: " + closeReason);
        System.out.print("Enter a command (type 'help' for a list of commands): ");
    }

    @Override
    public void onError(Session session, Throwable thr) {
        this.session = null;
        System.out.println();
        System.out.println("WebSocket error: " + (thr == null ? "unknown" : thr.getMessage()));
        System.out.print("Enter a command (type 'help' for a list of commands): ");
    }

}
