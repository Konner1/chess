package dataaccess;

public class DataAccess {
    private static final UserDAO userDAO = MemoryUserDAO.getInstance();
    private static final GameDAO gameDAO = MemoryGameDAO.getInstance();
    private static final AuthDAO authDAO = MemoryAuthDAO.getInstance();

    public static void clearAll() throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

    public static UserDAO userDAO() { return userDAO; }
    public static GameDAO gameDAO() { return gameDAO; }
    public static AuthDAO authDAO() { return authDAO; }
}