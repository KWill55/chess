package handler;

import dataaccess.DataAccessException;
import model.CreateGameRequest;
import model.CreateGameResponse;
import model.LoginResponse;
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
        String authToken = getAuthToken(req);

        if (request.gameName() == null || request.gameName().isBlank()) {
            res.status(400); // Bad request
            return gson.toJson(Map.of("message", "Error: bad request"));
        }

        try {
            int gameID = gameService.createGame(request.gameName());
            res.status(200);
            return gson.toJson(new CreateGameResponse(gameID));
        } catch (DataAccessException e) {
            res.status(403); // Forbidden (e.g., username doesn't exist)
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
