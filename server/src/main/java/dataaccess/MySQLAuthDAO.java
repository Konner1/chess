package dataaccess;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;


public class MySQLAuthDAO implements AuthDAO{
    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "DELETE FROM auth")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear auth", e);
        }
    }

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO auth (token, username) VALUES(?, ?)")) { // is it supposed to be username, token?
                statement.setString(1, auth.username());
                statement.setString(2, auth.authToken());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed insert auth", e);
        }
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(
                     "SELECT username, password FROM auth WHERE token = ?")) {

            statement.setString(1, token);
            var result = statement.executeQuery();

            if (result.next()) {
                var username = result.getString("username");
                return new AuthData(token, username);
            } else {
                throw new DataAccessException("Auth not found: " + token);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve auth: " + token, e);
        }
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(
                     "DELETE FROM auth WHERE token = ?")){
                statement.setString(1,token);
                statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth: " + token, e);
        }
    }
}
