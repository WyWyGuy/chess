package server;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.putIfAbsent(gameID, ConcurrentHashMap.newKeySet());
        connections.get(gameID).add(session);
    }

    public void remove(int gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void broadcast(String message, int gameID, Session excludeSession) throws Exception {
        for (Session s : connections.get(gameID)) {
            if (s.isOpen() && !s.equals(excludeSession)) {
                s.getRemote().sendString(message);
            }
        }
    }

}
