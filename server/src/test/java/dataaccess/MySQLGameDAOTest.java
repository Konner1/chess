package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import service.ClearService;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDAOTest {
    private final GameDAO dao = new MySQLGameDAO();
    private final UserDAO userDAO = new MySQLUserDAO();

    GameData testGame;

    @BeforeEach
    public void setup() throws Exception {
        new ClearService().clearApplication();
        userDAO.insertUser(new UserData("white", "pw", "white@email.com"));
        userDAO.insertUser(new UserData("black", "pw", "black@email.com"));

        testGame = new GameData(1, "white", "black", "Cool Game", new ChessGame());
    }

    @Test
    public void testInsertGamePass() throws Exception {
        assertDoesNotThrow(() -> dao.insertGame(testGame));
    }

    @Test
    public void testInsertGameFail() throws Exception {
        dao.insertGame(testGame);
        assertThrows(DataAccessException.class, () -> dao.insertGame(testGame)); // duplicate ID
    }

    @Test
    public void testListGamesPass() throws Exception {
        dao.insertGame(testGame);
        dao.insertGame(new GameData(2, "white", null, "Second Game", new ChessGame()));

        List<GameData> games = dao.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void testListGamesEmpty() throws Exception {
        List<GameData> games = dao.listGames();
        assertTrue(games.isEmpty());
    }

    @Test
    public void testGetGamePass() throws Exception {
        dao.insertGame(testGame);
        GameData result = dao.getGame(testGame.gameID());

        assertEquals(testGame.gameID(), result.gameID());
        assertEquals(testGame.gameName(), result.gameName());
        assertEquals(testGame.whiteUsername(), result.whiteUsername());
    }

    @Test
    public void testGetGameFail() {
        assertThrows(DataAccessException.class, () -> dao.getGame(9999));
    }

    @Test
    public void testUpdateGamePass() throws Exception {
        dao.insertGame(testGame);

        GameData updated = new GameData(1, "white", "black", "Renamed Game", testGame.game());
        dao.updateGame(updated);

        GameData result = dao.getGame(1);
        assertEquals("Renamed Game", result.gameName());
    }

    @Test
    public void testUpdateGameFail() {
        assertThrows(DataAccessException.class, () -> dao.updateGame(testGame)); // not inserted yet
    }

    @Test
    public void testClearGames() throws Exception {
        dao.insertGame(testGame);
        dao.clear();

        List<GameData> games = dao.listGames();
        assertTrue(games.isEmpty());
    }
}
