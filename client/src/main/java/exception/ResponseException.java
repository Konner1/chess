package exception;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {
    private final int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static ResponseException fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);

        Object statusObj = map.get("status");
        int status = 500;
        if (statusObj instanceof Double d) {
            status = d.intValue();
        }

        String message = map.get("message") != null
                ? map.get("message").toString()
                : "Unknown error from server";

        return new ResponseException(status, message);
    }

}
