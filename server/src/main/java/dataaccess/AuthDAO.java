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
        if (authData == null) {
            throw new DataAccessException("Error: Auth data cannot be null");
        }
        if (authData.authToken() == null) {
            throw new DataAccessException("Error: Auth token cannot be null");
        }
        if (authTokenMap.containsKey(authData.authToken())) {
            throw new DataAccessException("Error: Auth token already exists");
        }
        authTokenMap.put(authData.authToken(), authData);
    }



    /**
     * Retrieves the AuthData for a given authToken.
     */
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Error: Auth token cannot be null");
        }
        if (!authTokenMap.containsKey(authToken)) {
            throw new DataAccessException("Error: authToken not found");
        }
        return authTokenMap.get(authToken);
    }


    /**
     * Deletes an auth token, logging out the user.
     */
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authToken == null || !authTokenMap.containsKey(authToken)) {
            throw new DataAccessException("Error: authToken not found");
        }
        authTokenMap.remove(authToken);
    }

    public void clear() {
        authTokenMap.clear();
    }
}
