package handler;

import service.UserService;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class ClearHandler extends BaseHandler<Void> {
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    public ClearHandler(UserService userService, AuthService authService, GameService gameService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;
    }

    @Override
    protected Void parseRequest(Request req) {
        return null; // No request body needed
    }

    @Override
    protected Object handleRequest(Void requestData, Request req, Response res) {
        try {
            userService.clear();
            authService.clear();
            gameService.clear();
            return Map.of("message", "Database cleared");
        } catch (Exception e) {
            res.status(500); // Internal server error
            return Map.of("message", "Error: Could not clear database");
        }
    }
}
