package server;

import model.UserData;
import model.AuthData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class UserHandler extends BaseHandler {

    public Route register = (Request req, Response res) -> {
        try {
            UserData user = gson.fromJson(req.body(), UserData.class);
            AuthData result = new UserService().register(user);
            return success(res, result);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg.contains("already taken")){
                return error(res, 403, msg);
            }
            if (msg.contains("bad request")){
                return error(res, 400, msg);
            }
            return error(res, 500, msg);
        }
    };

    public Route login = (Request req, Response res) -> {
        try {
            UserData user = gson.fromJson(req.body(), UserData.class);
            AuthData result = new UserService().login(user);
            return success(res, result);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg.contains("unauthorized")){
                return error(res, 401, msg);
            }
            if (msg.contains("bad request")){
                return error(res, 400, msg);
            }
            return error(res, 500, msg);
        }
    };
    public Route logout = (Request req, Response res) -> {
        try {
            String authToken = req.headers("Authorization");
            new UserService().logout(authToken);
            res.status(200);
            return "{}";
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg.contains("unauthorized")){
                return error(res, 401, msg);
            }
            return error(res, 500, msg);
        }
    };
}
