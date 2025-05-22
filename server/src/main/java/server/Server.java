package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

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
