package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {
    private static final MemoryGameDAO instance = new MemoryGameDAO();
    public static MemoryGameDAO getInstance() { return instance; }

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
}
