//package exception;
//
//import com.google.gson.Gson;
//
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ResponseException extends Exception{
//    final private int statusCode;
//
//    public ResponseException(int statusCode, String message) {
//        super(message);
//        this.statusCode = statusCode;
//    }
//
//    public String toJson() {
//        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
//    }
//
//    public static ResponseException fromJson(InputStream stream) {
//        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
//        var status = ((Double)map.get("status")).intValue();
//        String message = map.get("message").toString();
//        return new ResponseException(status, message);
//    }
//
//    public int StatusCode() {
//        return statusCode;
//    }
//}

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

        // Handle missing "status" safely
        Object statusObj = map.get("status");
        int status = 500;  // Default if not provided
        if (statusObj instanceof Double d) {
            status = d.intValue();
        }

        String message = map.get("message") != null
                ? map.get("message").toString()
                : "Unknown error from server";

        return new ResponseException(status, message);
    }

    public int StatusCode() {
        return statusCode;
    }
}
