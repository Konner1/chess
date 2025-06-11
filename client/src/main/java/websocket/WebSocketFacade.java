package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;


import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    private static final Gson GSON = new Gson();

    private Session session;
    private final DisplayHandler displayHandler;

    public WebSocketFacade(String url, DisplayHandler displayHandler) throws ResponseException {
        this.displayHandler = displayHandler;

        try {
            URI socketURI = new URI(url.replaceFirst("^http", "ws") + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(String.class, this::onMessage);
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, "WebSocket connect failed: " + ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {

    }

    private void onMessage(String message) {
        try {
            ServerMessage base = GSON.fromJson(message, ServerMessage.class);
            dispatchMessage(base.getServerMessageType(), message);
        } catch (Exception e) {
            displayHandler.error("Failed to parse message: " + e.getMessage());
        }
    }

    private void dispatchMessage(ServerMessageType type, String raw) {
        switch (type) {
            case LOAD_GAME -> handleLoad(raw);
            case NOTIFICATION -> handleNotification(raw);
            case ERROR -> handleError(raw);
        }
    }

    private void handleLoad(String raw) {
        LoadMessage msg = GSON.fromJson(raw, LoadMessage.class);
        displayHandler.updateBoard(msg.getGame());
    }

    private void handleNotification(String raw) {
        NotificationMessage msg = GSON.fromJson(raw, NotificationMessage.class);
        displayHandler.notify(msg.getMessage());
    }

    private void handleError(String raw) {
        ErrorMessage msg = GSON.fromJson(raw, ErrorMessage.class);
        displayHandler.error(msg.getErrorMessage());
    }

    public void sendCommand(UserGameCommand command) throws ResponseException {
        if (session == null || !session.isOpen()) {
            throw new ResponseException(500, "WebSocket is not open");
        }
        try {
            session.getBasicRemote().sendText(GSON.toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}