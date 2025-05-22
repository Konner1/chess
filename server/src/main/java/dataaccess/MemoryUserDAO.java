package dataaccess;
import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private static final MemoryUserDAO INSTANCE = new MemoryUserDAO();
    public static MemoryUserDAO getInstance() { return INSTANCE; }

    private static final Map<String, UserData> USERS = new HashMap<>();

    @Override
    public void clear() {
        USERS.clear();
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (USERS.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        USERS.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return USERS.get(username);
    }

}