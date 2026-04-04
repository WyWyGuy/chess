package server;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson gson = new Gson();
    private ConnectionManager connectionManager = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        //TODO
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> executeConnect();
            case MAKE_MOVE -> executeMakeMove();
            case LEAVE -> executeLeave();
            case RESIGN -> executeResign();
        }
    }

    private void executeConnect() {
        System.out.println("Someone connected!");
        //TODO
    }

    private void executeMakeMove() {
        //TODO
    }

    private void executeLeave() {
        //TODO
    }

    private void executeResign() {
        //TODO
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        //TODO
    }
}
