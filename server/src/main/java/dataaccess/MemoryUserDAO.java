package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private static final MemoryUserDAO instance = new MemoryUserDAO();
    public static MemoryUserDAO getInstance() { return instance; }

    private static final Map<String, UserData> users = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
    }
}