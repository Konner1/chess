package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private static final MemoryAuthDAO instance = new MemoryAuthDAO();
    public static MemoryAuthDAO getInstance() { return instance; }

    private static final Map<String, AuthData> tokens = new HashMap<>();

    @Override
    public void clear() {
        tokens.clear();
    }
}
