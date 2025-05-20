package server;

import com.google.gson.Gson;
import model.ErrorResponse;
import service.ClearService;
import spark.Spark;

public class Server {
    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.delete("/db", (req, res) -> {
            try {
                new ClearService().clearApplication();
                res.status(200);
                return "{}";
            } catch (Exception e) {
                res.status(500);
                return new Gson().toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        });

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
