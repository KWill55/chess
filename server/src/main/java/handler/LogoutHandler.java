package handler;

import dataaccess.DataAccessException;
import service.AuthService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class LogoutHandler extends BaseHandler<Void> {
    private final AuthService authService;

    public LogoutHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected Void parseRequest(Request req) {
        return null; // No request body needed
    }

    @Override
    protected Object handleRequest(Void requestData, Request req, Response res) {
        String authToken;

        try {
            authToken = getAuthToken(req);
        } catch (Exception e) {
            res.status(401); // Unauthorized
            return Map.of("message", "Error: Missing or invalid auth token");
        }

        try {
            authService.deleteAuth(authToken);
            return Map.of("message", "Logout successful");
        } catch (DataAccessException e) {
            res.status(401); // Unauthorized (invalid token)
            return Map.of("message", "Error: Unauthorized");
        }
    }
}
