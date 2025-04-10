package handler;

import dataaccess.DataAccessException;
import model.*;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;
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

    /**
     * Handles the request to join a game.
     * Validates authentication, checks game existence, verifies player slot availability, and updates the game.
     *
     * @param request The parsed JoinGameRequest object.
     * @param req The Spark Request object.
     * @param res The Spark Response object.
     * @return A JSON response indicating success or failure.
     */
    @Override
    protected Object handleRequest(JoinGameRequest request, Request req, Response res) {
        String authToken = getAuthToken(req); // Extract the authentication token from the request

        // Authenticate the user
        String username;
        try {
            username = authService.getUserFromAuth(authToken);
        } catch (DataAccessException e) {
            res.status(401); // Unauthorized: Invalid authentication token
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }

        // Ensure the username is valid
        if (username == null) {
            res.status(401); // Unauthorized: Auth token doesn't correspond to a user
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }

        if (request.gameID() <= 0) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: invalid game ID"));
        }


        try {
            // Retrieve the game from the database
            GameData game = gameService.getGame(request.gameID());
            if (game == null) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: game not found"));
            }

            // Handle observer
            if (request.playerColor() == null) {
                res.status(200);
                return gson.toJson(new JoinGameResponse());
            }

            // Continue with player join logic
            if (!(request.playerColor().equalsIgnoreCase("WHITE") || request.playerColor().equalsIgnoreCase("BLACK"))) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: invalid player color"));
            }

            if ((request.playerColor().equalsIgnoreCase("WHITE") && game.whiteUsername() != null) ||
                    (request.playerColor().equalsIgnoreCase("BLACK") && game.blackUsername() != null)) {
                res.status(403);
                return gson.toJson(Map.of("message", "Error: already taken"));
            }

            GameData updatedGame = new GameData(
                    game.gameID(),
                    request.playerColor().equalsIgnoreCase("WHITE") ? username : game.whiteUsername(),
                    request.playerColor().equalsIgnoreCase("BLACK") ? username : game.blackUsername(),
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