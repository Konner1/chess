package service;

import dataaccess.*;
import model.*;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.AuthDAO;
import model.GameData;
import chess.ChessGame;


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

        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDAO.insertGame(game);

        return game;
    }

}
