package server;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;
import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson gson = new Gson();
    private ConnectionManager connectionManager = new ConnectionManager();

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
        ServerMessage loadGame = new ServerMessage(LOAD_GAME, null);
        ctx.session.getRemote().sendString(gson.toJson(loadGame));
        ServerMessage notification = new ServerMessage(NOTIFICATION, command.getUsername() + " has connected to the game as " + command.getRole());
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
