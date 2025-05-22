package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    @BeforeEach
    public void setup() throws Exception {
        new ClearService().clearApplication(); // start with a clean slate
        MemoryUserDAO.getInstance().insertUser(new UserData("alice", "pass", "a@b.com"));
    }

    @Test
    public void testClearAllSuccess() throws Exception {
        assertNotNull(MemoryUserDAO.getInstance().getUser("alice"));

        new ClearService().clearApplication();

        assertNull(MemoryUserDAO.getInstance().getUser("alice"));
        assertEquals(0, MemoryGameDAO.getInstance().listGames().size());
    }
}
