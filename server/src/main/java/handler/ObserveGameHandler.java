package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.JoinGameRequest;
import model.JoinGameResponse;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;
import java.util.Map;

public class ObserveGameHandler extends BaseHandler<JoinGameRequest> {
    private final GameService gameService;
    private final AuthService authService;

    /**
     * Constructor for the ObserveGameHandler.
     * @param gameService Service for handling game operations.
     * @param authService Service for authentication.
     */
    public ObserveGameHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    @Override
    protected JoinGameRequest parseRequest(Request req) {
        // Expect a JSON body that has the gameID (and a null or missing playerColor)
        return gson.fromJson(req.body(), JoinGameRequest.class);
    }

    @Override
    protected Object handleRequest(JoinGameRequest request, Request req, Response res) {
        String authToken = getAuthToken(req);  // Retrieve auth token from the header

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Missing auth token"));
        }

        try {
            // Validate the auth token and get the associated username
            String username = authService.getUserFromAuth(authToken);
            if (username == null) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: Unauthorized"));
            }

            int gameID = request.gameID();

            // Dummy observe behavior: if playerColor is null, we treat this as an observe request.
            if (request.playerColor() == null) {
                res.status(200);
                return gson.toJson(new JoinGameResponse());
            } else {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: Invalid parameters for observe game"));
            }
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
