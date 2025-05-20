package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccess;

public class ClearService {
    public void clearApplication() throws DataAccessException {
        DataAccess.clearAll();  // simple and centralized
    }
}