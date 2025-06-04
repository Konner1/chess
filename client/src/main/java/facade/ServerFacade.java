package facade;

import com.google.gson.Gson;

import model.GameData;

import model.AuthData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import ui.*;

import  exception.ResponseException;

import java.util.*;



public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        var request = Map.of("username", username, "password", password, "email", email);
        return this.makeRequest("POST", path, request, null, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        var path = "/session";
        var request = Map.of("username", username, "password", password);
        return this.makeRequest("POST", path, request, null, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        this.makeRequest("DELETE", "/session", null, authToken, Void.class);
    }

    public GameData createGame(String gameName, String authToken) throws ResponseException {
        var path = "/game";
        var request = Map.of("gameName", gameName);
        return this.makeRequest("POST", path, request, authToken, GameData.class);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        var path = "/game";
        GameResult result = this.makeRequest("GET", path, null, authToken, GameResult.class);
        return result.games();
    }


    public void joinGame(int gameID, String playerColor, String authToken) throws ResponseException {
        var path = "/game";
        var request = Map.of("gameID", gameID, "playerColor", playerColor);
        this.makeRequest("PUT", path, request, authToken, Void.class);
    }

    public void clear() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null, Void.class);
    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            if (responseClass == Void.class) {
                return null;
            }
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }
            throw new ResponseException(status, "Other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0 || http.getInputStream() != null) {
            try (InputStream resBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(resBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

