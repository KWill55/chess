package handler;

import model.LoginRequest;
import model.LoginResponse;
import service.AuthService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class LoginHandler extends BaseHandler<LoginRequest> {
    private final UserService userService;
    private final AuthService authService;

    public LoginHandler(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    protected LoginRequest parseRequest(Request req) {
        return gson.fromJson(req.body(), LoginRequest.class);
    }

    @Override
    protected Object handleRequest(LoginRequest request, Request req, Response res) {
        if (request.username() == null || request.password() == null) {
            res.status(400); // Bad request
            return gson.toJson(Map.of("message", "Error: bad request"));
        }

        try {
            // Use getUser() to check if the user exists
            var user = userService.getUser(request.username());

            // If the user doesn't exist or password doesn't match, return Unauthorized
            if (user == null || !user.password().equals(request.password())) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: Unauthorized"));
            }

            // Generate auth token
            String authToken = authService.createAuth(request.username());
            return gson.toJson(new LoginResponse(request.username(), authToken));

        } catch (Exception e) {
            res.status(401); // Internal server error
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }
    }
}
