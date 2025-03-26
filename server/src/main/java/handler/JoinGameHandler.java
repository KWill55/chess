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

        // Validate request data
        if (request.gameID() <= 0 || request.playerColor() == null ||
                !(request.playerColor().equalsIgnoreCase("WHITE") || request.playerColor().equalsIgnoreCase("BLACK"))) {
            res.status(400); // Bad request: Invalid game ID or player color
            return gson.toJson(Map.of("message", "Error: bad request"));
        }

        try {
            // Retrieve the game from the database
            GameData game = gameService.getGame(request.gameID());
            if (game == null) {
                res.status(400); // Bad request: Game does not exist
                return gson.toJson(Map.of("message", "Error: bad request"));
            }

            // Check if the requested player slot is already occupied
            if ((request.playerColor().equalsIgnoreCase("WHITE") && game.whiteUsername() != null) ||
                    (request.playerColor().equalsIgnoreCase("BLACK") && game.blackUsername() != null)) {
                res.status(403); // Forbidden: The chosen color slot is already taken
                return gson.toJson(Map.of("message", "Error: already taken"));
            }

            // Update the game with the new player assigned to the selected color slot
            GameData updatedGame = new GameData(
                    game.gameID(),
                    request.playerColor().equalsIgnoreCase("WHITE") ? username : game.whiteUsername(),
                    request.playerColor().equalsIgnoreCase("BLACK") ? username : game.blackUsername(),
                    game.gameName(),
                    game.game()
            );

            // Save the updated game in the database
            gameService.updateGame(request.gameID(), updatedGame);

            // Return success response with an empty JSON object
            res.status(200);
            return gson.toJson(Map.of());

        } catch (DataAccessException e) {
            res.status(500); // Internal Server Error: Database failure or unexpected issue
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}