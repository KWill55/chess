package dataaccess;

import model.GameData;
import java.util.*;

/**
 * The GameDAO class handles all interactions with game-related data.
 * It provides methods to create, retrieve, update, and delete games.
 */
public class GameDAO {
    // In-memory storage for games, mapped by game ID
    private final Map<Integer, GameData> games = new HashMap<>();

    // Counter to assign unique game IDs
    private int nextGameID = 1; // Initial gameID

    /////////////////////////////////////////////////////////////////////////////////
    /// Game Management Methods
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new game and stores it in the database.
     * Generates a game ID for the new game.
     *
     * @param game The GameData object containing the initial game details.
     * @return The generated game ID.
     * @throws DataAccessException If the game data is invalid (e.g., null name).
     */
    public int createGame(GameData game) throws DataAccessException {
        if (game == null || game.gameName() == null) {
            throw new DataAccessException("Error: Invalid game data");
        }

        // Generate new gameID and create a new game object
        int gameID = nextGameID++;
        GameData newGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

        // Store the new game in the map
        games.put(gameID, newGame);
        return gameID;
    }

    /**
     * Retrieves a game based on its game ID.
     *
     * @param gameID The unique identifier of the game.
     * @return The GameData object representing the game.
     * @throws DataAccessException If the game is not found.
     */
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Error: Game not found");
        }
        return games.get(gameID);
    }

    /**
     * Retrieves a list of all games currently stored.
     * The games are returned in sorted order based on game ID.
     *
     * @return A List of all GameData objects.
     */
    public List<GameData> getAllGames() {
        List<GameData> gameList = new ArrayList<>(games.values());
        gameList.sort(Comparator.comparingInt(GameData::gameID)); // Ensure sorted order
        return gameList;
    }

    /**
     * Updates an existing game's data.
     *
     * @param gameID      The ID of the game to be updated.
     * @param updatedGame The updated GameData object with new values.
     * @throws DataAccessException If the game does not exist.
     */
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Error: Game not found");
        }
        games.put(gameID, updatedGame);
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// Data Clearing Method
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Clears all stored games and resets the game ID counter.
     * This is typically used for testing or resetting the application state.
     */
    public void clear() {
        games.clear();
        nextGameID = 1; // Reset game ID counter
    }
}
