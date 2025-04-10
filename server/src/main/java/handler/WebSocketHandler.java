package handler;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.FileWriter;
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
            case MAKE_MOVE -> handleMove(command, session);
            case LEAVE -> handleLeave(command, session);
            case RESIGN -> handleResign(command, session);
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

            // Determine player color (or observer)
            String playerColor;
            if (username.equals(gameData.whiteUsername())) {
                playerColor = "WHITE";
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = "BLACK";
            } else {
                playerColor = "OBSERVER";
            }

            // Send game state to the joining client
            ServerMessage loadGame = new LoadGameMessage(gameData.game());
            session.getRemote().sendString(gson.toJson(loadGame));

            // Notify all other clients in this game
            String notificationText = gameData.gameName() + " - " + username + " joined the game as " + playerColor;
            ServerMessage notify = new NotificationMessage(notificationText);

//            System.out.println("Notification to send: " + notificationText);

            for (Session s : gameSessions.get(gameID)) {
                if (s.isOpen() && !s.equals(session)) {
                    s.getRemote().sendString(gson.toJson(notify));
                }
            }

        } catch (DataAccessException e) {
            ServerMessage error = new ErrorMessage("Error: " + e.getMessage());
            session.getRemote().sendString(gson.toJson(error));
        }
    }


    private void handleLeave(UserGameCommand command, Session session) {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        try {
            AuthDAO authDAO = new SQLAuthDAO();
            GameDAO gameDAO = new SQLGameDAO();
            String username = authDAO.getAuth(authToken).username();

            // Remove session from the game session list
            ArrayList<Session> sessions = gameSessions.get(gameID);
            if (sessions != null) {
                sessions.remove(session);
            }

            // Load the game
            GameData game = gameDAO.getGame(gameID);
            if (game == null) {
                return;
            }

            // Remove user from white/black slot if they were a player
            String white = game.whiteUsername();
            String black = game.blackUsername();

            boolean isPlayer = false;
            if (username.equals(white)) {
                white = null;
                isPlayer = true;
            } else if (username.equals(black)) {
                black = null;
                isPlayer = true;
            }

            // Create updated GameData with that player's slot set to null
            if (isPlayer) {
                GameData updatedGame = new GameData(
                        game.gameID(),
                        white,
                        black,
                        game.gameName(),
                        game.game(),
                        game.gameOver()
                );
                gameDAO.updateGame(updatedGame);
            }

            // Notify the other players
            ServerMessage notify = new NotificationMessage(username + " left the game.");
            if (sessions != null) {
                for (Session s : sessions) {
                    if (s.isOpen()) {
                        s.getRemote().sendString(gson.toJson(notify));
                    }
                }
            }

        } catch (DataAccessException | IOException e) {
            e.printStackTrace(); // For debugging, or replace with proper logging
        }
    }

    private void handleResign(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        try {
            AuthDAO authDAO = new SQLAuthDAO();
            GameDAO gameDAO = new SQLGameDAO();

            var auth = authDAO.getAuth(authToken);
            var gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();

            // Observer can't resign
            if (!auth.username().equals(gameData.whiteUsername()) &&
                    !auth.username().equals(gameData.blackUsername())) {
                session.getRemote().sendString(gson.toJson(
                        new ErrorMessage("Only players can resign.")));
                return;
            }

            // Game already over
            if (gameData.gameOver()) {
                session.getRemote().sendString(gson.toJson(
                        new ErrorMessage("The game is already over.")));
                return;
            }

            // Broadcast resignation
            String msg = auth.username() + " resigned.";
            broadcast(gameID, new NotificationMessage(msg));

            // Set game to over
            gameDAO.updateGame(new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game,
                    true
            ));

        } catch (Exception e) {
            session.getRemote().sendString(gson.toJson(
                    new ErrorMessage("Server error: " + e.getMessage())));
            e.printStackTrace();
        }
    }

    private void handleMove(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        ChessMove move = command.getMove();
        String authToken = command.getAuthToken();

        try {
            AuthDAO authDAO = new SQLAuthDAO();
            GameDAO gameDAO = new SQLGameDAO();

            var auth = authDAO.getAuth(authToken);
            var gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();

            if (gameData.gameOver()) {
                session.getRemote().sendString(gson.toJson(
                        new ErrorMessage("The game is over. No more moves allowed.")));
                return;
            }

            // Get player's color
            ChessGame.TeamColor playerColor = null;
            if (auth.username().equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (auth.username().equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            } else {
                session.getRemote().sendString(gson.toJson(
                        new ErrorMessage("You're not a player in this game.")));
                return;
            }

            // Check turn
            if (playerColor != game.getTeamTurn()) {
                session.getRemote().sendString(gson.toJson(
                        new ErrorMessage("Not your turn.")));
                return;
            }

            // Check piece ownership
            ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
            if (piece == null || piece.getTeamColor() != playerColor) {
                session.getRemote().sendString(gson.toJson(
                        new ErrorMessage("Invalid move: You can only move your own pieces.")));
                return;
            }

            // Make move
            game.makeMove(move);
            gameDAO.updateGame(new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game,
                    false
            ));

            // Broadcast LoadGameMessage
            ServerMessage boardUpdate = new LoadGameMessage(game);
            for (Session s : gameSessions.get(gameID)) {
                if (s.isOpen()) {
                    s.getRemote().sendString(gson.toJson(boardUpdate));
                }
            }

            // Send Notification (not to current player)
            String moveMessage = auth.username() + " (" + playerColor + ") moved from " +
                    move.getStartPosition() + " to " + move.getEndPosition();
            ServerMessage notification = new NotificationMessage(moveMessage);
            for (Session s : gameSessions.get(gameID)) {
                if (s.isOpen() && !s.equals(session)) {
                    s.getRemote().sendString(gson.toJson(notification));
                }
            }

            // ♟ Check for check/stalemate/checkmate
            ChessGame.TeamColor opponent = (playerColor == ChessGame.TeamColor.WHITE)
                    ? ChessGame.TeamColor.BLACK
                    : ChessGame.TeamColor.WHITE;

            if (game.isInCheckmate(opponent)) {
                broadcast(gameID, new NotificationMessage("Checkmate! " + opponent + " has lost."));
                gameDAO.updateGame(new GameData(
                        gameData.gameID(),
                        gameData.whiteUsername(),
                        gameData.blackUsername(),
                        gameData.gameName(),
                        game,
                        true
                ));
            } else if (game.isInStalemate(opponent)) {
                broadcast(gameID, new NotificationMessage("Stalemate! " + opponent + " has no legal moves."));
            } else if (game.isInCheck(opponent)) {
                broadcast(gameID, new NotificationMessage(opponent + " is in check."));
            }

        } catch (InvalidMoveException e) {
            session.getRemote().sendString(gson.toJson(
                    new ErrorMessage("Invalid move: " + e.getMessage())));
        } catch (Exception e) {
            session.getRemote().sendString(gson.toJson(
                    new ErrorMessage("Server error: " + e.getMessage())));
            e.printStackTrace();
        }
    }



    //helper function
    private void broadcast(int gameID, ServerMessage message) throws IOException {
        for (Session s : gameSessions.get(gameID)) {
            if (s.isOpen()) {
                s.getRemote().sendString(gson.toJson(message));
            }
        }
    }
}