package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private static MemoryAuthDAO instance = new MemoryAuthDAO();
    public static MemoryAuthDAO getINSTANCE() { return instance; }

    private final Map<String, AuthData> tokens = new HashMap<>();

    @Override
    public void clear() {
        tokens.clear();
    }

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        tokens.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        return tokens.get(token);
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        tokens.remove(token);
    }
}
