package handler;

import service.UserService;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;
import java.util.Map;

/**
 * The ClearHandler class is responsible for handling requests to clear all stored data.
 * This includes users, authentication tokens, and games.
 * This class extends BaseHandler and overrides methods to process requests.
 */
public class ClearHandler extends BaseHandler<Void> {

    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    /**
     * Constructor for ClearHandler.
     * Initializes service dependencies to allow clearing of data.
     *
     * @param userService Handles user-related data.
     * @param authService Handles authentication token data.
     * @param gameService Handles game-related data.
     */
    public ClearHandler(UserService userService, AuthService authService, GameService gameService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;
    }

    /**
     * Parses the incoming request.
     * Since the clear operation does not require a request body, this method returns null.
     *
     * @param req The Spark Request object.
     * @return Always returns null since no request data is needed.
     */
    @Override
    protected Void parseRequest(Request req) {
        return null; // No request body needed for this request
    }

    /**
     * Handles the request to clear all stored data.
     * Calls the clear() method on all service classes to remove users, authentication tokens, and games.
     *
     * @param requestData Unused, since no data is required for this operation.
     * @param req The Spark Request object.
     * @param res The Spark Response object.
     * @return A JSON-formatted success message if clearing is successful, otherwise an error message.
     */
    @Override
    protected Object handleRequest(Void requestData, Request req, Response res) {
        try {
            // Call the clear methods for all services to remove all stored data
            userService.clear();
            authService.clear();
            gameService.clear();

            // Set HTTP status to 200 (OK) indicating success
            res.status(200);
            return gson.toJson(Map.of("message", "Database cleared"));

        } catch (Exception e) {
            // If an error occurs, return an internal server error response
            res.status(500); // Internal Server Error
            return gson.toJson(Map.of("message", "Error: Could not clear database"));
        }
    }
}
