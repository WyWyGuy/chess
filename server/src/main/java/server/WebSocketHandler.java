package server;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson gson = new Gson();

    @Override
    public void handleConnect(WsConnectContext ctx) {

    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        System.out.println(ctx.message());
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        System.out.println(command.getCommandType());
    }

    @Override
    public void handleClose(WsCloseContext ctx) {

    }
}
