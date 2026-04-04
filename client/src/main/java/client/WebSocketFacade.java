package client;

import jakarta.websocket.*;

import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;

    public void connect(String url) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, new URI(url));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

}
