package service;

import dataaccess.*;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.AuthDAO;
import model.GameData;
import chess.ChessGame;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;


public class GameService {
    private final GameDAO gameDAO = MemoryGameDAO.getInstance();
    private final AuthDAO authDAO = MemoryAuthDAO.getInstance();

    public GameData createGame(String gameName, String authToken) throws Exception {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }

        if (gameName == null || gameName.isBlank()) {
            throw new Exception("bad request");
        }

        int gameID = gameDAO.getNextGameID();

        GameData game = new GameData(gameID, null, null, gameName);
        gameDAO.insertGame(game);

        return game;
    }

    public List<Map<String, Object>> listGames(String authToken) throws Exception {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }

        var allGames = gameDAO.listGames();

        // Map each GameData to a trimmed version
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
}
