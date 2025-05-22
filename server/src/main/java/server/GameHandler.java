package server;

import spark.Request;
import spark.Response;
import spark.Route;
import model.GameData;
import service.GameService;
import java.util.Map;
import java.util.List;

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

    public Route listGames = (Request req, Response res) -> {
        try {
            String authToken = req.headers("Authorization");
            System.out.println("Calling listGames()");
            List<Map<String, Object>> games = new GameService().listGames(authToken);
            System.out.println("Games returned: " + games.size());
            return success(res, Map.of("games", games));
        } catch (Exception e) {
            String msg = e.getMessage(); // <-- this could be null
            if (msg != null && msg.contains("unauthorized")) return error(res, 401, msg);
            return error(res, 500, msg != null ? msg : "internal server error");
        }
    };
}

