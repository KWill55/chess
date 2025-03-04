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

public class ListGamesHandler extends BaseHandler<Void> {
    private final GameService gameService;
    private final AuthService authService;

    public ListGamesHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    @Override
    protected Void parseRequest(Request req) {
        return null; // No request body needed
    }

    @Override
    protected Object handleRequest(Void requestData, Request req, Response res) {
        String authToken = getAuthToken(req);

        if (authToken == null || authToken.isEmpty()) {
            res.status(401); // Unauthorized
            return gson.toJson(Map.of("message", "Error: Missing or invalid auth token"));
        }

        try {
            // Validate auth token before proceeding
            String username = authService.getUserFromAuth(authToken);
            if (username == null) {
                res.status(401); // Unauthorized
                return gson.toJson(Map.of("message", "Error: Invalid authentication token"));
            }

            // Get the list of games
            List<GameData> gamesList = gameService.listGames();

            // Wrap it in ListGamesResponse
            res.status(200);
            return gson.toJson(new ListGamesResponse(gamesList));

        } catch (DataAccessException e) {
            // If the exception is due to an invalid token, return 401 instead of 403
            if (e.getMessage().contains("authToken not found")) {
                res.status(401); // Unauthorized
            } else {
                res.status(403); // Forbidden (e.g., database issues)
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
