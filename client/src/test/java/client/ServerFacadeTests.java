package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import exception.ResponseException;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearServer() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // ---------- Register Tests ----------

    @Test
    void register_validUser_success() throws Exception {
        var response = facade.register("kenny", "myPassword", "kenny@gmail.com");
        assertNotNull(response.authToken());
        assertEquals("kenny", response.username());
    }

    @Test
    void register_duplicateUser_fails() throws Exception {
        facade.register("maddie", "maddie_password", "maddie@gmail.com");
        assertThrows(ResponseException.class, () -> {
            facade.register("maddie", "maddie_password", "maddie@gmail.com");
        });
    }

    // ---------- Login Tests ----------

    @Test
    void login_validUser_success() throws Exception {
        facade.register("kayla", "kayla_password", "kayla@gmail.com");
        var response = facade.login("kayla", "kayla_password");
        assertNotNull(response.authToken());
    }

    @Test
    void login_wrongPassword_fails() throws Exception {
        facade.register("kara", "correct_password", "kara@gmail.com");
        assertThrows(ResponseException.class, () -> {
            facade.login("kara", "incorrect_password");
        });
    }

    // ---------- Logout Tests ----------

    @Test
    void logout_validUser_success() throws Exception {
        var regResponse = facade.register("user1", "password1", "user1@gmail.com");
        String authToken = regResponse.authToken();
        // Logout should succeed without throwing
        var logoutResponse = facade.logout(authToken);
        assertNotNull(logoutResponse);
        // After logout, using the same token should fail (e.g., listing games)
        assertThrows(ResponseException.class, () -> facade.listGames(authToken));
    }

    @Test
    void logout_invalidToken_fails() {
        assertThrows(ResponseException.class, () -> {
            facade.logout("invalid-token");
        });
    }

    // ---------- Create Game Tests ----------

    @Test
    void createGame_validAuth_success() throws Exception {
        var regResponse = facade.register("gameUser", "password", "gameUser@gmail.com");
        String authToken = regResponse.authToken();
        var createResponse = facade.createGame(authToken, "TestGame");
        assertTrue(createResponse.gameID() > 0);
    }

    @Test
    void createGame_invalidAuth_fails() {
        assertThrows(ResponseException.class, () -> {
            facade.createGame("invalid-token", "TestGame");
        });
    }

    // ---------- List Games Tests ----------

    @Test
    void listGames_validAuth_success() throws Exception {
        var regResponse = facade.register("listUser", "password", "listUser@gmail.com");
        String authToken = regResponse.authToken();
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
    void listGames_invalidAuth_fails() {
        assertThrows(ResponseException.class, () -> {
            facade.listGames("invalid-token");
        });
    }

    // ---------- Join Game Tests ----------

    @Test
    void joinGame_validParams_success() throws Exception {
        var regResponse = facade.register("joinUser", "password", "joinUser@gmail.com");
        String authToken = regResponse.authToken();
        // Create a game first
        CreateGameResponse createResponse = facade.createGame(authToken, "JoinableGame");
        int gameID = createResponse.gameID();
        // Join the game as WHITE
        JoinGameResponse joinResponse = facade.joinGame(authToken, gameID, "WHITE");
        assertNotNull(joinResponse);
    }

    @Test
    void joinGame_nonexistentGame_fails() throws Exception {
        var regResponse = facade.register("joinFailUser", "password", "joinFailUser@gmail.com");
        String authToken = regResponse.authToken();
        // Use a gameID that doesn't exist (e.g., 9999)
        assertThrows(ResponseException.class, () -> {
            facade.joinGame(authToken, 9999, "WHITE");
        });
    }

    @Test
    void joinGame_invalidAuth_fails() {
        assertThrows(ResponseException.class, () -> {
            facade.joinGame("invalid-token", 1, "BLACK");
        });
    }

    // ---------- Observe Game Tests ----------

//    @Test
//    void observeGame_validParams_success() throws Exception {
//        var regResponse = facade.register("observeUser", "password", "observeUser@gmail.com");
//        String authToken = regResponse.authToken();
//        // Create a game to observe
//        CreateGameResponse createResponse = facade.createGame(authToken, "ObservableGame");
//        int gameID = createResponse.gameID();
//        JoinGameResponse observeResponse = facade.observeGame(authToken, gameID);
//        assertNotNull(observeResponse);
//    }

//    @Test
//    void observeGame_nonexistentGame_fails() throws Exception {
//        var regResponse = facade.register("observeFailUser", "password", "observeFailUser@gmail.com");
//        String authToken = regResponse.authToken();
//        // Attempt to observe a game that doesn't exist
//        assertThrows(ResponseException.class, () -> {
//            facade.observeGame(authToken, 9999);
//        });
//    }

    // ---------- Clear Tests ----------

    @Test
    void clear_success() throws Exception {
        // Register, create a game, then clear the database
        var regResponse = facade.register("clearUser", "password", "clearUser@gmail.com");
        String authToken = regResponse.authToken();
        facade.createGame(authToken, "GameToClear");
        facade.clear();
        ListGamesResponse listResponse = facade.listGames(authToken);
        // Expect the game list to be empty after clearing
        assertEquals(0, listResponse.games().size());
    }
}
