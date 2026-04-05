package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(int gameID, Session session) {
        connections.putIfAbsent(gameID, ConcurrentHashMap.newKeySet());
        connections.get(gameID).add(session);
    }

    public void remove(int gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void broadcast(ServerMessage notification, int gameID, Session excludeSession) throws Exception {
        for (Session s : connections.get(gameID)) {
            if (s.isOpen() && !s.equals(excludeSession)) {
                String msg = gson.toJson(notification);
                s.getRemote().sendString(msg);
            }
        }
    }

}
