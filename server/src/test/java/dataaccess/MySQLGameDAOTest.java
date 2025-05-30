package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import service.ClearService;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDAOTest {
    private final GameDAO gamedao = new MySQLGameDAO();
    private final UserDAO userDAO = new MySQLUserDAO();

    GameData testGame;

    @BeforeEach
    public void setup() throws Exception {
        new ClearService().clearApplication();
        userDAO.insertUser(new UserData("konner", "pass", "konner@gmail.com"));
        userDAO.insertUser(new UserData("connor", "pass", "connor@gmail.com"));

        testGame = new GameData(1, "konner", "connor", "Cool Game", new ChessGame());
    }

    @Test
    public void testInsertGamePass() throws Exception {
        assertDoesNotThrow(() -> gamedao.insertGame(testGame));
    }

    @Test
    public void testInsertGameFail() throws Exception {
        gamedao.insertGame(testGame);
        assertThrows(DataAccessException.class, () -> gamedao.insertGame(testGame)); // duplicate ID
    }

    @Test
    public void testListGamesPass() throws Exception {
        gamedao.insertGame(testGame);
        gamedao.insertGame(new GameData(2, "konner", null, "Second Game", new ChessGame()));

        List<GameData> games = gamedao.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void testListGamesEmpty() throws Exception {
        List<GameData> games = gamedao.listGames();
        assertTrue(games.isEmpty());
    }

    @Test
    public void testGetGamePass() throws Exception {
        gamedao.insertGame(testGame);
        GameData result = gamedao.getGame(testGame.gameID());

        assertEquals(testGame.gameID(), result.gameID());
        assertEquals(testGame.gameName(), result.gameName());
        assertEquals(testGame.whiteUsername(), result.whiteUsername());
    }

    @Test
    public void testGetGameFail() {
        assertThrows(DataAccessException.class, () -> gamedao.getGame(9999));
    }

    @Test
    public void testUpdateGamePass() throws Exception {
        gamedao.insertGame(testGame);

        GameData updated = new GameData(1, "konner", "connor", "Renamed Game", testGame.game());
        gamedao.updateGame(updated);

        GameData result = gamedao.getGame(1);
        assertEquals("Renamed Game", result.gameName());
    }

    @Test
    public void testUpdateGameFail() {
        assertThrows(DataAccessException.class, () -> gamedao.updateGame(testGame)); // not inserted yet
    }

    @Test
    public void testClearGames() throws Exception {
        gamedao.insertGame(testGame);
        gamedao.clear();

        List<GameData> games = gamedao.listGames();
        assertTrue(games.isEmpty());
    }
}
