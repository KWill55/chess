package handler;

import dataaccess.DataAccessException;
import model.CreateGameRequest;
import model.CreateGameResponse;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;
import java.util.Map;

/**
 * Handles the "Create Game" API request.
 * This handler processes requests to create a new game and assigns a unique game ID.
 */
public class CreateGameHandler extends BaseHandler<CreateGameRequest> {
    private final GameService gameService;
    private final AuthService authService;

    /**
     * Constructor for CreateGameHandler.
     *
     * @param gameService Handles game-related operations.
     * @param authService Handles authentication token validation.
     */
    public CreateGameHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    /**
     * Parses the incoming request body into a CreateGameRequest object.
     *
     * @param req The Spark Request object containing the request body.
     * @return A CreateGameRequest object parsed from the JSON request body.
     */
    @Override
    protected CreateGameRequest parseRequest(Request req) {
        return gson.fromJson(req.body(), CreateGameRequest.class);
    }

    /**
     * Handles the request to create a new game.
     * Validates the authentication token, verifies the requesting user, and creates the game.
     *
     * @param request The parsed CreateGameRequest object.
     * @param req The Spark Request object.
     * @param res The Spark Response object.
     * @return A JSON response indicating success or failure.
     */
    @Override
    protected Object handleRequest(CreateGameRequest request, Request req, Response res) {
        String authToken = getAuthToken(req);  // Get the auth token from the request header

        // Return `401 Unauthorized` if the auth token is missing or empty
        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Missing auth token"));
        }

        try {
            // Verify the auth token and get the associated username
            String username = authService.getUserFromAuth(authToken);

            // Return `401 Unauthorized` if the token is invalid or does not correspond to a user
            if (username == null) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: Unauthorized"));
            }

            // Create a new game and assign it a unique game ID
            int gameID = gameService.createGame(request.gameName());


            // Return a success response with the generated game ID
            res.status(200);
            return gson.toJson(new CreateGameResponse(gameID));

        } catch (DataAccessException e) {
            // Return `401 Unauthorized` if there is an issue with the database or username validation
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

}
