package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

/**
 * The AuthDAO is responsible for managing authTokens.
 * It has methods for creating, retrieving, deleting, and clearing authentication data.
 */
public class AuthDAO {
    private final Map<String, AuthData> authTokenMap; // Stores auth tokens mapped to AuthData objects

    /**
     * Constructor for AuthDAO.
     * Initializes an empty HashMap to store authTokens.
     */
    public AuthDAO() {
        this.authTokenMap = new HashMap<>();
    }

    /**
     * Creates and stores a new authToken.
     *
     * @param authData The authentication data containing the token and associated username.
     * @throws DataAccessException if the authData is null, the token is null, or the token already exists.
     */
    public void createAuth(AuthData authData) throws DataAccessException {
        if (authData == null) {
            throw new DataAccessException("Error: Auth data cannot be null");
        }
        if (authData.authToken() == null) {
            throw new DataAccessException("Error: Auth token cannot be null");
        }
        if (authTokenMap.containsKey(authData.authToken())) {
            throw new DataAccessException("Error: Auth token already exists");
        }

        // Store the auth token
        authTokenMap.put(authData.authToken(), authData);
    }

    /**
     * Retrieves the auth data
     *
     * @param authToken The authentication token to look up.
     * @return The AuthData associated with the token.
     * @throws DataAccessException if the token is null or not found.
     */
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Error: Auth token cannot be null");
        }
        if (!authTokenMap.containsKey(authToken)) {
            throw new DataAccessException("Error: authToken not found");
        }

        // Return the AuthData object associated with the token
        return authTokenMap.get(authToken);
    }

    /**
     * Deletes an authentication token (log out user)
     *
     * @param authToken The authentication token to be deleted.
     * @throws DataAccessException if the token is null or not found.
     */
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authToken == null || !authTokenMap.containsKey(authToken)) {
            throw new DataAccessException("Error: authToken not found");
        }

        // Remove the token from the map
        authTokenMap.remove(authToken);
    }

    /**
     * Clears all authentication tokens from the system.
     */
    public void clear() {
        authTokenMap.clear();
    }
}
