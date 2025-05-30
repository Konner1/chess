package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTest {

    private final UserDAO dao = new MySQLUserDAO();
    private final UserData testUser = new UserData("konner", "password", "k@gmail.com");

    @BeforeEach
    public void setup() throws Exception {
        new ClearService().clearApplication();  // ensures DB is clean
    }

    @Test
    public void testInsertUserPass() throws Exception {
        assertDoesNotThrow(() -> dao.insertUser(testUser));
    }

    @Test
    public void testInsertUserFail() throws Exception {
        dao.insertUser(testUser);
        assertThrows(DataAccessException.class, () -> dao.insertUser(testUser));  // duplicate user
    }

    @Test
    public void testGetUserPass() throws Exception {
        dao.insertUser(testUser);
        UserData found = dao.getUser("konner");

        assertEquals(testUser.username(), found.username());
        assertEquals(testUser.email(), found.email());
        assertNotNull(found.password());  // should be hashed
        assertNotEquals("password", found.password());  // definitely shouldn't match raw
    }

    @Test
    public void testGetUserFail() throws Exception {
        UserData found = dao.getUser("ghost");
        assertNull(found);
    }

    @Test
    public void testClearUserTable() throws Exception {
        dao.insertUser(testUser);
        dao.clear();
        assertNull(dao.getUser("konner"));
    }
}
