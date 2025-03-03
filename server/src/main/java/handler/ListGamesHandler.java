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
        String authToken;

        // Handle potential exception from getAuthToken()
        try {
            authToken = getAuthToken(req);
        } catch (Exception e) {
            res.status(401); // Unauthorized
            return Map.of("message", "Error: Missing or invalid auth token");
        }

        try {
            // Get the list of games
            List<GameData> gamesList = gameService.listGames();

            // Wrap it in ListGamesResponse
            ListGamesResponse response = new ListGamesResponse(gamesList);

            return response;
        } catch (DataAccessException e) {
            res.status(403); // Forbidden (e.g., invalid auth)
            return Map.of("message", "Error: " + e.getMessage());
        }
    }
}
