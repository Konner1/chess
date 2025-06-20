package service;

import dataaccess.*;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.AuthDAO;
import model.GameData;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import chess.ChessGame;


public class GameService {
    private final GameDAO gameDAO = new MySQLGameDAO();
    private final AuthDAO authDAO = new MySQLAuthDAO();

    public GameData createGame(String gameName, String authToken) throws Exception {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }

        if (gameName == null || gameName.isBlank()) {
            throw new Exception("bad request");
        }

        int gameID = gameDAO.getNextGameID();
        ChessGame game = new ChessGame();

        GameData gamedata = new GameData(gameID, null, null, gameName, game);
        gameDAO.insertGame(gamedata);

        return gamedata;
    }

    public GameData[] listGames(String authToken) throws Exception {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }

        List<GameData> allGames = gameDAO.listGames();
        return allGames.toArray(new GameData[0]);
    }

    public void joinGame(String authToken, String color, int gameID) throws Exception {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }

        if (color == null || (!color.equals("WHITE") && !color.equals("BLACK"))) {
            throw new Exception("bad request");
        }

        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new Exception("bad request");
        }

        String username = authDAO.getAuth(authToken).username();

        if (color.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new Exception("already taken");
            }
            game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            if (game.blackUsername() != null) {
                throw new Exception("already taken");
            }
            game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }

        gameDAO.updateGame(game);
    }
    public void observeGame(String authToken, int gameID) throws Exception {

        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }

        if (gameDAO.getGame(gameID) == null) {
            throw new Exception("bad request");
        }
    }

    public void leaveGame(String authToken, int gameID) throws Exception {

        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }

        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new Exception("bad request");
        }
        String user = authDAO.getAuth(authToken).username();

        String white = game.whiteUsername();
        String black = game.blackUsername();
        String newWhite = white, newBlack = black;

        if (user.equals(white)) {
            newWhite = null;
        } else if (user.equals(black)) {
            newBlack = null;
        } else {
            throw new Exception("not in game");
        }

        GameData updated = new GameData(
                game.gameID(),
                newWhite,
                newBlack,
                game.gameName(),
                game.game()
        );
        gameDAO.updateGame(updated);
    }


}
