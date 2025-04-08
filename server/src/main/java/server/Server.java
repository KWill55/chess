package server;

import dataaccess.*;
import handler.*;
import service.UserService;
import service.AuthService;
import service.GameService;
import spark.*;

/**
 * The main server class that sets up the application and handles incoming HTTP requests.
 * Uses Spark framework for defining API endpoints.
 */
public class Server {
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    /**
     * Default constructor that initializes the server with fresh DAOs and services.
     */
    public Server() {
        // Instantiate DAO (Data Access Object) components for user, auth, and game data management
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        GameDAO gameDAO = new SQLGameDAO();

        // Initialize services, passing DAO dependencies
        this.userService = new UserService(userDAO);
        this.authService = new AuthService(authDAO);
        this.gameService = new GameService(gameDAO);
    }

    /**
     * Overloaded constructor allowing dependency injection of services.
     *
     * @param userService  Handles user-related operations
     * @param authService  Manages authentication tokens
     * @param gameService  Manages game-related operations
     */
    public Server(UserService userService, AuthService authService, GameService gameService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;
    }

    /**
     * Starts the server and sets up the API endpoints.
     *
     * @param desiredPort The port number on which the server should run.
     * @return The actual port the server is running on.
     */
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        // Register WebSocket BEFORE routes!
        WebSocketHandler webSocketHandler = new WebSocketHandler();
        Spark.webSocket("/ws", webSocketHandler);

        Spark.staticFiles.location("web");

        // Now it's safe to define your REST routes
        Spark.post("/user", new RegisterHandler(userService, authService));
        Spark.post("/session", new LoginHandler(userService, authService));
        Spark.delete("/session", new LogoutHandler(authService));
        Spark.get("/game", new ListGamesHandler(gameService, authService));
        Spark.post("/game", new CreateGameHandler(gameService, authService));
        Spark.put("/game", new JoinGameHandler(gameService, authService));
        Spark.delete("/db", new ClearHandler(userService, authService, gameService));

        Spark.init();
        Spark.awaitInitialization(); // Wait for full startup
        System.out.println("♕ 240 Chess Server fully initialized!");

        return Spark.port();
    }



    /**
     * Retrieves the current port number the server is running on.
     *
     * @return The port number.
     */
    public int port() {
        return Spark.port();
    }

    /**
     * Stops the server and ensures graceful shutdown.
     */
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
