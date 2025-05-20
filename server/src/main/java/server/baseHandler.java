package server;

import com.google.gson.Gson;
import model.ErrorResponse;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class BaseHandler {
    protected final Gson gson = new Gson();

    protected void sendResponse(HttpExchange exchange, int statusCode, Object responseObj) throws IOException {
        String json = gson.toJson(responseObj);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, json.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    protected void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendResponse(exchange, statusCode, new ErrorResponse("Error: " + message));
    }
}
