package service;

import dataaccess.AuthDAO;
import model.AuthData;
import dataaccess.DataAccessException;
import java.util.UUID;

/**
 * Service class for handling authentication-related operations.
 * This class acts as an intermediary between the API handlers and the AuthDAO.
 */
public class AuthService {
    private final AuthDAO authDAO; // DAO responsible for authentication data storage and retrieval

    /**
     * Constructor for AuthService.
     * @param authDAO The data access object responsible for authentication management.
     */
    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    /**
     * Generates a new authentication token for a user and stores it.
     *
     * @param username The username associated with the authentication token.
     * @return The generated authentication token.
     * @throws DataAccessException If there's an issue with the data access layer.
     */
    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString(); // Generate a unique token
        authDAO.createAuth(new AuthData(authToken, username)); // Store the token in the DAO
        return authToken; // Return the generated token
    }

    /**
     * Retrieves authentication details using an auth token.
     *
     * @param authToken The authentication token.
     * @return AuthData object containing the associated username.
     * @throws DataAccessException If the authentication token is not found.
     */
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    /**
     * Deletes an authentication token, effectively logging out the user.
     *
     * @param authToken The authentication token to be deleted.
     * @throws DataAccessException If the token does not exist.
     */
    public void deleteAuth(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }

    /**
     * Retrieves the username associated with an authentication token.
     *
     * @param authToken The authentication token.
     * @return The username associated with the token.
     * @throws DataAccessException If the auth token is invalid or not found.
     */
    public String getUserFromAuth(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) { // Double-check that authData is valid
            throw new DataAccessException("Error: Invalid authToken");
        }

        return authData.username(); // Return the username tied to the token
    }

    /**
     * Clears all authentication data from the database.
     *
     * @throws DataAccessException If there's an issue clearing the data.
     */
    public void clear() throws DataAccessException {
        authDAO.clear(); // Clears all authentication records
    }
}
