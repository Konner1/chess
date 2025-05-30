package dataaccess;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MySQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDAOTest {
    AuthDAO dao;
    AuthData defaultAuth;

    @BeforeEach
    void setUp() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        dao = new MySQLAuthDAO();

        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("DELETE FROM auth")) {
                statement.executeUpdate();
            }
        }

        defaultAuth = new AuthData("test-token", "test-user");
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("DELETE FROM auth")) {
                statement.executeUpdate();
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void insertAuthPositive() throws DataAccessException, SQLException {
        dao.insertAuth(defaultAuth);

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT token, username FROM auth WHERE token = ?")) {
            statement.setString(1, defaultAuth.authToken());
            try (var results = statement.executeQuery()) {
                assertTrue(results.next());
                assertEquals(defaultAuth.authToken(), results.getString("token"));
                assertEquals(defaultAuth.username(), results.getString("username"));
            }
        }
    }

    @Test
    void insertAuthNegative() throws DataAccessException {
        dao.insertAuth(defaultAuth);
        assertThrows(DataAccessException.class, () -> dao.insertAuth(defaultAuth));
    }

    @Test
    void deleteAuthPositive() throws DataAccessException, SQLException {
        dao.insertAuth(defaultAuth);
        dao.deleteAuth(defaultAuth.authToken());

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT token FROM auth WHERE token = ?")) {
            statement.setString(1, defaultAuth.authToken());
            try (var results = statement.executeQuery()) {
                assertFalse(results.next());
            }
        }
    }

    @Test
    void deleteAuthNegative() {

        assertDoesNotThrow(() -> dao.deleteAuth("nonexistent-token"));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        dao.insertAuth(defaultAuth);
        AuthData result = dao.getAuth(defaultAuth.authToken());
        assertEquals(defaultAuth, result);
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        AuthData result = dao.getAuth("nonexistent-token");
        assertNull(result);
    }

    @Test
    void clearPositive() throws DataAccessException, SQLException {
        dao.insertAuth(defaultAuth);
        dao.clear();

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT token FROM auth WHERE token = ?")) {
            statement.setString(1, defaultAuth.authToken());
            try (var results = statement.executeQuery()) {
                assertFalse(results.next());
            }
        }
    }
}
