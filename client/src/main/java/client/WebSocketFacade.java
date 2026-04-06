package client;

import chess.ChessGame;
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

    public WebSocketFacade(UserInterface ui) {
        this.ui = ui;
    }

    public void connect(String url) throws Exception {
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
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, username, isWhite ? "white" : "black");
        String commandStr = gson.toJson(command);
        session.getBasicRemote().sendText(commandStr);
    }

    public void observe(int gameID, String authToken, String username) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, username, "an observer");
        String commandStr = gson.toJson(command);
        session.getBasicRemote().sendText(commandStr);
    }

    public void leave(int gameID, String authToken, String username, ChessGame.TeamColor team) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, username, gson.toJson(team));
        String commandStr = gson.toJson(command);
        session.getBasicRemote().sendText(commandStr);
    }

    private void handleNotification(NotificationMessage notification) {
        System.out.println();
        System.out.println(notification.getMessage());
        System.out.print("Enter a command (type 'help' for a list of commands): ");
    }

    private void handleLoadGame(LoadGameMessage loadGame) {
        ui.curr_state = loadGame.getGame();
        System.out.println();
        ui.renderChessBoard(loadGame.getGame(), loadGame.getIsWhite());
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

}
