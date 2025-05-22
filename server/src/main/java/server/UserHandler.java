package server;

import model.UserData;
import model.AuthData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class UserHandler extends BaseHandler {

    public Route register = (Request req, Response res) -> handleSafely(req, res, () -> {
        UserData user = gson.fromJson(req.body(), UserData.class);
        AuthData result = new UserService().register(user);
        return success(res, result);
    });

    public Route login = (Request req, Response res) -> handleSafely(req, res, () -> {
        UserData user = gson.fromJson(req.body(), UserData.class);
        AuthData result = new UserService().login(user);
        return success(res, result);
    });

    public Route logout = (Request req, Response res) -> handleSafely(req, res, () -> {
        String authToken = req.headers("Authorization");
        new UserService().logout(authToken);
        res.status(200);
        return "{}";
    });
}

