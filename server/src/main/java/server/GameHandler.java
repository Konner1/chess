package server;

import spark.Request;
import spark.Response;
import spark.Route;
import model.GameData;
import service.GameService;
import java.util.Map;
import java.util.List;

public class GameHandler extends BaseHandler {

    public Route createGame = (Request req, Response res) -> handleSafely(req, res, () -> {
        String authToken = req.headers("Authorization");
        GameData request = gson.fromJson(req.body(), GameData.class);
        GameData result = new GameService().createGame(request.gameName(), authToken);
        return success(res, Map.of("gameID", result.gameID()));
    });

    public Route listGames = (Request req, Response res) -> handleSafely(req, res, () -> {
        String authToken = req.headers("Authorization");
        GameData[] games = new GameService().listGames(authToken);
        return success(res, Map.of("games", games));
    });

    public Route joinGame = (Request req, Response res) -> handleSafely(req, res, () -> {
        String authToken = req.headers("Authorization");
        Map<String, Object> body = gson.fromJson(req.body(), Map.class);

        if (body == null || !body.containsKey("playerColor") || !body.containsKey("gameID")) {
            throw new Exception("bad request");
        }

        String color = (String) body.get("playerColor");

        Object idObj = body.get("gameID");
        if (!(idObj instanceof Number)) {
            throw new Exception("bad request");
        }
        int gameID = ((Number) idObj).intValue();

        new GameService().joinGame(authToken, color, gameID);
        res.status(200);
        return "{}";
    });

}


