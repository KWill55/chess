package handler;

import dataaccess.DataAccessException;
import model.*;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class JoinGameHandler extends BaseHandler<JoinGameRequest> {
    private final GameService gameService;
    private final AuthService authService;

    public JoinGameHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    @Override
    protected JoinGameRequest parseRequest(Request req) {
        return gson.fromJson(req.body(), JoinGameRequest.class);
    }

    @Override
    protected Object handleRequest(JoinGameRequest request, Request req, Response res) {
        String authToken = getAuthToken(req);

        // Authenticate user
        String username;
        try {
            username = authService.getUserFromAuth(authToken);
        } catch (DataAccessException e) {
            res.status(401); // Unauthorized
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }

        if (username == null) {
            res.status(401); // Unauthorized
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }

        // Validate request data
        if (request.gameID() <= 0 || request.playerColor() == null ||
                !(request.playerColor().equalsIgnoreCase("WHITE") || request.playerColor().equalsIgnoreCase("BLACK"))) {
            res.status(400); // Bad request
            return gson.toJson(Map.of("message", "Error: bad request"));
        }

        try {
            // Retrieve the game
            GameData game = gameService.getGame(request.gameID());
            if (game == null) {
                res.status(400); // Bad request (game does not exist)
                return gson.toJson(Map.of("message", "Error: bad request"));
            }

            // Check if the spot is already taken
            if ((request.playerColor().equalsIgnoreCase("WHITE") && game.whiteUsername() != null) ||
                    (request.playerColor().equalsIgnoreCase("BLACK") && game.blackUsername() != null)) {
                res.status(403); // Forbidden (spot already taken)
                return gson.toJson(Map.of("message", "Error: already taken"));
            }

            // Update the game with the new player
            GameData updatedGame = new GameData(
                    game.gameID(),
                    request.playerColor().equalsIgnoreCase("WHITE") ? username : game.whiteUsername(),
                    request.playerColor().equalsIgnoreCase("BLACK") ? username : game.blackUsername(),
                    game.gameName(),
                    game.game()
            );

            // Save the updated game
            gameService.updateGame(request.gameID(), updatedGame);
            res.status(200);
            return gson.toJson(Map.of()); // Empty JSON response for success

        } catch (DataAccessException e) {
            res.status(500); // Internal Server Error
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }



}
