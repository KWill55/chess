package handler;

import dataaccess.DataAccessException;
import model.GameData;
import model.ListGamesResponse;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

/**
 * Handles the "List Games" API request.
 * This handler processes requests to retrieve a list of available chess games.
 */
public class ListGamesHandler extends BaseHandler<Void> {
    private final GameService gameService;
    private final AuthService authService;

    /**
     * Constructor for ListGamesHandler.
     *
     * @param gameService Handles game-related operations.
     * @param authService Handles authentication token validation.
     */
    public ListGamesHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    /**
     * Parses the incoming request.
     * Since listing games does not require a request body, this method returns null.
     *
     * @param req The Spark Request object containing the request details.
     * @return Always returns null, as no request body is needed.
     */
    @Override
    protected Void parseRequest(Request req) {
        return null; // No request body needed
    }

    /**
     * Handles the request to list all available games.
     * Validates the authentication token before retrieving the game list.
     *
     * @param requestData Not used since this request does not require a body.
     * @param req The Spark Request object.
     * @param res The Spark Response object.
     * @return A JSON response containing the list of games or an error message.
     */
    @Override
    protected Object handleRequest(Void requestData, Request req, Response res) {
        String authToken = getAuthToken(req); // Extract the authentication token from the request

        // Check if authToken is missing or empty
        if (authToken == null || authToken.isEmpty()) {
            res.status(401); // Unauthorized: Missing authentication token
            return gson.toJson(Map.of("message", "Error: Missing or invalid auth token"));
        }

        try {
            // Validate the authentication token before proceeding
            String username = authService.getUserFromAuth(authToken);
            if (username == null) {
                res.status(401); // Unauthorized: Auth token is invalid
                return gson.toJson(Map.of("message", "Error: Invalid authentication token"));
            }

            // Retrieve the list of games from GameService
            List<GameData> gamesList = gameService.listGames();

            // Wrap the list in a ListGamesResponse object and return as JSON
            res.status(200);
            return gson.toJson(new ListGamesResponse(gamesList));

        } catch (DataAccessException e) {
            // Handle specific exception for invalid authentication token
            if (e.getMessage().contains("authToken not found")) {
                res.status(401); // Unauthorized: Auth token does not exist in database
            } else {
                res.status(401); // Forbidden: Other database issues
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
