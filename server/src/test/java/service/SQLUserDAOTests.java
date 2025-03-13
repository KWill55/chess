package service;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQLUserDAOTests {

    private SQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        clearUsers();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        clearUsers();
    }

    // Helper method to clear the Users table.
    private void clearUsers() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing Users table: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Create User - Success")
    public void testCreateUserSuccess() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "test@example.com");
        userDAO.createUser(user);
        UserData retrieved = userDAO.getUser("testUser");
        assertNotNull(retrieved, "User should be stored in the database");
        assertEquals("testUser", retrieved.username(), "Usernames should match");
        assertEquals("test@example.com", retrieved.email(), "Emails should match");
        // Password is stored as a bcrypt hash; verify using BCrypt
        assertTrue(org.mindrot.jbcrypt.BCrypt.checkpw("password123", retrieved.password()),
                "Passwords should match");
    }

    @Test
    @DisplayName("Create User - Duplicate Username")
    public void testCreateUserDuplicate() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "test@example.com");
        userDAO.createUser(user);
        UserData duplicate = new UserData("testUser", "differentPass", "diff@example.com");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> userDAO.createUser(duplicate));
        assertEquals("Error: Username already taken", ex.getMessage());
    }

    @Test
    @DisplayName("Get User - Non-existent User")
    public void testGetUserNonExistent() throws DataAccessException {
        // In our SQL DAO we return null if a user is not found.
        UserData retrieved = userDAO.getUser("nonExistent");
        assertNull(retrieved, "Expected null for a non-existent user");
    }

    @Test
    @DisplayName("Clear Users - Success")
    public void testClearUsers() throws DataAccessException {
        userDAO.createUser(new UserData("user1", "pass1", "u1@example.com"));
        userDAO.createUser(new UserData("user2", "pass2", "u2@example.com"));
        userDAO.clear();
        assertNull(userDAO.getUser("user1"), "User1 should be removed");
        assertNull(userDAO.getUser("user2"), "User2 should be removed");
    }
}
