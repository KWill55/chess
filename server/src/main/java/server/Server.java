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

        this.userService = new UserService(userDAO, authDAO);
        this.authService = new AuthService(authDAO, userDAO);
        this.gameService = new GameService(gameDAO, authDAO, userDAO);
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
    /// User
    /////////////////////////////////////////////////////////////////////////////////

    private Object registerUser(Request req, Response res) {
        var gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);

        try {
            RegisterResponse response = userService.register(registerRequest);
            res.status(200);
            return gson.toJson(response);
        } catch (Exception e) {
            res.status(400); // Bad request (e.g., username already taken)
            return gson.toJson(Map.of("message", "Error: Could not register user"));
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// Auth stuff
    /////////////////////////////////////////////////////////////////////////////////

    /*
    Login User (server/handler)
    - convert JSON to Java object (Handler)
    - Call UserService.login with loginRequest as parameter
    - update response if a bad request
    - return JSON string with username and authToken
     */
    private Object loginUser(Request req, Response res) throws DataAccessException {

        //TODO separate handlers into their own files
        //convert JSON to Java object (Handler)
        var gson = new Gson();
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

        // Initialize services
        UserService userService = new UserService(new UserDAO(), new AuthDAO());
        AuthService authService = new AuthService(new AuthDAO(), new UserDAO());

        //call login from AuthService
        LoginResponse response;
        try {
            response = authService.login(loginRequest);
            res.status(200); // Success
            return gson.toJson(response);
        } catch (DataAccessException e) {
            res.status(401); // Unauthorized
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
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
            authService.logout(authToken);
            res.status(200);
            return gson.toJson(Map.of("message", "Logout successful"));
        } catch (Exception e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// Game
    /////////////////////////////////////////////////////////////////////////////////

    private Object listGames(Request req, Response res) {
        var gson = new Gson();
        String authToken = req.headers("Authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Missing auth token"));
        }

        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);

        try {
            ListGamesResponse response = gameService.listGames(listGamesRequest);
            res.status(200);
            return gson.toJson(response);
        } catch (Exception e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }
    }

    private Object createGame(Request req, Response res) {
        var gson = new Gson();
        String authToken = req.headers("Authorization");
        //TODO include authToken into request
        CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Missing auth token"));
        }

        try {
            CreateGameResponse game = gameService.createGame(createGameRequest);
            res.status(200);
            return gson.toJson(game);
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: Could not create game"));
        }
    }

    private Object joinGame(Request req, Response res) {
        var gson = new Gson();
        String authToken = req.headers("Authorization");
        JoinGameRequest joinRequest = gson.fromJson(req.body(), JoinGameRequest.class);

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Missing auth token"));
        }


        try {
            gameService.joinGame(joinRequest);
            res.status(200);
            return gson.toJson(Map.of("message", "Joined game successfully"));
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: Could not join game"));
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// Clear
    /////////////////////////////////////////////////////////////////////////////////

    private Object clear(Request req, Response res) {
        var gson = new Gson();
        try {
            userService.clearAll();
            authService.clearAll();
            gameService.clearAll();
            res.status(200);
            return gson.toJson(Map.of("message", "Database cleared"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: Could not clear database"));
        }
    }
}
