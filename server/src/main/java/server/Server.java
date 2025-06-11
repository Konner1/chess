package server;

import spark.*;
import dataaccess.*;
import server.websocket.WebsocketHandler;
import server.websocket.ConnectionManager;

public class Server {

    public static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();


    public int run(int desiredPort) {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to initialize database", ex);
        }

        Spark.port(desiredPort);
        Spark.webSocket("/ws", WebsocketHandler.class);

        Spark.staticFiles.location("web");

        // register your endpoints and handle exceptions here.

        Spark.delete("/db", new ClearHandler());

        UserHandler userHandler = new UserHandler();

        Spark.post("/user", userHandler.register);
        Spark.post("/session", userHandler.login);
        Spark.delete("/session", userHandler.logout);


        GameHandler gameHandler = new GameHandler();

        Spark.post("/game", gameHandler.createGame);
        Spark.get("/game", gameHandler.listGames);
        Spark.put("/game", gameHandler.joinGame);
        Spark.put("/observe", gameHandler.observeGame);



        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
