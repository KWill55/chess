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
    @DisplayName("Get Auth Token - Fail (Non-existent)")
    public void testGetAuthFailure() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.getAuth("non-existent-token"));
        assertEquals("Error: Auth token not found", ex.getMessage());
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
