package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import exception.ResponseException;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    // Use an instance field to store the current auth token
    private String currentAuthToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearServer() throws ResponseException {
        facade.clear();
        // Reset currentAuthToken between tests.
        currentAuthToken = null;
    }

    @AfterEach
    void tearDown() {
        if (currentAuthToken != null) {
            try {
                facade.logout(currentAuthToken);
            } catch (ResponseException e) {
                // Optionally log the error; not failing the test on tearDown issues.
                System.out.println("Error during logout: " + e.getMessage());
            }
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // ---------- Register Tests ----------

    @Test
    void registerValidUserSuccess() throws Exception {
        var response = facade.register("kenny", "myPassword", "kenny@gmail.com");
        assertNotNull(response.authToken());
        assertEquals("kenny", response.username());
        // Store token for tearDown cleanup
        currentAuthToken = response.authToken();
    }

    @Test
    void registerDuplicateUserFails() throws Exception {
        facade.register("maddie", "maddie_password", "maddie@gmail.com");
        assertThrows(ResponseException.class, () -> {
            facade.register("maddie", "maddie_password", "maddie@gmail.com");
        });
    }

    // ---------- Login Tests ----------

    @Test
    void loginValidUserSuccess() throws Exception {
        facade.register("kayla", "kayla_password", "kayla@gmail.com");
        var response = facade.login("kayla", "kayla_password");
        assertNotNull(response.authToken());
        currentAuthToken = response.authToken();
    }

    @Test
    void loginWrongPasswordFails() throws Exception {
        facade.register("kara", "correct_password", "kara@gmail.com");
        assertThrows(ResponseException.class, () -> {
            facade.login("kara", "incorrect_password");
        });
    }

    // ---------- Logout Tests ----------

    @Test
    void logoutValidUserSuccess() throws Exception {
        var regResponse = facade.register("user1", "password1", "user1@gmail.com");
        String authToken = regResponse.authToken();
        // Logout should succeed without throwing
        var logoutResponse = facade.logout(authToken);
        assertNotNull(logoutResponse);
        // After logout, using the same token should fail (e.g., listing games)
        assertThrows(ResponseException.class, () -> facade.listGames(authToken));
    }

    @Test
    void logoutInvalidTokenFails() {
        assertThrows(ResponseException.class, () -> {
            facade.logout("invalid-token");
        });
    }

    // ---------- Create Game Tests ----------

    @Test
    void createGameValidAuthSuccess() throws Exception {
        // Generate a unique username using System.nanoTime()
        String uniqueUsername = "gameUser" + System.nanoTime();
        var regResponse = facade.register(uniqueUsername, "password", uniqueUsername + "@example.com");
        currentAuthToken = regResponse.authToken();

        // Now create a game using this new user.
        var createResponse = facade.createGame(currentAuthToken, "TestGame");
        assertTrue(createResponse.gameID() > 0);
    }

    @Test
    void createGameInvalidAuthFails() {
        assertThrows(ResponseException.class, () -> {
            facade.createGame("invalid-token", "TestGame");
        });
    }

    // ---------- List Games Tests ----------

    @Test
    void listGamesValidAuthSuccess() throws Exception {
        var regResponse = facade.register("listUser", "password", "listUser@gmail.com");
        String authToken = regResponse.authToken();
        currentAuthToken = authToken;
        // Initially the list should be empty
        ListGamesResponse listResponse = facade.listGames(authToken);
        assertNotNull(listResponse.games());
        assertEquals(0, listResponse.games().size());

        // Create a game and then the list should show it
        facade.createGame(authToken, "GameOne");
        listResponse = facade.listGames(authToken);
        assertTrue(listResponse.games().size() > 0);
    }

    @Test
    void listGamesInvalidAuthFails() {
        assertThrows(ResponseException.class, () -> {
            facade.listGames("invalid-token");
        });
    }

    // ---------- Join Game Tests ----------

    @Test
    void joinGameValidParamsSuccess() throws Exception {
        var regResponse = facade.register("joinUser", "password", "joinUser@gmail.com");
        String authToken = regResponse.authToken();
        currentAuthToken = authToken;
        // Create a game first
        CreateGameResponse createResponse = facade.createGame(authToken, "JoinableGame");
        int gameID = createResponse.gameID();
        // Join the game as WHITE
        JoinGameResponse joinResponse = facade.joinGame(authToken, gameID, "WHITE");
        assertNotNull(joinResponse);
    }

    @Test
    void joinGameNonexistentGameFails() throws Exception {
        var regResponse = facade.register("joinFailUser", "password", "joinFailUser@gmail.com");
        String authToken = regResponse.authToken();
        currentAuthToken = authToken;
        // Use a gameID that doesn't exist (e.g., 9999)
        assertThrows(ResponseException.class, () -> {
            facade.joinGame(authToken, 9999, "WHITE");
        });
    }

    @Test
    void joinGameInvalidAuthFails() {
        assertThrows(ResponseException.class, () -> {
            facade.joinGame("invalid-token", 1, "BLACK");
        });
    }

    // ---------- Observe Game Tests ----------

//    @Test
//    void observeGame_validAuth_success() throws Exception {
//        // Register a new user for observation
//        var regResponse = facade.register("observeUser", "password", "observeUser@gmail.com");
//        String authToken = regResponse.authToken();
//        currentAuthToken = authToken; // store for tearDown
//
//        // Create a new game to be observed
//        CreateGameResponse createResponse = facade.createGame(authToken, "ObservableGame");
//        int gameID = createResponse.gameID();
//
//        // Call observeGame (using null for color indicates observation)
//        JoinGameResponse observeResponse = facade.observeGame(authToken, gameID);
//        assertNotNull(observeResponse, "Expected a valid response when observing a game");
//    }

    @Test
    void observeGameNonexistentGameFails() throws Exception {
        // Register a new user
        var regResponse = facade.register("observeFailUser", "password", "observeFailUser@gmail.com");
        String authToken = regResponse.authToken();
        currentAuthToken = authToken;

        // Attempt to observe a game with an ID that doesn't exist (e.g., 9999)
        assertThrows(ResponseException.class, () -> {
            facade.observeGame(authToken, 9999);
        });
    }


    // ---------- Clear Test? ----------

}
