package client;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;

import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private Gson gson = new Gson();

    public void connect(String url) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, new URI(url));
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

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

}
