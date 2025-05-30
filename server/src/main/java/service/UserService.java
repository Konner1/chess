package service;

import dataaccess.*;
import model.UserData;
import model.AuthData;

import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDAO = new MySQLUserDAO();
    private final AuthDAO authDAO = new MySQLAuthDAO();

    public AuthData register(UserData user) throws Exception {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new Exception("bad request");
        }

        if (userDAO.getUser(user.username()) != null) {
            throw new Exception("already taken");
        }

        userDAO.insertUser(user);

        String token = UUID.randomUUID().toString();
        authDAO.insertAuth(new AuthData(user.username(),token));

        return new AuthData(user.username(), token);
    }

    public AuthData login(UserData user) throws Exception {
        if (user.username() == null || user.password() == null) {
            throw new DataAccessException("bad request");
        }

        UserData storedUser = userDAO.getUser(user.username());
        if ((storedUser == null) || !BCrypt.checkpw(user.password(), storedUser.password())) {
            throw new Exception("unauthorized");
        }

        String token = UUID.randomUUID().toString();
        authDAO.insertAuth(new AuthData(user.username(),token));

        return new AuthData(user.username(), token);
    }

    public void logout(String authToken) throws Exception {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }

        authDAO.deleteAuth(authToken);
    }
}
