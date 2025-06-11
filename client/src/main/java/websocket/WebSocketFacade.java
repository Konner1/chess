package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;
import websocket.DisplayHandler;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final DisplayHandler displayHandler;

    public WebSocketFacade(String url, DisplayHandler displayHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.displayHandler = displayHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    var gson = new Gson();
                    try {
                        ServerMessage baseMessage = gson.fromJson(message, ServerMessage.class);
                        switch (baseMessage.getServerMessageType()) {
                            case LOAD_GAME -> {
                                LoadMessage load = gson.fromJson(message, LoadMessage.class);
                                displayHandler.updateBoard(load.getGame());
                            }
                            case NOTIFICATION -> {
                                NotificationMessage notif = gson.fromJson(message, NotificationMessage.class);
                                displayHandler.notify(notif.getMessage());
                            }
                            case ERROR -> {
                                ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
                                displayHandler.error(error.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to handle WebSocket message: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // no-op
    }

    public void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            String json = new Gson().toJson(command);
            this.session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
