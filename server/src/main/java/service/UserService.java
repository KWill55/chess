package service;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;

/**
 * Service class responsible for handling user-related operations.
 */
public class UserService {
    private final UserDAO userDAO; // DAO responsible for managing user data

    /**
     * Constructor for UserService.
     * @param userDAO The data access object responsible for user storage and retrieval.
     */
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Creates a new user in the system.
     *
     * @param user The UserData object containing user details.
     * @throws DataAccessException If there is an issue creating the user (e.g., username already exists).
     */
    public void createUser(UserData user) throws DataAccessException {
        userDAO.createUser(user);
    }

    /**
     * Retrieves a specific user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return The UserData object containing user details.
     * @throws DataAccessException If the user does not exist.
     */
    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    /**
     * Clears all user data from the system.
     *
     * @throws DataAccessException If there is an issue clearing user data.
     */
    public void clear() throws DataAccessException {
        userDAO.clear();
    }
}
