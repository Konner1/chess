// server/websocket/WebsocketHandler.java
package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import server.Server;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {
    private static final Gson gson = new Gson();
    private static final ConnectionManager connections = Server.connectionManager;

    // use your instance DAOs, not static calls
    private final AuthDAO authDAO = new MySQLAuthDAO();
    private final GameDAO gameDAO = new MySQLGameDAO();

    @OnWebSocketConnect
    public void onConnect(Session session) { /* wait for CONNECT msg */ }

    @OnWebSocketClose
    public void onClose(Session session, int status, String reason) {
        connections.removeConnection(session);
    }
    @OnWebSocketError
    public void onError(Session session, Throwable cause) {
        cause.printStackTrace();
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String raw) {
        try {
            var base = gson.fromJson(raw, UserGameCommand.class);
            switch (base.getCommandType()) {
                case CONNECT   -> handleConnect(session, gson.fromJson(raw, ConnectCommand.class));
                case MAKE_MOVE -> handleMove   (session, gson.fromJson(raw, MakeMoveCommand.class));
                case LEAVE     -> handleLeave  (session, gson.fromJson(raw, LeaveCommand.class));
                case RESIGN    -> handleResign (session, gson.fromJson(raw, ResignCommand.class));
                default        -> sendError(session, "Unknown command");
            }
        } catch (Exception e) {
            sendError(session, e.getMessage());
        }
    }

    private void handleConnect(Session session, ConnectCommand cmd)
            throws DataAccessException, IOException
    {
        AuthData auth = authDAO.getAuth(cmd.getAuthToken());
        GameData game = gameDAO.getGame(cmd.getGameID());
        if (auth == null) throw new DataAccessException("Invalid token");
        if (game == null) throw new DataAccessException("Game not found");

        String user = auth.username();
        String white = game.whiteUsername();
        String black = game.blackUsername();

        // register connection
        connections.addConnection(cmd.getGameID(), user, session);

        // send current board to just this client
        session.getRemote().sendString(gson.toJson(new LoadMessage(game.game())));

        // build the correct notification
        String note;
        if (user.equals(white))       note = user + " joined as WHITE.";
        else if (user.equals(black))  note = user + " joined as BLACK.";
        else                           note = user + " joined as an observer.";

        // broadcast to everyone else
        connections.broadcast(cmd.getGameID(),
                new NotificationMessage(note),
                session);
    }

    private void handleMove(Session session, MakeMoveCommand cmd)
            throws DataAccessException, InvalidMoveException, IOException
    {
        AuthData auth = authDAO.getAuth(cmd.getAuthToken());
        GameData game = gameDAO.getGame(cmd.getGameID());
        if (auth == null) throw new DataAccessException("Invalid auth");
        if (game == null) throw new DataAccessException("Game not found");

        // ← NEW: if the game is already over, error out immediately
        if (game.game().isGameOver()) {
            throw new InvalidMoveException("Game is over");
        }

        // enforce turn order
        ChessGame.TeamColor side = getTeam(auth.username(), game);
        if (side != game.game().getTeamTurn()) {
            throw new InvalidMoveException("Not your turn");
        }

        // apply move and persist
        game.game().makeMove(cmd.getMove());
        gameDAO.updateGame(game);

        // broadcast updated board to everyone
        connections.broadcast(cmd.getGameID(), new LoadMessage(game.game()));

        // broadcast notification to everyone *except* the mover
        connections.broadcast(
                cmd.getGameID(),
                new NotificationMessage(auth.username() + " made a move."),
                session
        );
    }



    private void handleResign(Session session, ResignCommand cmd)
            throws DataAccessException, IOException
    {
        AuthData auth = authDAO.getAuth(cmd.getAuthToken());
        GameData game = gameDAO.getGame(cmd.getGameID());
        if (auth == null) throw new DataAccessException("Invalid auth");
        if (game == null) throw new DataAccessException("Game not found");

        String user = auth.username();
        String white = game.whiteUsername();
        String black = game.blackUsername();
        // ← NEW: only the white or black player may resign
        if (!user.equals(white) && !user.equals(black)) {
            throw new IllegalStateException("Only players may resign");
        }

        // now safe to end the game
        game.game().setGameOver(true);
        gameDAO.updateGame(game);

        connections.broadcast(
                cmd.getGameID(),
                new NotificationMessage(user + " resigned.")
        );
    }


    private void handleLeave(Session session, LeaveCommand cmd) throws IOException {
        String user = connections.getUsername(session);
        Integer gid = connections.getGameID(session);
        connections.removeConnection(session);
        connections.broadcast(gid,
                new NotificationMessage(user + " left the game."));
    }

    private ChessGame.TeamColor getTeam(String user, GameData g) {
        if (user.equals(g.whiteUsername())) return ChessGame.TeamColor.WHITE;
        if (user.equals(g.blackUsername())) return ChessGame.TeamColor.BLACK;
        return null;
    }

    private void sendError(Session session, String err) {
        try {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + err)));
        } catch (IOException ignored) {}
    }
}


