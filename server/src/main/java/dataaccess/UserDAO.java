package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

/**
 * The UserDAO class is responsible for handling user-related data storage and retrieval.
 * It provides methods to create, retrieve, and clear users from an in-memory database.
 */
public class UserDAO {

    // In-memory storage for user accounts (simulating a database)
    private final Map<String, UserData> users = new HashMap<>();

    /**
     * Default constructor for UserDAO.
     */
    public UserDAO() {
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// User Methods
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new user and stores it in the database.
     * Ensures that usernames are unique.
     *
     * @param user The UserData object containing user details.
     * @throws DataAccessException If the username is already taken or the input is invalid.
     */
    public void createUser(UserData user) throws DataAccessException {
        if (user == null || user.username() == null) {
            throw new DataAccessException("Error: Invalid user data");
        }
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Error: Username already taken");
        }
        users.put(user.username(), user);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user.
     * @return The UserData object representing the user.
     * @throws DataAccessException If the username is null or does not exist.
     */
    public UserData getUser(String username) throws DataAccessException {
        System.out.println("DEBUG: calling getUser for: " + username); // Debugging

        if (username == null) {
            System.out.println("DEBUG: Username cannot be null");
            throw new DataAccessException("Error: Username cannot be null");
        }

        UserData user = users.get(username);

        if (user == null) {
            System.out.println("DEBUG: User not found: " + username);
            throw new DataAccessException("Error: User not found");
        }

        System.out.println("DEBUG: Returning userData for " + username + " to AuthService");
        return user;
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// Data Clearing Method
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Clears all users from the database.
     */
    public void clear() {
        users.clear();
    }
}
