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

            System.out.println("Notification to send: " + notificationText);

            for (Session s : gameSessions.get(gameID)) {
                if (s.isOpen() && !s.equals(session)) {
                    s.getRemote().sendString(gson.toJson(notify));
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
            GameDAO gameDAO = new SQLGameDAO();
            String username = authDAO.getAuth(authToken).username();

            // Remove session from the game session list
            ArrayList<Session> sessions = gameSessions.get(gameID);
            if (sessions != null) {
                sessions.remove(session);
            }

            // Load the game
            GameData game = gameDAO.getGame(gameID);
            if (game == null) return;

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
                        game.game()
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

            ChessGame.TeamColor opponent = (playerColor == ChessGame.TeamColor.WHITE)
                    ? ChessGame.TeamColor.BLACK
                    : ChessGame.TeamColor.WHITE;

// Checkmate?
            if (game.isInCheckmate(opponent)) {
                String msg = "Checkmate! " + opponent + " has lost.";
                broadcast(gameID, new NotificationMessage(msg));
            } else if (game.isInStalemate(opponent)) {
                String msg = "Stalemate! " + opponent + " has no legal moves.";
                broadcast(gameID, new NotificationMessage(msg));
            } else if (game.isInCheck(opponent)) {
                String msg = opponent + " is in check.";
                broadcast(gameID, new NotificationMessage(msg));
            }


            String moveMessage = auth.username() + " (" + playerColor + ") moved from " +
                    move.getStartPosition() + " to " + move.getEndPosition();
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

    //helper function
    private void broadcast(int gameID, ServerMessage message) throws IOException {
        for (Session s : gameSessions.get(gameID)) {
            if (s.isOpen()) {
                s.getRemote().sendString(gson.toJson(message));
            }
        }
    }
}