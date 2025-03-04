package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GameServiceTests {
    private GameService gameService;
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() {
        gameDAO = new GameDAO();
        gameService = new GameService(gameDAO);
    }

    // createGame: Successfully create a game (positive test case)
    @Test
    @DisplayName("Successfully create a game")
    void testCreateGameSuccess() throws DataAccessException {
        int gameID = gameService.createGame("Test Game");
        assertTrue(gameID > 0, "Game ID should be a positive number");
    }

    // createGame: Fail to create a game (negative test case)
    @Test
    @DisplayName("Fail to create a game with invalid input")
    void testCreateGameFailure() {
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(null);
        });
        assertEquals("Error: Invalid game data", thrown.getMessage());
    }

    // listGames: Successfully list all games (positive test case)
    @Test
    @DisplayName("Successfully list all games")
    void testListGamesSuccess() throws DataAccessException {
        gameService.createGame("Game 1");
        gameService.createGame("Game 2");

        List<GameData> games = gameService.listGames();
        assertEquals(2, games.size(), "Should return exactly 2 games");
    }

    // listGames: Handle case when no games exist (negative test case)
    @Test
    @DisplayName("List games when no games exist")
    void testListGamesEmpty() throws DataAccessException {
        List<GameData> games = gameService.listGames();
        assertEquals(0, games.size(), "Game list should be empty");
    }

    // getGame: Successfully retrieve a game (positive test case)
    @Test
    @DisplayName("Successfully retrieve an existing game")
    void testGetGameSuccess() throws DataAccessException {
        int gameID = gameService.createGame("Chess Match");
        GameData game = gameService.getGame(gameID);

        assertNotNull(game, "Game should not be null");
        assertEquals("Chess Match", game.gameName(), "Game name should match");
    }

    // getGame: Fail to retrieve a non-existent game (negative test case)
    @Test
    @DisplayName("Fail to retrieve a non-existent game")
    void testGetGameFailure() {
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            gameService.getGame(22); // Non-existent game ID
        });
        assertEquals("Error: Game not found", thrown.getMessage());
    }

    // updateGame: Successfully update a game (positive test case)
    @Test
    @DisplayName("Successfully update a game")
    void testUpdateGameSuccess() throws DataAccessException {
        int gameID = gameService.createGame("Old Game Name");
        GameData updatedGame = new GameData(gameID, "Reese", "Twix", "New Game Name", null);

        assertDoesNotThrow(() -> gameService.updateGame(gameID, updatedGame));

        GameData retrievedGame = gameService.getGame(gameID);
        assertEquals("New Game Name", retrievedGame.gameName(), "Game name should be updated");
    }

    // updateGame: Fail to update a non-existent game (negative test case)
    @Test
    @DisplayName("Fail to update a non-existent game")
    void testUpdateGameFailure() {
        GameData updatedGame = new GameData(22, "Maddie", "Kenny", "Invalid Game", null);

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            gameService.updateGame(22, updatedGame);
        });
        assertEquals("Error: Game not found", thrown.getMessage());
    }

    // clear: Successfully clear all games (positive test case)
    @Test
    @DisplayName("Successfully clear all games")
    void testClearGames() throws DataAccessException {
        gameService.createGame("Game 1");
        gameService.createGame("Game 2");

        gameService.clear();

        List<GameData> games = gameService.listGames();
        assertEquals(0, games.size(), "Game list should be empty after clear");
    }
}
