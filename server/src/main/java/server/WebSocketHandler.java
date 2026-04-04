package server;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    
    @Override
    public void handleConnect(WsConnectContext ctx) {

    }

    @Override
    public void handleMessage(WsMessageContext ctx) {

    }

    @Override
    public void handleClose(WsCloseContext ctx) {

    }
}
