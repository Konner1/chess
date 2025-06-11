// server/websocket/Connection.java
package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;

/**
 * Holds one client’s session plus which game they’re in.
 */
public class Connection {
    private final String username;
    private final Session session;
    private final int    gameID;

    public Connection(String username, Session session, int gameID) {
        this.username = username;
        this.session  = session;
        this.gameID   = gameID;
    }

    public String   getUsername() { return username; }
    public Session  getSession()  { return session;  }
    public int      getGameID()   { return gameID;   }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
