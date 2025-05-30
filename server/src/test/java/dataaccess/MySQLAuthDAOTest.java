package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDAOTest {

    private final AuthDAO dao = new MySQLAuthDAO();
    private final UserDAO userDAO = new MySQLUserDAO(); // Needed to satisfy foreign key constraint
    private final AuthData testAuth = new AuthData("auth-token", "test-user");

    @BeforeEach
    public void setup() throws Exception {
        new ClearService().clearApplication();
        // Insert corresponding user to satisfy FK constraint
        userDAO.insertUser(new UserData("test-user", "pw", "email@test.com"));
    }

    @Test
    public void testInsertAuthPass() throws Exception {
        assertDoesNotThrow(() -> dao.insertAuth(testAuth));
    }

    @Test
    public void testInsertAuthFail() throws Exception {
        dao.insertAuth(testAuth);
        assertThrows(DataAccessException.class, () -> dao.insertAuth(testAuth)); // duplicate token
    }

    @Test
    public void testGetAuthPass() throws Exception {
        dao.insertAuth(testAuth);
        AuthData result = dao.getAuth("auth-token");
        assertEquals(testAuth, result);
    }

    @Test
    public void testGetAuthFail() throws Exception {
        AuthData result = dao.getAuth("nonexistent-token");
        assertNull(result);
    }

    @Test
    public void testDeleteAuthPass() throws Exception {
        dao.insertAuth(testAuth);
        dao.deleteAuth("auth-token");
        assertNull(dao.getAuth("auth-token"));
    }

    @Test
    public void testDeleteAuthFail() {
        assertDoesNotThrow(() -> dao.deleteAuth("nonexistent-token"));
    }

    @Test
    public void testClearAuthTable() throws Exception {
        dao.insertAuth(testAuth);
        dao.clear();
        assertNull(dao.getAuth("auth-token"));
    }
}
