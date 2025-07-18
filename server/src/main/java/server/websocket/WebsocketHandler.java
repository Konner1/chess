package server.websocket;

import chess.*;
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
import service.GameService;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {
    private static final Gson GSON = new Gson();
    private static final ConnectionManager CONNECTIONS = Server.CONNECTION_MANAGER;

    private final AuthDAO authDAO = new MySQLAuthDAO();
    private final GameDAO gameDAO = new MySQLGameDAO();


    @OnWebSocketMessage
    public void onMessage(Session session, String raw) {
        try {
            var base = GSON.fromJson(raw, UserGameCommand.class);
            switch (base.getCommandType()) {
                case CONNECT   -> handleConnect(session, GSON.fromJson(raw, ConnectCommand.class));
                case MAKE_MOVE -> handleMove   (session, GSON.fromJson(raw, MakeMoveCommand.class));
                case LEAVE     -> handleLeave  (session, GSON.fromJson(raw, LeaveCommand.class));
                case RESIGN    -> handleResign (session, GSON.fromJson(raw, ResignCommand.class));
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
        if (auth == null){
            throw new DataAccessException("Invalid token");
        }
        if (game == null){
            throw new DataAccessException("Game not found");
        }

        String user = auth.username();
        String white = game.whiteUsername();
        String black = game.blackUsername();

        CONNECTIONS.addConnection(cmd.getGameID(), user, session);

        session.getRemote().sendString(GSON.toJson(new LoadMessage(game.game())));

        String note;
        if (user.equals(white)){
            note = user + " joined as WHITE.";
        }
        else if (user.equals(black)){
            note = user + " joined as BLACK.";
        }
        else {
            note = user + " joined as an observer.";
        }

        CONNECTIONS.broadcast(cmd.getGameID(),
                new NotificationMessage(note),
                session);
    }

    private void handleMove(Session session, MakeMoveCommand cmd)
            throws DataAccessException, InvalidMoveException, IOException
    {
        // --- standard auth / lookup / guards ---
        AuthData auth = authDAO.getAuth(cmd.getAuthToken());
        GameData gameData = gameDAO.getGame(cmd.getGameID());
        if (auth == null)      throw new DataAccessException("Invalid auth");
        if (gameData == null)  throw new DataAccessException("Game not found");

        ChessGame game = gameData.game();
        if (game.isGameOver()) {
            throw new InvalidMoveException("Game is over");
        }

        // whose turn is it?
        ChessGame.TeamColor you = getTeam(auth.username(), gameData);
        if (you != game.getTeamTurn()) {
            throw new InvalidMoveException("Not your turn");
        }

        // record start/end and piece name for the notification
        ChessPosition start = cmd.getMove().getStartPosition();
        ChessPosition end   = cmd.getMove().getEndPosition();
        ChessPiece moved    = game.getBoard().getPiece(start);
        String pieceName    = moved.getPieceType().name().toLowerCase();
        String from         = "" + (char)('a' + start.getColumn()-1) + start.getRow();
        String to           = "" + (char)('a' +   end.getColumn()-1) +   end.getRow();
        String user         = auth.username();

        game.makeMove(cmd.getMove());

        ChessGame.TeamColor defenderColor = game.getTeamTurn();

        boolean isCheckmate = game.isInCheckmate(defenderColor);

        boolean isCheck     = !isCheckmate && game.isInCheck(defenderColor);

        gameDAO.updateGame(new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game
        ));

        CONNECTIONS.broadcast(cmd.getGameID(), new LoadMessage(game));

        CONNECTIONS.broadcast(
                cmd.getGameID(),
                new NotificationMessage(user + " moved " + pieceName + " from " + from + " to " + to),
                session
        );

        if (isCheckmate) {

            game.setGameOver(true);
            gameDAO.updateGame(new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game
            ));

            CONNECTIONS.broadcast(
                    cmd.getGameID(),
                    new NotificationMessage(user + " wins by checkmate.")
            );

        } else if (isCheck) {

            String defender = defenderColor == ChessGame.TeamColor.WHITE
                    ? gameData.whiteUsername()
                    : gameData.blackUsername();

            CONNECTIONS.broadcast(
                    cmd.getGameID(),
                    new NotificationMessage(defender + " is in check.")
            );
        }
    }







    private void handleResign(Session session, ResignCommand cmd)
            throws DataAccessException, IOException
    {
        AuthData auth = authDAO.getAuth(cmd.getAuthToken());
        if (auth == null) {
            throw new DataAccessException("Invalid auth");
        }
        GameData game = gameDAO.getGame(cmd.getGameID());
        if (game == null) {
            throw new DataAccessException("Game not found");
        }

        if (game.game().isGameOver()) {
            throw new IllegalStateException("Game is already over");
        }

        String user = auth.username();
        if (!user.equals(game.whiteUsername()) && !user.equals(game.blackUsername())) {
            throw new IllegalStateException("Only players may resign");
        }

        game.game().setGameOver(true);
        gameDAO.updateGame(game);

        CONNECTIONS.broadcast(cmd.getGameID(),
                new NotificationMessage(user + " resigned."));
    }





    private void handleLeave(Session session, LeaveCommand cmd) throws DataAccessException {
        AuthData auth = authDAO.getAuth(cmd.getAuthToken());
        GameData game = gameDAO.getGame(cmd.getGameID());

        String user  = auth.username();
        String white = game.whiteUsername();
        String black = game.blackUsername();
        if (user.equals(white) || user.equals(black)) {
            try {
                new GameService().leaveGame(cmd.getAuthToken(), cmd.getGameID());
            } catch (Exception ignored) {
            }
        }

        CONNECTIONS.removeConnection(session);

        NotificationMessage note = new NotificationMessage(user + " left the game.");
        try {
            CONNECTIONS.broadcast(cmd.getGameID(), note, session);
        } catch (IOException ignore) {}
    }



    private ChessGame.TeamColor getTeam(String user, GameData g) {
        if (user.equals(g.whiteUsername())){
            return ChessGame.TeamColor.WHITE;
        }
        if (user.equals(g.blackUsername())){
            return ChessGame.TeamColor.BLACK;
        }
        return null;
    }

    private void sendError(Session session, String err) {
        try {
            session.getRemote().sendString(GSON.toJson(new ErrorMessage("Error: " + err)));
        } catch (IOException ignored) {}
    }
}


