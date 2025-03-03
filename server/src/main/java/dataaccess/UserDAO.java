package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {

    // Temporary database structure (until SQL is implemented)
    private final Map<String, UserData> users = new HashMap<>();

    public UserDAO(){
//        users.put("Kenny", new UserData("Kenny", "password", "kenny@email.com"));
//        System.out.println("Preloaded user: Kenny (password: password)");
    }

    /**
     * Creates a new user in the database.
     * @param user The UserData object containing user details.
     * @throws DataAccessException If the username already exists or input is invalid.
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
     * Retrieves a user from the database.
     * @param username The username of the user.
     * @return The UserData object, or null if the user is not found.
     * @throws DataAccessException If the username is null or an unexpected error occurs.
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


    public void printAllUsers() {
        System.out.println("=== Stored Users ===");
        for (UserData user : users.values()) {
            System.out.println("Username: " + user.username());
            System.out.println("Password: " + user.password());
            System.out.println("Email: " + user.email());
            System.out.println("---------------------");
        }
    }

    /**
     * Clears all users from the database (for testing).
     */
    public void clear() {
        users.clear();
    }
}
