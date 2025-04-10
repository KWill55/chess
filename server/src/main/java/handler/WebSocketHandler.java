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
        System.out.println("Incoming WebSocket message: " + message);

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

        System.out.println("CONNECT received for gameID: " + gameID + ", authToken: " + authToken);

        // Add the session to the game
        gameSessions.putIfAbsent(gameID, new ArrayList<>());
        gameSessions.get(gameID).add(session);

        System.out.println(" Sessions in game " + gameID + ": " + gameSessions.get(gameID).size());

        try {
            GameDAO gameDAO = new SQLGameDAO();
            GameData gameData = gameDAO.getGame(gameID);

            AuthDAO authDAO = new SQLAuthDAO();
            String username = authDAO.getAuth(authToken).username();

            ServerMessage loadGame = new LoadGameMessage(gameData.game());
            session.getRemote().sendString(gson.toJson(loadGame));

            String notificationText = gameData.gameName() + " - " + username + " joined the game";
            ServerMessage notify = new NotificationMessage(notificationText);

            System.out.println("Notification to send: " + notificationText);

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

    private void handleResign(UserGameCommand command, Session session) {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        try {
            AuthDAO authDAO = new SQLAuthDAO();
            String username = authDAO.getAuth(authToken).username();

            String text = username + " has resigned. Game over.";
            ServerMessage resignMessage = new NotificationMessage(text);

            for (Session s : gameSessions.get(gameID)) {
                if (s.isOpen()) {
                    s.getRemote().sendString(gson.toJson(resignMessage));
                }
            }

        } catch (DataAccessException | IOException e) {
            e.printStackTrace();
        }
    }

//    private void handleMove(UserGameCommand command, Session session) throws IOException {
//        int gameID = command.getGameID();
//        ChessMove move = command.getMove();
//        String authToken = command.getAuthToken();
//
//        try {
//            AuthDAO authDAO = new SQLAuthDAO();
//            GameDAO gameDAO = new SQLGameDAO();
//
//            // Get the game and user
//            var auth = authDAO.getAuth(authToken);
//            var gameData = gameDAO.getGame(gameID);
//            ChessGame game = gameData.game();
//
//            //verify that player can only move player pieces
//            ChessGame.TeamColor playerColor = getPlayerColor(gameData, auth.username());
//
//
//            System.out.println("🔎 DEBUG: Player = " + auth.username());
//            System.out.println("🔎 DEBUG: Claimed teamColor = " + playerColor);
//            System.out.println("🔎 DEBUG: Server's current turn = " + game.getTeamTurn());
//
//            ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
//            System.out.println("🔎 DEBUG: Piece at start = " + (piece == null ? "null" : piece.getTeamColor()));
//
//
//            if (playerColor != game.getTeamTurn()) {
//                session.getRemote().sendString(gson.toJson(
//                        new NotificationMessage("Not your turn.")
//                ));
//                return;
//            }
//
//            if (piece == null || piece.getTeamColor() != playerColor) {
//                session.getRemote().sendString(gson.toJson(
//                        new NotificationMessage("Invalid move: You can only move your own pieces.")
//                ));
//                return;
//            }
//
//
//
//            // Make the move
//            game.makeMove(move);
//
//            String moveMessage = playerColor + " moved from " +
//                    move.getStartPosition().toString() + " to " + move.getEndPosition().toString();
//
//            ServerMessage notification = new NotificationMessage(moveMessage);
//
//
//            // Save the updated game
//            gameDAO.updateGame(new GameData(
//                    gameData.gameID(),
//                    gameData.whiteUsername(),
//                    gameData.blackUsername(),
//                    gameData.gameName(),
//                    game
//            ));
//
//
//            // Send updated board to all clients in the game
//            ServerMessage msg = new LoadGameMessage(game);
//            for (Session s : gameSessions.get(gameID)) {
//                if (s.isOpen()) s.getRemote().sendString(gson.toJson(msg));
//            }
//
//            // Send move notification to all clients
//            for (Session s : gameSessions.get(gameID)) {
//                if (s.isOpen()) {
//                    s.getRemote().sendString(gson.toJson(notification));
//                }
//            }
//
//
//        } catch (InvalidMoveException e) {
//            session.getRemote().sendString(gson.toJson(new NotificationMessage("Invalid move: " + e.getMessage())));
//        } catch (Exception e) {
//            session.getRemote().sendString(gson.toJson(new NotificationMessage("Server error: " + e.getMessage())));
//        }
//    }


    private void handleMove(UserGameCommand command, Session session) throws IOException {
        session.getRemote().sendString(gson.toJson(
                new NotificationMessage("handleMove called")
        ));



        int gameID = command.getGameID();
        ChessMove move = command.getMove();
        String authToken = command.getAuthToken();

        session.getRemote().sendString(gson.toJson(
                new NotificationMessage("Server received move: " + move.getStartPosition() + " → " + move.getEndPosition())
        ));


        if (move == null) {
            session.getRemote().sendString(gson.toJson(
                    new NotificationMessage("move is null")
            ));
            return;
        } else {
            session.getRemote().sendString(gson.toJson(
                    new NotificationMessage("Move received: " + move.getStartPosition() + " → " + move.getEndPosition())
            ));
        }

        session.getRemote().sendString(gson.toJson(
                new NotificationMessage("Game ID: " + gameID + ", Auth Token: " + authToken)
        ));

        try {
            AuthDAO authDAO = new SQLAuthDAO();
            GameDAO gameDAO = new SQLGameDAO();

            var auth = authDAO.getAuth(authToken);
            session.getRemote().sendString(gson.toJson(
                    new NotificationMessage("Authenticated user: " + auth.username())
            ));

            var gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();

            ChessGame.TeamColor playerColor = game.getTeamTurn();
//            ChessGame.TeamColor playerColor = getPlayerColor(gameData, auth.username(), session);

            session.getRemote().sendString(gson.toJson(
                    new NotificationMessage("Player color: " + playerColor + ", Server turn: " + game.getTeamTurn())
            ));

            if (playerColor == null) {
                session.getRemote().sendString(gson.toJson(
                        new NotificationMessage("Could not determine your color. You might not be in this game.")
                ));
                return;
            }

            ChessPosition start = move.getStartPosition();
            ChessPiece piece = game.getBoard().getPiece(start);
            session.getRemote().sendString(gson.toJson(
                    new NotificationMessage("Piece at " + start + ": " +
                            (piece == null ? "null" : piece.getTeamColor() + " " + piece.getPieceType()))
            ));

            if (piece != null) {
                session.getRemote().sendString(gson.toJson(
                        new NotificationMessage("Comparing playerColor (" + playerColor + ") to piece.getTeamColor() (" + piece.getTeamColor() + ")")
                ));
            }

            if (playerColor != game.getTeamTurn()) {
                session.getRemote().sendString(gson.toJson(
                        new NotificationMessage("Not your turn.")
                ));
                return;
            }

            if (piece == null || piece.getTeamColor() != playerColor) {
                session.getRemote().sendString(gson.toJson(
                        new NotificationMessage("Invalid move: You can only move your own pieces.")
                ));
                return;
            }

            game.makeMove(move);
            session.getRemote().sendString(gson.toJson(
                    new NotificationMessage("Move made successfully")
            ));

            String moveMessage = playerColor + " moved from " + move.getStartPosition() + " to " + move.getEndPosition();
            ServerMessage notification = new NotificationMessage(moveMessage);

            gameDAO.updateGame(new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game
            ));
            session.getRemote().sendString(gson.toJson(
                    new NotificationMessage("Game updated in DB")
            ));

            ServerMessage boardUpdate = new LoadGameMessage(game);
            for (Session s : gameSessions.get(gameID)) {
                if (s.isOpen()) {
                    s.getRemote().sendString(gson.toJson(boardUpdate));
                }
            }

            for (Session s : gameSessions.get(gameID)) {
                if (s.isOpen()) {
                    s.getRemote().sendString(gson.toJson(notification));
                }
            }
            session.getRemote().sendString(gson.toJson(
                    new NotificationMessage("📢 Sent move notification")
            ));

        } catch (InvalidMoveException e) {
            session.getRemote().sendString(gson.toJson(
                    new NotificationMessage("Invalid move: " + e.getMessage())
            ));
        } catch (Exception e) {
            session.getRemote().sendString(gson.toJson(
                    new NotificationMessage("Server error: " + e.getMessage())
            ));
            e.printStackTrace();
        }
    }
}