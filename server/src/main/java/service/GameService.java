package service;

import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;
import chess.*;

import java.util.List;

/**
 * Service class responsible for handling game-related operations.
 */
public class GameService {
    private final GameDAO gameDAO; // DAO responsible for game data storage and retrieval

    /**
     * Constructor for GameService.
     * @param gameDAO The data access object responsible for managing game data.
     */
    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    /**
     * Retrieves a list of all games in the system.
     *
     * @return A list of GameData objects representing all games.
     * @throws DataAccessException If there's an issue accessing the data.
     */
    public List<GameData> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }

    /**
     * Creates a new chess game and stores it in the database.
     *
     * @param gameName The name of the game to be created.
     * @return The unique game ID assigned to the new game.
     * @throws DataAccessException If there's an issue creating the game.
     */
    public int createGame(String gameName) throws DataAccessException {
        ChessGame newGame = new ChessGame(); // Create a new ChessGame instance
        GameData gameData = new GameData(0, null, null, gameName, newGame); // Default values for game
        int gameID = gameDAO.createGame(gameData); // Store in database
        System.out.println("GameService: gameName = " + gameName);
        return gameID; // Return the assigned game ID
    }

    /**
     * Retrieves a specific game by its unique game ID.
     *
     * @param gameID The ID of the game to retrieve.
     * @return The GameData object containing game details.
     * @throws DataAccessException If the game is not found.
     */
    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    /**
     * Updates an existing game with new game data.
     *
     * @param gameID The ID of the game to be updated.
     * @param updatedGame The updated GameData object containing new game information.
     * @throws DataAccessException If the game does not exist or cannot be updated.
     */
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        gameDAO.updateGame(updatedGame);
    }

    /**
     * Clears all game data from the system.
     *
     * @throws DataAccessException If there's an issue clearing the data.
     */
    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}
