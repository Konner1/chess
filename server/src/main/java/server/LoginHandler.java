package server;

import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;


public class LoginHandler extends BaseHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            UserData user = gson.fromJson(req.body(),UserData.class);
            AuthData result = new UserService().login(user);
            return success(res,result);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg.contains("unauthorized")) {
                return error(res, 401, msg);
            } else if (msg.contains("bad request")) {
                return error(res, 400, msg);
            } else {
                return error(res, 500, msg);
            }
        }
    }
}