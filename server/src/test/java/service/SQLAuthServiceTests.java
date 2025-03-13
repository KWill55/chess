package service;

import dataaccess.SQLAuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthServiceTests {
    private static SQLAuthDAO authDAO;
    private AuthService authService;

    @BeforeAll
    static void setupDatabase() throws DataAccessException {
        DatabaseManager.createDatabase(); // Ensure database is created
        authDAO = new SQLAuthDAO();
        authDAO.clear(); // Clear existing authentication data
    }

    @BeforeEach
    void setUp() {
        authService = new AuthService(authDAO);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        authDAO.clear(); // Clear auth table after each test
    }

    @AfterAll
    static void cleanup() throws DataAccessException {
        authDAO.clear(); // Final cleanup to avoid leftover data
    }

    // createAuth: Successfully create an auth token
    @Test
    @DisplayName("Successfully create an auth token")
    void testCreateAuthSuccess() throws DataAccessException {
        String username = "kenny";
        String authToken = authService.createAuth(username);
        assertNotNull(authToken, "Auth token should not be null");

        AuthData storedAuth = authService.getAuth(authToken);
        assertNotNull(storedAuth);
        assertEquals(username, storedAuth.username());
    }

    // createAuth: Fail to create an auth token for a null username
    @Test
    @DisplayName("Fail to create an auth token for a null username")
    void testCreateAuthFailure() {
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            authService.createAuth(null);
        });
        assertEquals("Error: Username cannot be null", thrown.getMessage());
    }

    // getAuth: Successfully retrieve an existing auth token
    @Test
    @DisplayName("Successfully retrieve an existing auth token")
    void testGetAuthSuccess() throws DataAccessException {
        String username = "kenny";
        String authToken = authService.createAuth(username);

        AuthData retrievedAuth = authService.getAuth(authToken);
        assertNotNull(retrievedAuth);
        assertEquals(username, retrievedAuth.username());
    }

    // getAuth: Fail to retrieve a non-existent auth token
    @Test
    @DisplayName("Fail to retrieve a non-existent auth token")
    void testGetAuthFailure() {
        String invalidAuthToken = "invalidToken";
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            authService.getAuth(invalidAuthToken);
        });
        assertEquals("Error: authToken not found", thrown.getMessage());
    }

    // deleteAuth: Successfully delete an auth token
    @Test
    @DisplayName("Successfully delete an auth token")
    void testDeleteAuthSuccess() throws DataAccessException {
        String username = "kenny";
        String authToken = authService.createAuth(username);

        assertDoesNotThrow(() -> authService.deleteAuth(authToken));

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> authService.getAuth(authToken));
        assertEquals("Error: authToken not found", thrown.getMessage());
    }

    // deleteAuth: Fail to delete a non-existent auth token
    @Test
    @DisplayName("Fail to delete a non-existent auth token")
    void testDeleteAuthFailure() {
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> authService.deleteAuth("invalidToken"));
        assertEquals("Error: authToken not found", thrown.getMessage());
    }

    // clear: Successfully clear all auth data
    @Test
    @DisplayName("Successfully clear all auth data")
    void testClearAuth() throws DataAccessException {
        authService.createAuth("kenny");
        authService.createAuth("alice");

        authService.clear(); // Clear all authentication data

        DataAccessException thrown1 = assertThrows(DataAccessException.class, () -> authService.getAuth("kenny"));
        DataAccessException thrown2 = assertThrows(DataAccessException.class, () -> authService.getAuth("alice"));

        assertEquals("Error: authToken not found", thrown1.getMessage());
        assertEquals("Error: authToken not found", thrown2.getMessage());
    }
}

