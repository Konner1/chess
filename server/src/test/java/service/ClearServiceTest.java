package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    @BeforeEach
    public void setup() throws Exception {
        new ClearService().clearApplication();
        MemoryUserDAO.getInstance().insertUser(new UserData("konner", "pass", "k@gmail.com"));
    }

    @Test
    public void testClearAllSuccess() throws Exception {
        assertNotNull(MemoryUserDAO.getInstance().getUser("konner"));

        new ClearService().clearApplication();

        assertNull(MemoryUserDAO.getInstance().getUser("konner"));
        assertEquals(0, MemoryGameDAO.getInstance().listGames().size());
    }
}
