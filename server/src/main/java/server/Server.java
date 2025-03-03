package server;


import com.google.gson.Gson;
import dataaccess.GameDAO;
import service.UserService;
import service.AuthService;
import service.GameService;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import spark.*;
import java.util.Map;
import model.*;


public class Server {
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    public Server() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

        this.userService = new UserService(userDAO);
        this.authService = new AuthService(authDAO);
        this.gameService = new GameService(gameDAO);
    }

    // Constructor with dependencies (for flexibility in other use cases)
    //overloaded version
    public Server(UserService userService, AuthService authService, GameService gameService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;
    }

    public int run(int desiredPort) {
        System.out.println("Server running on port: " + desiredPort);
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser); //for registering a user
        Spark.post("/session", this::loginUser); //for logging in
        Spark.delete("/session", this::logoutUser); //for logging out
        Spark.get("/game", this::listGames); //for listing games
        Spark.post("/game", this::createGame); //for creating a game
        Spark.put("/game", this::joinGame); //for joining a game
        Spark.delete("/db", this::clear); //for clearing application

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// User APIs
    /////////////////////////////////////////////////////////////////////////////////

    private Object registerUser(Request req, Response res) {
        var gson = new Gson();
        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);

        try {
            UserData user = new UserData(request.username(), request.password(), request.email());
            userService.createUser(user);
            res.status(200);
            return gson.toJson(Map.of("message", "User registered successfully"));
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: Could not register user"));
        }
    }

    private Object loginUser(Request req, Response res) {
        var gson = new Gson();
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);

        try {
            UserData user = userService.getUser(request.username());

            if (user == null || !user.password().equals(request.password())) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: Unauthorized"));
            }

            String authToken = authService.createAuth(request.username());
            res.status(200);
            return gson.toJson(new LoginResponse(request.username(), authToken));
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: Could not login"));
        }
    }


    private Object logoutUser(Request req, Response res) {
        var gson = new Gson();
        String authToken = req.headers("Authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Missing auth token"));
        }

        try {
            authService.deleteAuth(authToken);
            res.status(200);
            return gson.toJson(Map.of("message", "Logout successful"));
        } catch (Exception e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// Game  APIs
    /////////////////////////////////////////////////////////////////////////////////

    private Object listGames(Request req, Response res) {
        var gson = new Gson();
        try {
            var games = gameService.listGames();
            res.status(200);
            return gson.toJson(games);
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: Could not list games"));
        }
    }

    private Object createGame(Request req, Response res) {
        var gson = new Gson();
        CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);

        try {
            int gameID = gameService.createGame(request.gameName());
            res.status(200);
            return gson.toJson(Map.of("gameID", gameID));
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: Could not create game"));
        }
    }

    private Object joinGame(Request req, Response res) {
        var gson = new Gson();
        String authToken = req.headers("Authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Missing auth token"));
        }

        JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);

        try {
            // Verify authToken and join the game
//            gameService.updateGame(request.gameID(), authService.getUserFromAuth(authToken), request.color());

            res.status(200);
            return gson.toJson(Map.of("message", "Joined game successfully"));
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: Could not join game"));
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// Clear API
    /////////////////////////////////////////////////////////////////////////////////

    private Object clear(Request req, Response res) {
        var gson = new Gson();
        try {
            userService.clear();
            authService.clear();
            gameService.clear();
            res.status(200);
            return gson.toJson(Map.of("message", "Database cleared"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: Could not clear database"));
        }
    }
}