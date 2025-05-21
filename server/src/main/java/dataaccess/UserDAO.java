package dataaccess;
import model.UserData;

public interface UserDAO {
    void clear() throws DataAccessException;

    void insertUser(UserData auth) throws DataAccessException;

    UserData getUser(String token) throws DataAccessException;
}