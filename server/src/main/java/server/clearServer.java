package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.ClearService;

import java.io.IOException;

public class ClearEndpoint extends BaseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!exchange.getRequestMethod().equalsIgnoreCase("DELETE")) {
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }

            ClearService service = new ClearService();
            service.clearApplication();

            sendResponse(exchange, 200, new Object()); // Respond with empty JSON: {}
        } catch (Exception e) {
            sendError(exchange, 500, e.getMessage());
        }
    }
}
