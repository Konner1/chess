// server/websocket/ConnectionManager.java
package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class ConnectionManager {
    private final ConcurrentHashMap<Integer, CopyOnWriteArrayList<Connection>> gameConnections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void addConnection(int gameID, String username, Session session) {
        var list = gameConnections.computeIfAbsent(gameID, id -> new CopyOnWriteArrayList<>());
        list.add(new Connection(username, session, gameID));
    }

    public void removeConnection(Session session) {
        gameConnections.values().forEach(list -> list.removeIf(c -> c.getSession().equals(session)));
    }

    public void broadcast(int gameID, ServerMessage msg) throws IOException {
        broadcast(gameID, msg, null);
    }

    public void broadcast(int gameID, ServerMessage msg, Session exclude) throws IOException {
        var list = gameConnections.get(gameID);
        if (list == null){
            return;
        }

        String json = gson.toJson(msg);
        for (Connection c : list) {
            var s = c.getSession();
            if (!s.isOpen()) {
                removeConnection(s);
            } else if (exclude == null || !s.equals(exclude)) {
                c.send(json);
            }
        }
    }

    private Connection findBySession(Session session) {
        return gameConnections.values().stream()
                .flatMap(l -> l.stream())
                .filter(c -> c.getSession().equals(session))
                .findFirst().orElse(null);
    }

    public String getUsername(Session session) {
        var c = findBySession(session);
        return c != null ? c.getUsername() : null;
    }

    public Integer getGameID(Session session) {
        var c = findBySession(session);
        return c != null ? c.getGameID() : null;
    }

    public void removeGame(int gameID) throws IOException {
        var list = gameConnections.remove(gameID);
        if (list == null) {
            return;
        }
        for (Connection c : list) {
            c.getSession().close();
        }
    }
}
