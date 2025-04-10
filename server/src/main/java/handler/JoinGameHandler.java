package handler;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.SQLGameDAO;
import model.*;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;
import websocket.messages.ErrorMessage;

import java.util.Map;

/**
 * Handles the "Join Game" API request.
 * This handler processes requests to join an existing chess game by assigning a player to a color slot.
 */
public class JoinGameHandler extends BaseHandler<JoinGameRequest> {
    private final GameService gameService;
    private final AuthService authService;

    /**
     * Constructor for JoinGameHandler.
     *
     * @param gameService Handles game-related operations.
     * @param authService Handles authentication token validation.
     */
    public JoinGameHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    /**
     * Parses the incoming request body into a JoinGameRequest object.
     *
     * @param req The Spark Request object containing the request body.
     * @return A JoinGameRequest object parsed from the JSON request body.
     */
    @Override
    protected JoinGameRequest parseRequest(Request req) {
        return gson.fromJson(req.body(), JoinGameRequest.class);
    }


    @Override
    protected Object handleRequest(JoinGameRequest request, Request req, Response res) {
        String authToken = getAuthToken(req); // Extract the authentication token

        // Authenticate user
        String username;
        try {
            username = authService.getUserFromAuth(authToken);
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }
        if (username == null) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }
        if (request.gameID() <= 0) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: invalid game ID"));
        }

        // Normalize and validate the color
        String color = request.playerColor();
        if (color != null) {
            color = color.trim().toUpperCase();
        }
        if (color != null && !color.equals("WHITE") && !color.equals("BLACK")) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: invalid player color"));
        }

        // If no color was provided, treat it as invalid (instead of observer)
        if (color == null) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: invalid player color"));
        }

        try {
            // Retrieve the game from the database
            GameData game = gameService.getGame(request.gameID());
            if (game == null) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: game not found"));
            }

            // If the desired color slot is already taken, return 403
            if ((color.equals("WHITE") && game.whiteUsername() != null) ||
                    (color.equals("BLACK") && game.blackUsername() != null)) {
                res.status(403);
                return gson.toJson(Map.of("message", "Error: already taken"));
            }

            GameData updatedGame = new GameData(
                    game.gameID(),
                    color.equals("WHITE") ? username : game.whiteUsername(),
                    color.equals("BLACK") ? username : game.blackUsername(),
                    game.gameName(),
                    game.game(),
                    game.gameOver()
            );

            gameService.updateGame(request.gameID(), updatedGame);

            res.status(200);
            return gson.toJson(new JoinGameResponse());

        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }





}