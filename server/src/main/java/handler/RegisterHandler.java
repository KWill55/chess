package handler;

import model.RegisterRequest;
import model.RegisterResponse;
import model.UserData;
import service.AuthService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class RegisterHandler extends BaseHandler<RegisterRequest> {
    private final UserService userService;
    private final AuthService authService;

    public RegisterHandler(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    protected RegisterRequest parseRequest(Request req) {
        return gson.fromJson(req.body(), RegisterRequest.class);
    }

    @Override
    protected Object handleRequest(RegisterRequest request, Request req, Response res) {
        if (request.username() == null || request.password() == null || request.email() == null) {
            res.status(400); // Bad request
            return gson.toJson(Map.of("message", "Error: bad request"));
        }

        try {
            // Convert RegisterRequest to UserData
            UserData newUser = new UserData(request.username(), request.password(), request.email());

            // Create user
            userService.createUser(newUser);

            // Generate auth token for the newly registered user
            String authToken = authService.createAuth(request.username());

            res.status(200);
            return gson.toJson(new RegisterResponse(request.username(), authToken));

        } catch (Exception e) {
            res.status(403); // Forbidden (username already taken)
            return gson.toJson(Map.of("message", "Error: already taken"));
        }
    }
}
