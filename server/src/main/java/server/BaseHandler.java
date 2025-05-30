package server;

import com.google.gson.Gson;
import model.ErrorResponse;
import spark.Response;
import spark.Request;

import java.util.concurrent.Callable;
import com.google.gson.GsonBuilder;

public abstract class BaseHandler {
    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    protected String error(Response res, int statusCode, String message) {
        res.status(statusCode);
        res.type("application/json");
        return gson.toJson(new ErrorResponse("Error: " + message));
    }

    protected String success(Response res, Object result) {
        res.status(200);
        res.type("application/json");
        return gson.toJson(result);
    }

    protected Object handleSafely(Request req, Response res, Callable<Object> logic) {
        try {
            return logic.call();
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("unauthorized")){
                return error(res, 401, msg);
            }
            if (msg != null && msg.contains("bad request")){
                return error(res, 400, msg);
            }
            if (msg != null && msg.contains("already taken")){
                return error(res, 403, msg);
            }
            return error(res, 500, msg != null ? msg : "internal server error");
        }
    }
}
