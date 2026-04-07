package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Session, String> sessionToUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Session, Integer> sessionToGame = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(int gameID, Session session, String username) {
        connections.putIfAbsent(gameID, ConcurrentHashMap.newKeySet());
        connections.get(gameID).add(session);
        sessionToUser.put(session, username);
        sessionToGame.put(session, gameID);
    }

    public void remove(Session session) {
        for (Set<Session> set : connections.values()) {
            set.remove(session);
        }
        sessionToUser.remove(session);
        sessionToGame.remove(session);
    }

    public void broadcast(ServerMessage notification, int gameID, Session excludeSession) throws Exception {
        var set = connections.get(gameID);
        if (set == null) {
            return;
        }
        for (Session s : set) {
            if (s.isOpen() && !s.equals(excludeSession)) {
                String msg = gson.toJson(notification);
                s.getRemote().sendString(msg);
            }
        }
    }

    public String getUser(Session session) {
        return sessionToUser.get(session);
    }

    public Integer getGame(Session session) {
        return sessionToGame.get(session);
    }

}
