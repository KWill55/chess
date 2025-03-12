package service;

import dataaccess.SQLUserDAO;
import dataaccess.DatabaseManager;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SQLUserServiceTests {
    private static SQLUserDAO userDAO;

    @BeforeAll
    static void setup() throws DataAccessException {
        DatabaseManager.createDatabase();
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    @DisplayName("Create User - Positive Case")
    void testCreateUser() {
        UserData user = new UserData("testUser", "password123", "email@example.com");
        assertDoesNotThrow(() -> userDAO.createUser(user));
    }

    @Test
    @DisplayName("Retrieve User - Positive Case")
    void testGetUser() throws DataAccessException {
        UserData user = new UserData("anotherUser", "pass", "user@example.com");
        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser("anotherUser");
        assertNotNull(retrievedUser);
        assertEquals("anotherUser", retrievedUser.username());
    }

    @Test
    @DisplayName("Retrieve User - Negative Case (Nonexistent User)")
    void testGetNonexistentUser() throws DataAccessException {
        assertNull(userDAO.getUser("noUser"));
    }


    @Test
    @DisplayName("Duplicate User - Negative Case")
    void testCreateDuplicateUser() throws DataAccessException {
        UserData user = new UserData("dupUser", "pass", "dup@example.com");
        userDAO.createUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }
}
