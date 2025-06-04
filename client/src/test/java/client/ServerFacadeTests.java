package client;

import facade.ServerFacade;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    private String authToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() throws Exception {
        facade.clear();
    }

    @Test
    public void registerPass() throws Exception {
        AuthData auth = facade.register("konner", "pass", "konner@email.com");
        assertNotNull(auth.authToken());
        authToken = auth.authToken();
    }

    @Test
    public void registerFail() throws Exception {
        facade.register("konner", "pass", "konner@email.com");
        assertThrows(Exception.class, () -> facade.register("konner", "pass", "konner@email.com"));
    }

    @Test
    public void loginPass() throws Exception {
        facade.register("konner", "pass", "konner@email.com");
        AuthData auth = facade.login("konner", "pass");
        assertNotNull(auth.authToken());
    }

    @Test
    public void loginFail() {
        assertThrows(Exception.class, () -> facade.login("ghost", "wrong"));
    }

    @Test
    public void logoutPass() throws Exception {
        AuthData auth = facade.register("konner", "pass", "konner@email.com");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    public void logoutFail() {
        assertThrows(Exception.class, () -> facade.logout("invalid-token"));
    }

    @Test
    public void createGamePass() throws Exception {
        AuthData auth = facade.register("konner", "pass", "konner@email.com");
        GameData game = facade.createGame("MyGame", auth.authToken());
        assertNotNull(game);
        assertEquals("MyGame", game.gameName());
    }

    @Test
    public void createGameFail() {
        assertThrows(Exception.class, () -> facade.createGame("My Game", "invalid-token"));
    }

    @Test
    public void listGamesPass() throws Exception {
        AuthData auth = facade.register("konner", "pass", "konner@email.com");
        facade.createGame("Game1", auth.authToken());
        GameData[] games = facade.listGames(auth.authToken());
        assertTrue(games.length >= 1);
    }

    @Test
    public void listGamesFail() {
        assertThrows(Exception.class, () -> facade.listGames("invalid-token"));
    }

    @Test
    public void joinGamePass() throws Exception {
        AuthData auth = facade.register("konner", "pass", "konner@email.com");
        GameData game = facade.createGame("Joinable", auth.authToken());
        assertDoesNotThrow(() -> facade.joinGame(game.gameID(), "WHITE", auth.authToken()));
    }

    @Test
    public void joinGameFail() {
        assertThrows(Exception.class, () -> facade.joinGame(9999, "WHITE", "invalid-token"));
    }

    @Test
    public void observeGamePass() throws Exception {
        AuthData auth = facade.register("konner", "pass", "konner@email.com");
        GameData game = facade.createGame("Observable", auth.authToken());
        assertDoesNotThrow(() -> facade.observeGame(game.gameID(), auth.authToken()));
    }

    @Test
    public void observeGameFail() {
        assertThrows(Exception.class, () -> facade.observeGame(9999, "invalid-token"));
    }

    @Test
    public void clearPass() {
        assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    public void clearIdempotent() throws Exception {
        facade.clear();
        assertDoesNotThrow(() -> facade.clear());
    }
}

