package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
    private final Map<String, AuthData> authTokenMap; // Stores auth tokens

    public AuthDAO() {
        this.authTokenMap = new HashMap<>();
    }

    /**
     *  new auth token.
     */
    public void createAuth(AuthData authData) throws DataAccessException {
        if (authData == null || authData.authToken() == null) {
            throw new DataAccessException("Error: Invalid auth data");
        }
        authTokenMap.put(authData.authToken(), authData);
    }

    /**
     * Retrieves the AuthData for a given authToken.
     */
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null || !authTokenMap.containsKey(authToken)) {
            throw new DataAccessException("Error: Invalid or expired auth token");
        }
        return authTokenMap.get(authToken);
    }

    /**
     * Deletes an auth token, logging out the user.
     */
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authToken == null || !authTokenMap.containsKey(authToken)) {
            throw new DataAccessException("Error: Auth token not found");
        }
        authTokenMap.remove(authToken);
    }
}
