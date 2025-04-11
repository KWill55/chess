package client.websocket;

import chess.ChessBoard;
import com.google.gson.Gson;
import exception.ResponseException;
import ui.DrawBoard;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
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
    private ChessBoard latestBoard;
    private String playerColor;


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

                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION:
                            NotificationMessage notifyMsg = gson.fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notifyMsg);
                            break;
                        case LOAD_GAME:
                            LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
                            latestBoard = load.getGame().getBoard();  // Save latest board
                            DrawBoard drawBoard = new DrawBoard(latestBoard, playerColor);
                            drawBoard.drawBoard();
                            break;

                        case ERROR:
                            ErrorMessage error = gson.fromJson(message.toString(), ErrorMessage.class);
                            System.out.println(" " + error.errorMessage);

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
            UserGameCommand command = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT,
                    authToken,
                    gameID
            );

            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void send(UserGameCommand command) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public ChessBoard getLatestBoard() {
        return latestBoard;
    }

    public void setPlayerColor(String color) {
        this.playerColor = color;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}

