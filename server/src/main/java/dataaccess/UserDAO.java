package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {

    // Temporary database structure (until SQL is implemented)
    private final Map<String, UserData> users = new HashMap<>();

    /**
     * Creates a new user in the database.
     * @param user The UserData object containing user details.
     * @throws DataAccessException If the username already exists or input is invalid.
     */
    public void createUser(UserData user) throws DataAccessException {
        try {
            if (user == null || user.username() == null) {
                throw new DataAccessException("Error: Invalid user data");
            }
            if (users.containsKey(user.username())) {
                throw new DataAccessException("Error: Username already taken");
            }
            users.put(user.username(), user);
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to create user");
        }
    }

    /**
     * Retrieves a user from the database.
     * @param username The username of the user.
     * @return The UserData object, or null if the user is not found.
     * @throws DataAccessException If the username is null or an unexpected error occurs.
     */
    public UserData getUser(String username) throws DataAccessException {
        System.out.println("DEBUG: calling getUser for: " + username); // Debugging
        try {
            if (username == null) {
                System.out.println("DEBUG: User not found: " + username);
                throw new DataAccessException("Error: Username cannot be null");
            }
            return users.get(username);
        } catch (Exception e) {
            System.out.println("DEBUG: Unable to retrieve user: " + username);
            throw new DataAccessException("Error: Unable to retrieve user");
        }
    }

    /**
     * Clears all users from the database (for testing).
     */
    public void clear() {
        users.clear();
    }
}
