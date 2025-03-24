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

}