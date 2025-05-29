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

    public List<Map<String, Object>> listGames(String authToken) throws Exception {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }

        var allGames = gameDAO.listGames();

        List<Map<String, Object>> result = new ArrayList<>();
        for (var game : allGames) {
            Map<String, Object> map = new HashMap<>();
            map.put("gameID", game.gameID());
            map.put("whiteUsername", game.whiteUsername());
            map.put("blackUsername", game.blackUsername());
            map.put("gameName", game.gameName());
            result.add(map);
        }

        return result;
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

}
