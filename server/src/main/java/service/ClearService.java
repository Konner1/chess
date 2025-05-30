package service;

import dataaccess.*;

public class ClearService {
    private final UserDAO userDAO = new MySQLUserDAO();
    private final GameDAO gameDAO = new MySQLGameDAO();
    private final AuthDAO authDAO = new MySQLAuthDAO();
    public void clearApplication() throws DataAccessException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }
}
