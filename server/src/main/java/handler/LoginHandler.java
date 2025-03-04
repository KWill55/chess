package handler;

import model.LoginRequest;
import model.LoginResponse;
import service.AuthService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * Handles user login requests.
 * This handler processes login requests by validating credentials and generating authentication tokens.
 */
public class LoginHandler extends BaseHandler<LoginRequest> {
    private final UserService userService;
    private final AuthService authService;

    /**
     * Constructor for LoginHandler.
     *
     * @param userService Handles user-related operations like retrieving user data.
     * @param authService Handles authentication-related operations like creating auth tokens.
     */
    public LoginHandler(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * Parses the incoming request JSON into a LoginRequest object.
     *
     * @param req The Spark Request object containing the request body.
     * @return A LoginRequest object containing the parsed username and password.
     */
    @Override
    protected LoginRequest parseRequest(Request req) {
        return gson.fromJson(req.body(), LoginRequest.class);
    }

    /**
     * Handles the login request, authenticating the user and returning an authentication token.
     *
     * @param request The parsed LoginRequest object containing username and password.
     * @param req The Spark Request object.
     * @param res The Spark Response object.
     * @return A JSON response containing an authentication token if successful, or an error message otherwise.
     */
    @Override
    protected Object handleRequest(LoginRequest request, Request req, Response res) {
        // Check if the request is valid (username and password must not be null)
        if (request.username() == null || request.password() == null) {
            res.status(400); // 400 Bad Request if username or password is missing
            return gson.toJson(Map.of("message", "Error: bad request"));
        }

        try {
            // Retrieve user from the database
            var user = userService.getUser(request.username());

            // If user is not found or password does not match, return 401 Unauthorized
            if (user == null || !user.password().equals(request.password())) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: Unauthorized"));
            }

            // Generate an authentication token for the user
            String authToken = authService.createAuth(request.username());

            // Return a successful response with the auth token
            return gson.toJson(new LoginResponse(request.username(), authToken));

        } catch (Exception e) {
            // If an unexpected exception occurs, return a generic unauthorized response
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }
    }
}
