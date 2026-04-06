package server;

import com.google.gson.Gson;
import dataaccess.DatabaseGameDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;
import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson gson = new Gson();
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final GameDAO gameDAO = new DatabaseGameDAO();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        //TODO
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
        LoadGameMessage loadGame = new LoadGameMessage(gameDAO.getGame(command.getGameID()).game(), (!Objects.equals(command.getRole(), "black")));
        ctx.session.getRemote().sendString(gson.toJson(loadGame));
        NotificationMessage notification = new NotificationMessage(command.getUsername() + " has connected to the game as " + command.getRole());
        connectionManager.broadcast(notification, command.getGameID(), ctx.session);
    }

    private void executeMakeMove(WsMessageContext ctx, UserGameCommand command) {
        //TODO
    }

    private void executeLeave(WsMessageContext ctx, UserGameCommand command) {
        //TODO
    }

    private void executeResign(WsMessageContext ctx, UserGameCommand command) {
        //TODO
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        //TODO
    }
}
