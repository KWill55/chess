package service;

import dataaccess.SQLAuthDAO;
import dataaccess.DataAccessException;
import dataaccess.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTests {
    private AuthService authService;
    private UserService userService;
    private SQLAuthDAO authDAO;
    private SQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();  // Creates a new AuthDAO for each test
        userDAO = new SQLUserDAO();

        try {
            authDAO.clear();
            userDAO.clear(); // Make sure this clears the Users table
        } catch (DataAccessException e) {
            fail("Setup failed: " + e.getMessage());
        }

        // Insert test user "kenny" (and others as needed)
        try {
            userDAO.createUser(new UserData("kenny", "1234", "kenny@gmail.com"));
            userDAO.createUser(new UserData("alice", "password", "alice@gmail.com"));
        } catch (DataAccessException e) {
            // If the user already exists, you might want to ignore or log the error
            System.out.println("Test user might already exist: " + e.getMessage());
        }

        authService = new AuthService(authDAO);
        userService = new UserService(userDAO);
    }

    //createAuth: Successfully create an auth token
    @Test
    @DisplayName("Successfully create an authToken")
    void testCreateAuthSuccess() throws DataAccessException {
        // Create an auth Token for the given username
        String username = "kenny";
        String authToken = authService.createAuth(username);
        assertNotNull(authToken, "authToken should not be null");

        // Verify that retrieving the authToken is successful
        AuthData storedAuth = authService.getAuth(authToken);
        assertNotNull(storedAuth);
        assertEquals(username, storedAuth.username());
    }

    // createAuth: Fail to create an auth token for a null username
    @Test
    @DisplayName("Fail to create an authToken for a null username")
    void testCreateAuthFailure() {
        //create invalid AuthData (null)
        AuthData invalidAuthData = new AuthData(null, null);

        // make sure that exception is thrown when creating invalid Auth Data
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(invalidAuthData);
        });

        assertEquals("Error: authToken cannot be null", thrown.getMessage());
    }

    // getAuth: Successfully retrieve an existing auth token
    @Test
    @DisplayName("Successfully retrieve an existing authToken")
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
    @DisplayName("Fail to retrieve a non-existent authToken")
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
    @DisplayName("Successfully delete an authToken")
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
    @DisplayName("Fail to delete a non-existent authToken")
    void testDeleteAuthFailure() {
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> authService.deleteAuth("invalidToken"));
        assertEquals("Error: authToken not found for deletion", thrown.getMessage());
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