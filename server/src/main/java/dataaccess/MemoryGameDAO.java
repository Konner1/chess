package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private static final MemoryGameDAO instance = new MemoryGameDAO();
    public static MemoryGameDAO getInstance() { return instance; }

    private static final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        games.clear();
    }
}
