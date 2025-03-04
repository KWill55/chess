package dataaccess;

import model.UserData;
import model.GameData;
import model.AuthData;
import java.util.List;

/**
 * The DataAccess interface defines the contract for interacting with
 * user, game, and auth storage
 */
public interface DataAccess {

    /////////////////////////////////////////////////////////////////////////////////
    /// User Methods
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new user and stores their data.
     *
     * @param user The UserData object containing username, password, and email.
     * @throws DataAccessException if a user with the same username already exists or if the data is invalid.
     */
    void createUser(UserData user) throws DataAccessException;

    /**
     * Retrieves a user's information based on their username.
     *
     * @param username The username of the user to retrieve.
     * @return The UserData object containing the user's details, or null if the user is not found.
     */
    UserData getUser(String username);

    /////////////////////////////////////////////////////////////////////////////////
    /// Authentication Methods
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates and stores a new authentication token.
     *
     * @param auth The AuthData object containing the token and associated username.
     */
    void createAuth(AuthData auth);

    /**
     * Retrieves authentication data based on the provided auth token.
     *
     * @param authToken The authentication token.
     * @return The AuthData object containing the username associated with the token.
     */
    AuthData getAuth(String authToken);

    /**
     * Deletes an authentication token, effectively logging the user out.
     *
     * @param authToken The authentication token to be removed.
     */
    void deleteAuth(String authToken);

    /////////////////////////////////////////////////////////////////////////////////
    /// Game Methods
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new game and stores it.
     *
     * @param game The GameData object containing details about the game.
     */
    void createGame(GameData game);

    /**
     * Retrieves game information based on its game ID.
     *
     * @param gameID The unique identifier for the game.
     * @return The GameData object containing the game's details, or null if not found.
     */
    GameData getGame(int gameID);

    /**
     * Retrieves a list of all stored games.
     *
     * @return A List of GameData objects representing all available games.
     */
    List<GameData> listGames();

    /**
     * Updates an existing game's data.
     *
     * @param game The updated GameData object containing the new game details.
     */
    void updateGame(GameData game);

    /////////////////////////////////////////////////////////////////////////////////
    /// Clear Data Method
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Clears all stored data (user, auth, and game)
     * This is typically used for system resets or testing purposes.
     */
    void clear();
}
