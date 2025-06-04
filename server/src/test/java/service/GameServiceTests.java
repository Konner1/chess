package service;

import model.AuthData;
import model.UserData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private final GameService gameService = new GameService();
    private final UserService userService = new UserService();
    private String authToken;

    @BeforeEach
    public void setup() throws Exception {
        new ClearService().clearApplication();
        UserData user = new UserData("konner", "password", "k@gmail.com");
        AuthData auth = userService.register(user);
        this.authToken = auth.authToken();
    }

    @Test
    public void testCreateGamePass() throws Exception {
        GameData game = gameService.createGame("test", authToken);
        assertEquals("test", game.gameName());
        assertNotEquals(0, game.gameID());
    }

//    @Test
//    public void testListGamePass() throws Exception {
//        GameData game = gameService.createGame("test", authToken);
//        var games = gameService.listGames(authToken);
//
//        assertEquals(1, games.size());
//        var gameInfo = games.get(0);
//
//        assertEquals("test", gameInfo.get("gameName"));
//        assertNotNull(gameInfo.get("gameID"));
//    }

    @Test
    public void testJoinGamePass() throws Exception {
        GameData game = gameService.createGame("JoinTest", authToken);

        assertDoesNotThrow(() -> gameService.joinGame(authToken, "WHITE", game.gameID()));
    }

    @Test
    public void testCreateGameFail() {
        assertThrows(Exception.class, () -> gameService.createGame(null, authToken));
        assertThrows(Exception.class, () -> gameService.createGame("GameName", null));
    }

    @Test
    public void testListGameFail() {
        assertThrows(Exception.class, () -> gameService.listGames(null));
    }

    @Test
    public void testJoinGameFail() throws Exception {
        GameData game = gameService.createGame("failGame", authToken);

        assertThrows(Exception.class, () -> gameService.joinGame(null, "WHITE", game.gameID()));
        assertThrows(Exception.class, () -> gameService.joinGame(authToken, null, game.gameID()));

    }


}
