package handler.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.Database;
import model.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private final ConcurrentHashMap<Integer, List<Session>> gameSessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(command, session);
            // More cases like MAKE_MOVE, LEAVE, RESIGN go here later
        }
    }

    private void handleConnect(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        // Add the session to the game
        gameSessions.putIfAbsent(gameID, new ArrayList<>());
        gameSessions.get(gameID).add(session);

        // TODO: Validate authToken and gameID (e.g., check DB)

        // Fetch the current game state (stubbed for now)
        Game game = Database.getGame(gameID); // Replace with your real game fetch

        // Send LOAD_GAME to the root client
        ServerMessage loadGame = new LoadGameMessage(game);
        session.getRemote().sendString(gson.toJson(loadGame));

        // Notify other clients
        String notificationText = "A new player joined game " + gameID;
        ServerMessage notify = new NotificationMessage(notificationText);

        for (Session s : gameSessions.get(gameID)) {
            if (s.isOpen() && !s.equals(session)) {
                s.getRemote().sendString(gson.toJson(notify));
            }
        }
    }
}
