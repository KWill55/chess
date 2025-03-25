package service;

import dataaccess.SQLUserDAO;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;


class UserServiceTests {
    private UserService userService;
    private SQLUserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new SQLUserDAO();  // Creates a fresh UserDAO for each test

        try {
            userDAO.clear();
        } catch (DataAccessException e) {
            fail("Failed to clear users table in setup: " + e.getMessage());
        }

        userService = new UserService(userDAO);
    }

    // createUser: Successfully create a user
    @Test
    @DisplayName("Successfully create a user")
    void testCreateUserSuccess() throws DataAccessException {
        UserData user = new UserData("kenny", "1234", "kenny@gmail.com");
        assertDoesNotThrow(() -> userService.createUser(user)); //make sure no exceptions are thrown

        // Verify the user is stored
        UserData storedUser = userService.getUser("kenny");
        assertNotNull(storedUser);
        assertEquals("kenny", storedUser.username());
        assertTrue(BCrypt.checkpw("1234", storedUser.password()));
//        assertEquals("1234", storedUser.password());
        assertEquals("kenny@gmail.com", storedUser.email());
    }

    // createUser: Fail to create duplicate user
    @Test
    @DisplayName("Fail to create duplicate user")
    void testCreateUserFailure() throws DataAccessException {
        UserData user = new UserData("kenny", "password123", "kenny@email.com");
        userService.createUser(user);

        // Attempt to create the same user again
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> userService.createUser(user));
        assertEquals("Error: Username already taken", thrown.getMessage());
    }

    // getUser: Successfully get a user
    @Test
    @DisplayName("Successfully get a user")
    void testGetUserSuccess() throws DataAccessException {
        UserData user = new UserData("kenny", "1234", "kenny@gmail.com");
        userService.createUser(user);

        // Retrieve user
        UserData retrievedUser = userService.getUser("kenny");
        assertNotNull(retrievedUser);
        assertEquals("kenny", retrievedUser.username());
    }

    // getUser: Fail to get non-existent user
    @Test
    @DisplayName("Fail to get non-existent user")
    void testGetUserFailure() throws DataAccessException {
        UserData retrieved = userService.getUser("nonexistent");
        assertNull(retrieved, "Expected null for a non-existent user");

    }

    // clear: Successfully clear userData database
    @Test
    @DisplayName("Successfully clear userData database")
    void testClearUsers() throws DataAccessException {
        UserData user1 = new UserData("jack", "magicBeans", "jack@gmail.com");
        UserData user2 = new UserData("stitch", "family", "stitch@gmail.com");

        userService.createUser(user1);
        userService.createUser(user2);

        userService.clear(); // Clear all users

        // Verify that after clearing, getUser returns null
        UserData retrieved1 = userService.getUser("jack");
        UserData retrieved2 = userService.getUser("stitch");

        assertNull(retrieved1, "User 'jack' should be removed after clear");
        assertNull(retrieved2, "User 'stitch' should be removed after clear");
    }
}
