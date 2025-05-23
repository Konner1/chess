package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {
    private static final MemoryGameDAO INSTANCE = new MemoryGameDAO();
    public static MemoryGameDAO getInstance() { return INSTANCE; }

    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGame = 1;

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public int getNextGameID() {
        return nextGame ++;
    }

    @Override
    public void insertGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        games.put(game.gameID(),game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }
}
