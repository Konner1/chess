package server;

import com.google.gson.Gson;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler extends BaseHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
        try {
            new ClearService().clearApplication();
            res.status(200);
            return "{}";
        } catch (Exception e) {
            return error(res, 500, e.getMessage());
        }
    }
}
