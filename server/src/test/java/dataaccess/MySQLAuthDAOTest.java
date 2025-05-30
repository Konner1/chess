package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDAOTest {

    private final AuthDAO authdao = new MySQLAuthDAO();
    private final UserDAO userDAO = new MySQLUserDAO();
    private final AuthData testAuth = new AuthData("konner", "auth-token");

    @BeforeEach
    public void setup() throws Exception {
        new ClearService().clearApplication();
        userDAO.insertUser(new UserData("konner", "pass", "k@gmail.com"));
    }

    @Test
    public void testInsertAuthPass() throws Exception {
        authdao.insertAuth(testAuth);
        AuthData result = authdao.getAuth("auth-token");
        assertNotNull(result);
        assertEquals(testAuth.username(), result.username());
        assertEquals(testAuth.authToken(), result.authToken());
    }

    @Test
    public void testInsertAuthFail() throws Exception {
        authdao.insertAuth(testAuth);
        assertThrows(DataAccessException.class, () -> authdao.insertAuth(testAuth));
    }

    @Test
    public void testGetAuthPass() throws Exception {
        authdao.insertAuth(testAuth);
        AuthData result = authdao.getAuth("auth-token");
        assertNotNull(result);
        assertEquals(testAuth.username(), result.username());
        assertEquals(testAuth.authToken(), result.authToken());
    }

    @Test
    public void testGetAuthFail() throws Exception {
        AuthData result = authdao.getAuth("nonexistent-token");
        assertNull(result);
    }

    @Test
    public void testDeleteAuthPass() throws Exception {
        authdao.insertAuth(testAuth);
        authdao.deleteAuth("auth-token");
        AuthData result = authdao.getAuth("auth-token");
        assertNull(result);
    }

    @Test
    public void testDeleteAuthFail() {
        assertDoesNotThrow(() -> authdao.deleteAuth("nonexistent-token"));
    }

    @Test
    public void testClearAuthTable() throws Exception {
        authdao.insertAuth(testAuth);
        authdao.clear();
        AuthData result = authdao.getAuth("auth-token");
        assertNull(result);
    }
}
