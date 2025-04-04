package client.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.NotificationMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final NotificationHandler notificationHandler;
    private final Gson gson = new Gson();

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

                    switch (serverMessage.serverMessageType) {
                        case NOTIFICATION:
                            NotificationMessage notifyMsg = gson.fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notifyMsg);
                            break;
                        case LOAD_GAME:
                            // Handle board redraw or pass to another handler
                            System.out.println("Received LOAD_GAME message.");
                            break;
                        case ERROR:
                            System.err.println("Received error from server: " + message);
                            break;
                    }
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        // You can initialize stuff here if needed
    }

    public void connectToGame(String authToken, int gameID) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand();
            command.commandType = UserGameCommand.CommandType.CONNECT;
            command.authToken = authToken;
            command.gameID = gameID;

            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    // TODO: Add makeMove, leave, resign, etc. commands
}

