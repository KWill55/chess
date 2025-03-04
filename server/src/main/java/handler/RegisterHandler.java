package handler;

import model.RegisterRequest;
import model.RegisterResponse;
import model.UserData;
import service.AuthService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * Handles user registration requests.
 * This handler processes new user registrations by creating a new user and generating an authentication token.
 */
public class RegisterHandler extends BaseHandler<RegisterRequest> {
    private final UserService userService;
    private final AuthService authService;

    /**
     * Constructor for RegisterHandler.
     *
     * @param userService Service that handles user-related operations such as creating users.
     * @param authService Service that manages authentication tokens.
     */
    public RegisterHandler(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * Parses the incoming HTTP request into a RegisterRequest object.
     *
     * @param req The Spark Request object containing the request body.
     * @return A RegisterRequest object containing the registration details.
     */
    @Override
    protected RegisterRequest parseRequest(Request req) {
        return gson.fromJson(req.body(), RegisterRequest.class);
    }

    /**
     * Handles the user registration request.
     *
     * @param request The parsed RegisterRequest containing username, password, and email.
     * @param req The Spark Request object.
     * @param res The Spark Response object.
     * @return A JSON response indicating success or an error message.
     */
    @Override
    protected Object handleRequest(RegisterRequest request, Request req, Response res) {
        // Validate request data: Ensure that username, password, and email are not null
        if (request.username() == null || request.password() == null || request.email() == null) {
            res.status(400); // 400 Bad Request (missing required fields)
            return gson.toJson(Map.of("message", "Error: bad request"));
        }

        try {
            // Convert RegisterRequest to UserData for storage
            UserData newUser = new UserData(request.username(), request.password(), request.email());

            // Create the user in the system
            userService.createUser(newUser);

            // Generate an authentication token for the newly registered user
            String authToken = authService.createAuth(request.username());

            // Return success response with the newly generated auth token
            res.status(200); // 200 OK (successful registration)
            return gson.toJson(new RegisterResponse(request.username(), authToken));

        } catch (Exception e) {
            // 403 Forbidden (username already exists)
            res.status(403);
            return gson.toJson(Map.of("message", "Error: already taken"));
        }
    }
}
