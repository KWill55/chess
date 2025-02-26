package server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import model.LoginRequest;
import model.LoginResponse;
//import server.websocket.WebSocketHandler;
import service.UserService;
import service.AuthService;
import service.GameService;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import spark.*;
import java.util.Map;


public class Server {
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    public Server() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();

        this.userService = new UserService(userDAO, authDAO);
        this.authService = new AuthService(authDAO, userDAO);
        this.gameService = new GameService()
    }

    // Constructor with dependencies (for flexibility in other use cases)
    //overloaded version
    public Server(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;
    }

    public int run(int desiredPort) {
        System.out.println("Server running on port: " + desiredPort);
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
//        Spark.post("/user", this::registerUser); //for registering a user
        Spark.post("/session", this::loginUser); //for logging in
//        Spark.delete("/session", this::deleteUser); //for logging out
//        Spark.get("/game", this::listGames); //for listing games
//        Spark.post("/game", this::createGame); //for creating a game
//        Spark.put("/game", this::joinGame); //for joining a game
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

    /*
    Login User (server/handler)
    - convert JSON to Java object (Handler)
    - Call UserService.login with loginRequest as parameter
    - update response if a bad request
    - return JSON string with username and authToken
     */
    private Object loginUser(Request req, Response res) throws DataAccessException {

        //TODO maybe separate handlers into their own files later?
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

    private Object clear(Request req, Response res) {
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
