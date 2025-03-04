package server;

import dataaccess.GameDAO;
import handler.*;
import service.UserService;
import service.AuthService;
import service.GameService;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
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
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

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
        System.out.println("Server running on port: " + desiredPort);
        Spark.port(desiredPort);

        // Serves static files (if applicable)
        Spark.staticFiles.location("web");

        // Define API routes and attach corresponding request handlers
        Spark.post("/user", new RegisterHandler(userService, authService));    // User registration
        Spark.post("/session", new LoginHandler(userService, authService));    // User login
        Spark.delete("/session", new LogoutHandler(authService));              // User logout
        Spark.get("/game", new ListGamesHandler(gameService, authService));    // List available games
        Spark.post("/game", new CreateGameHandler(gameService, authService));  // Create a new game
        Spark.put("/game", new JoinGameHandler(gameService, authService));     // Join an existing game
        Spark.delete("/db", new ClearHandler(userService, authService, gameService)); // Clear all data

        // Initialize the Spark server
        Spark.init();
        Spark.awaitInitialization(); // Wait for the server to start fully
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
