package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTest {

    private final UserDAO userdao = new MySQLUserDAO();
    private final UserData testUser = new UserData("konner", "password", "k@gmail.com");

    @BeforeEach
    public void setup() throws Exception {
        new ClearService().clearApplication();
    }

    @Test
    public void testInsertUserPass() throws Exception {
        assertDoesNotThrow(() -> userdao.insertUser(testUser));
    }

    @Test
    public void testInsertUserFail() throws Exception {
        userdao.insertUser(testUser);
        assertThrows(DataAccessException.class, () -> userdao.insertUser(testUser));
    }

    @Test
    public void testGetUserPass() throws Exception {
        userdao.insertUser(testUser);
        UserData found = userdao.getUser("konner");

        assertEquals(testUser.username(), found.username());
        assertEquals(testUser.email(), found.email());
        assertNotNull(found.password());
        assertNotEquals("password", found.password());
    }

    @Test
    public void testGetUserFail() throws Exception {
        UserData found = userdao.getUser("ghost");
        assertNull(found);
    }

    @Test
    public void testClearUserTable() throws Exception {
        userdao.insertUser(testUser);
        userdao.clear();
        assertNull(userdao.getUser("konner"));
    }
}
