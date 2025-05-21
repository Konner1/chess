package server;

import spark.Request;
import spark.Response;
import spark.Route;
import model.GameData;
import service.GameService;
import java.util.Map;

public class GameHandler extends BaseHandler {
    public Route createGame = (Request req, Response res) -> {
        try {
            String authToken = req.headers("Authorization");

            GameData request = gson.fromJson(req.body(), GameData.class);
            GameData result = new GameService().createGame(request.gameName(), authToken);

            return success(res, Map.of("gameID", result.gameID()));
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg.contains("unauthorized")) return error(res, 401, msg);
            if (msg.contains("bad request")) return error(res, 400, msg);
            return error(res, 500, msg);
        }
    };
}

