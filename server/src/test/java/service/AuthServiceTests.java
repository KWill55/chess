package service;

import dataaccess.InMemoryAuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthService authService;
    private InMemoryAuthDAO inMemoryAuthDAO;

    @BeforeEach
    void setUp() {
        inMemoryAuthDAO = new InMemoryAuthDAO();  // Creates a new AuthDAO for each test
        authService = new AuthService(inMemoryAuthDAO);
    }

    //createAuth: Successfully create an auth token
    @Test
    @DisplayName("Successfully create an auth token")
    void testCreateAuthSuccess() throws DataAccessException {
        // Create an auth Token for the given username
        String username = "kenny";
        String authToken = authService.createAuth(username);
        assertNotNull(authToken, "Auth token should not be null");

        // Verify that retrieving the authToken is successful
        AuthData storedAuth = authService.getAuth(authToken);
        assertNotNull(storedAuth);
        assertEquals(username, storedAuth.username());
    }

    // createAuth: Fail to create an auth token for a null username
    @Test
    @DisplayName("Fail to create an auth token for a null username")
    void testCreateAuthFailure() {
        //create invalid AuthData (null)
        AuthData invalidAuthData = new AuthData(null, null);

        // make sure that exception is thrown when creating invalid Auth Data
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            inMemoryAuthDAO.createAuth(invalidAuthData);
        });

        assertEquals("Error: Auth token cannot be null", thrown.getMessage());
    }

    // getAuth: Successfully retrieve an existing auth token
    @Test
    @DisplayName("Successfully retrieve an existing auth token")
    void testGetAuthSuccess() throws DataAccessException {
        //Create an
        String username = "kenny";
        String authToken = authService.createAuth(username);

        // Retrieve auth data
        AuthData retrievedAuth = authService.getAuth(authToken);
        assertNotNull(retrievedAuth);
        assertEquals(username, retrievedAuth.username());
    }

    // getAuth: Fail to retrieve a non-existent auth token
    @Test
    @DisplayName("Fail to retrieve a non-existent auth token")
    void testGetAuthFailure() {
        // Attempt to retrieve an authToken that does not exist
        String invalidAuthToken = "invalidToken";

        //ensure that an exception is thrown
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

        // Delete the auth token
        assertDoesNotThrow(() -> authService.deleteAuth(authToken));

        // Verify it no longer exists
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
        String username = "kenny";
        authService.createAuth(username);
        authService.createAuth("alice");

        authService.clear(); // Clear all authentication data

        // Verify database is empty
        DataAccessException thrown1 = assertThrows(DataAccessException.class, () -> authService.getAuth("kenny"));
        DataAccessException thrown2 = assertThrows(DataAccessException.class, () -> authService.getAuth("alice"));

        assertEquals("Error: authToken not found", thrown1.getMessage());
        assertEquals("Error: authToken not found", thrown2.getMessage());
    }
}