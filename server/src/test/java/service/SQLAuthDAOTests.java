package service;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLAuthDAOTests {

    private SQLAuthDAO authDAO;
    private SQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
        clearDatabase();
        // Create a user to be used for auth tests
        userDAO.createUser(new UserData("kenny", "password", "test@gmail.com"));
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        clearDatabase();
    }

    // Helper method to clear both AuthTokens and Users tables.
    private void clearDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0")) { stmt.executeUpdate(); }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM AuthTokens")) { stmt.executeUpdate(); }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users")) { stmt.executeUpdate(); }
            try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=1")) { stmt.executeUpdate(); }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing database: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Create Auth Token - Success")
    public void testCreateAuthSuccess() throws DataAccessException {
        AuthData authData = new AuthData(null, "kenny");
        authDAO.createAuth(authData);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.getAuth("non-existent-token"));
        assertEquals("Error: Auth token not found", ex.getMessage());
    }

    @Test
    @DisplayName("Create Auth Token - Fail (Non-existent User)")
    public void testCreateAuthNonExistentUser() {
        AuthData authData = new AuthData(null, "nonExistentUser");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.createAuth(authData));
    }

    @Test
    @DisplayName("Get Auth Token - Success")
    public void testGetAuthSuccess() throws DataAccessException {
        // Define a known token and username for testing.
        String knownToken = "known-token-123";
        String username = "kenny";

        // Insert the known auth token into the AuthTokens table.
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO AuthTokens (authToken, username) VALUES (?, ?)")) {
            stmt.setString(1, knownToken);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();
            assertTrue(rowsAffected > 0, "Expected at least one row to be inserted for the auth token");
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting known auth token: " + e.getMessage());
        }

        // Use the DAO to retrieve the auth token.
        AuthData retrievedAuth = authDAO.getAuth(knownToken);

        // Assert that the retrieved auth data is correct.
        assertNotNull(retrievedAuth, "Expected auth data to be retrieved");
        assertEquals(username, retrievedAuth.username(), "Usernames should match");
    }

    @Test
    @DisplayName("Get Auth Token - Fail (Non-existent)")
    public void testGetAuthFailure() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.getAuth("non-existent-token"));
        assertEquals("Error: Auth token not found", ex.getMessage());
    }

    @Test
    @DisplayName("Delete Auth Token - Success")
    public void testDeleteAuthSuccess() throws DataAccessException {
        // Define a known token and username for testing.
        String knownToken = "delete-test-token-123";
        String username = "kenny";

        // insert the known auth token into the AuthTokens table.
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO AuthTokens (authToken, username) VALUES (?, ?)")) {
            stmt.setString(1, knownToken);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();
            assertTrue(rowsAffected > 0, "Expected the auth token to be inserted successfully");
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting known auth token: " + e.getMessage());
        }

        // Call the DAO's deleteAuth method with the known token.
        assertDoesNotThrow(() -> authDAO.deleteAuth(knownToken), "Deleting an existing auth token should not throw an exception");

        // Verify that retrieving the token throws the expected exception.
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.getAuth(knownToken));
        assertEquals("Error: Auth token not found", ex.getMessage(), "Expected auth token to be deleted");
    }


    @Test
    @DisplayName("Delete Auth Token - Fail (Non-existent)")
    public void testDeleteAuthFailure() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("non-existent-token"));
        assertEquals("Error: Auth token not found for deletion", ex.getMessage());
    }

    @Test
    @DisplayName("Clear Auth Tokens - Success")
    public void testClearAuth() throws DataAccessException {
        // Create two auth tokens for testUser
        authDAO.createAuth(new AuthData(null, "kenny"));
        authDAO.createAuth(new AuthData(null, "kenny"));
        authDAO.clear();
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.getAuth("any-token"));
        assertEquals("Error: Auth token not found", ex.getMessage());
    }
}
