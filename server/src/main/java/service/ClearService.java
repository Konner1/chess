package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthDAO;

public class ClearService {
    public void clearApplication() throws DataAccessException {
        MemoryUserDAO.getInstance().clear();
        MemoryGameDAO.getInstance().clear();
        MemoryAuthDAO.getINSTANCE().clear();
    }
}
