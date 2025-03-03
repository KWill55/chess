package handler;

import dataaccess.DataAccessException;
import model.GameData;
import model.JoinGameRequest;
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

        if (request.gameID() <= 0 || request.playerColor() == null) {
            res.status(400); // Bad request
            return Map.of("message", "Error: bad request");
        }

        try {
            // Retrieve the game
            GameData game = gameService.getGame(request.gameID());
            if (game == null) {
                res.status(404); // Not found
                return Map.of("message", "Error: Game not found");
            }

            // Get the username from the auth token
            String username = authService.getUserFromAuth(authToken);
            if (username == null) {
                res.status(401); // Unauthorized
                return Map.of("message", "Error: Unauthorized");
            }

            // Check if the player color is available
            if (request.playerColor().equals("WHITE") && game.whiteUsername() != null ||
                    request.playerColor().equals("BLACK") && game.blackUsername() != null) {
                res.status(403); // Forbidden (spot already taken)
                return Map.of("message", "Error: Spot already taken");
            }

            // Update the game with the new player
            GameData updatedGame = new GameData(
                    game.gameID(),
                    request.playerColor().equals("WHITE") ? username : game.whiteUsername(),
                    request.playerColor().equals("BLACK") ? username : game.blackUsername(),
                    game.gameName(),
                    game.game()
            );

            // Save the updated game
            gameService.updateGame(request.gameID(), updatedGame);

            return Map.of("message", "Joined game successfully");
        } catch (DataAccessException e) {
            res.status(403); // Forbidden (e.g., database issue)
            return Map.of("message", "Error: " + e.getMessage());
        }
    }
}
