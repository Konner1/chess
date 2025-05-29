package dataaccess;
import model.UserData;

import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;



public class MySQLUserDAO implements UserDAO {


    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "DELETE FROM users")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear users", e);
        }
    }


    @Override
    public void insertUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "INSERT INTO user (username, password, email) VALUES(?, ?, ?)")) {

                statement.setString(1, user.username());
                statement.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                statement.setString(3, user.email());
                statement.executeUpdate();

            }
        } catch (SQLException e) {
            throw new DataAccessException("User already exists: " + user.username());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(
                     "SELECT username, password, email FROM user WHERE username = ?")) {

            statement.setString(1, username);
            var result = statement.executeQuery();

            if (result.next()) {
                var password = result.getString("password");
                var email = result.getString("email");
                return new UserData(username, password, email);
            } else {
                throw new DataAccessException("User not found: " + username);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve user: " + username, e);
        }
    }

}
