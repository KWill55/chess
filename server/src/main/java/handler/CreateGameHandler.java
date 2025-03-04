package handler;

import dataaccess.DataAccessException;
import model.CreateGameRequest;
import model.CreateGameResponse;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class CreateGameHandler extends BaseHandler<CreateGameRequest> {
    private final GameService gameService;
    private final AuthService authService;

    public CreateGameHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    @Override
    protected CreateGameRequest parseRequest(Request req) {
        return gson.fromJson(req.body(), CreateGameRequest.class);
    }

    @Override
    protected Object handleRequest(CreateGameRequest request, Request req, Response res) {
        String authToken = getAuthToken(req);  // Get the auth token from the header

        // `401 Unauthorized` is returned when the auth token is missing
        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Missing auth token"));
        }

        try {
            // `401 Unauthorized` is returned for invalid auth tokens
            String username = authService.getUserFromAuth(authToken);
            if (username == null) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: Unauthorized"));
            }

            // create a game
            int gameID = gameService.createGame(request.gameName());
            res.status(200);
            return gson.toJson(new CreateGameResponse(gameID));

        } catch (DataAccessException e) {
            res.status(401); // Forbidden (e.g., username doesn’t exist)
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

}
