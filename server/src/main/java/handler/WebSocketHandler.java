package handler;

import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@WebSocket
public class WebSocketHandler {
    private final Gson gson = new Gson();
    private final HashMap<Integer, ArrayList<Session>> gameSessions = new HashMap<>();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
//        System.out.println("Incoming WebSocket message: " + message);

        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(command, session);
            case MAKE_MOVE -> System.out.println("⚠️not implemented yet"); //->handleMakeMove(command);
            case LEAVE -> handleLeave(command, session);
            case RESIGN -> System.out.println("⚠️ RESIGN not implemented yet");
        }
    }

    private void handleConnect(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

//        System.out.println("CONNECT received for gameID: " + gameID + ", authToken: " + authToken);

        // Add the session to the game
        gameSessions.putIfAbsent(gameID, new ArrayList<>());
        gameSessions.get(gameID).add(session);

//        System.out.println(" Sessions in game " + gameID + ": " + gameSessions.get(gameID).size());

        try {
            GameDAO gameDAO = new SQLGameDAO();
            GameData gameData = gameDAO.getGame(gameID);

            AuthDAO authDAO = new SQLAuthDAO();
            String username = authDAO.getAuth(authToken).username();

            ServerMessage loadGame = new LoadGameMessage(gameData.game());
            session.getRemote().sendString(gson.toJson(loadGame));

            String notificationText = gameData.gameName() + " - " + username + " joined the game";
            ServerMessage notify = new NotificationMessage(notificationText);

//            System.out.println("Notification to send: " + notificationText);

            for (Session s : gameSessions.get(gameID)) {
//                System.out.println("Attempting to send to session: " + s);
                if (s.isOpen() && !s.equals(session)) {
                    s.getRemote().sendString(gson.toJson(notify));
//                    System.out.println("Sent to other client!");
                }
            }

        } catch (DataAccessException e) {
            ServerMessage error = new NotificationMessage("Error: " + e.getMessage());
            session.getRemote().sendString(gson.toJson(error));
        }
    }

    private void handleLeave(UserGameCommand command, Session session) {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        try {
            AuthDAO authDAO = new SQLAuthDAO();
            String username = authDAO.getAuth(authToken).username();

            // Remove the session from the list
            ArrayList<Session> sessions = gameSessions.get(gameID);
            sessions.remove(session);

            // Notify others
            ServerMessage notify = new NotificationMessage(username + " left the game.");
            for (Session s : sessions) {
                if (s.isOpen()) {
                    s.getRemote().sendString(gson.toJson(notify));
                }
            }

        } catch (DataAccessException | IOException e) {
            e.printStackTrace(); // optional debug logging
        }
    }

}