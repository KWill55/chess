package handler;

import dataaccess.DataAccessException;
import service.AuthService;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * Handles user logout requests.
 * This handler processes logout requests by deleting the user's authentication token.
 */
public class LogoutHandler extends BaseHandler<Void> {
    private final AuthService authService;

    /**
     * Constructor for LogoutHandler.
     *
     * @param authService Handles authentication-related operations like deleting auth tokens.
     */
    public LogoutHandler(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Since logout does not require a request body, this method returns null.
     *
     * @param req The Spark Request object.
     * @return null (no request body needed).
     */
    @Override
    protected Void parseRequest(Request req) {
        return null; // No request body needed
    }

    /**
     * Handles the logout request by deleting the authentication token.
     *
     * @param requestData No request data needed for logout.
     * @param req The Spark Request object.
     * @param res The Spark Response object.
     * @return A JSON response indicating success or an error message.
     */
    @Override
    protected Object handleRequest(Void requestData, Request req, Response res) {
        String authToken;

        // Attempt to retrieve the auth token from request headers
        try {
            authToken = getAuthToken(req);
        } catch (Exception e) {
            res.status(401); // 401 Unauthorized if auth token is missing or invalid
            return gson.toJson(Map.of("message", "Error: Missing or invalid auth token"));
        }

        // Attempt to delete the auth token (logging out the user)
        try {
            authService.deleteAuth(authToken);
            res.status(200); // 200 OK for successful logout
            return gson.toJson(Map.of("message", "Logout successful"));
        } catch (DataAccessException e) {
            res.status(401); // 401 Unauthorized if the auth token is invalid
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }
    }
}
