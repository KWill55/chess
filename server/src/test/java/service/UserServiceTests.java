package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        userService = new UserService(userDAO, authDAO);

        // ✅ Preload a user for login testing
        try {
            userDAO.createUser(new UserData("Kenny", "password", "kenny@email.com"));
        } catch (DataAccessException e) {
            fail("Setup failed: Could not create test user.");
        }
    }

    @Test
    @DisplayName("✅ Login Success - Valid Username and Password")
    void testLoginSuccess() throws DataAccessException {
        // ✅ Create a login request with correct credentials
        LoginRequest request = new LoginRequest("Kenny", "password");

        // ✅ Call the login method
        LoginResponse response = userService.login(request);

        // ✅ Check that the response contains the correct username and a valid authToken
        assertNotNull(response, "Response should not be null");
        assertEquals("Kenny", response.username(), "Username should match");
        assertNotNull(response.authToken(), "Auth token should not be null");
    }
}

