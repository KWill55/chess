package service;

import dataaccess.InMemoryUserDAO;
import dataaccess.DataAccessException;
import model.UserData;

/**
 * Service class responsible for handling user-related operations.
 */
public class UserService {
    private final InMemoryUserDAO inMemoryUserDAO; // DAO responsible for managing user data

    /**
     * Constructor for UserService.
     * @param inMemoryUserDAO The data access object responsible for user storage and retrieval.
     */
    public UserService(InMemoryUserDAO inMemoryUserDAO) {
        this.inMemoryUserDAO = inMemoryUserDAO;
    }

    /**
     * Creates a new user in the system.
     *
     * @param user The UserData object containing user details.
     * @throws DataAccessException If there is an issue creating the user (e.g., username already exists).
     */
    public void createUser(UserData user) throws DataAccessException {
        inMemoryUserDAO.createUser(user);
    }

    /**
     * Retrieves a specific user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return The UserData object containing user details.
     * @throws DataAccessException If the user does not exist.
     */
    public UserData getUser(String username) throws DataAccessException {
        return inMemoryUserDAO.getUser(username);
    }

    /**
     * Clears all user data from the system.
     *
     * @throws DataAccessException If there is an issue clearing user data.
     */
    public void clear() throws DataAccessException {
        inMemoryUserDAO.clear();
    }
}
