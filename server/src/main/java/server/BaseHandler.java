package server;

import com.google.gson.Gson;
import model.ErrorResponse;
import spark.Response;
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
}
