package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SQLGameDAOTests {
    private SQLGameDAO gameDAO;
    private static final Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        clearGames();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        clearGames();
    }

    // Helper method to clear the Games
    private void clearGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Games")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing Games table: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Create Game - Success")
    public void testCreateGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame(); // initial board state
        GameData game = new GameData(0, "whiteUser", null, "Test Game", chessGame);
        int gameID = gameDAO.createGame(game);
        assertTrue(gameID > 0, "Game ID should be positive");
    }

    @Test
    @DisplayName("Create Game - Fail (Invalid Data)")
    public void testCreateGameFailure() {
        // Failing case: gameName is null
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "whiteUser", null, null, chessGame);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameDAO.createGame(game));
        assertEquals("Error: Invalid game data", ex.getMessage());
    }

    @Test
    @DisplayName("Get Game - Success")
    public void testGetGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "whiteUser", "blackUser", "Chess Match", chessGame);
        int gameID = gameDAO.createGame(game);
        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved, "Game should be retrieved");
        assertEquals("Chess Match", retrieved.gameName());
        assertEquals("whiteUser", retrieved.whiteUsername());
        assertEquals("blackUser", retrieved.blackUsername());
        assertEquals(gson.toJson(chessGame), gson.toJson(retrieved.game()));
    }

    @Test
    @DisplayName("Get Game - Fail (Non-existent)")
    public void testGetGameFailure() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameDAO.getGame(9999));
        assertEquals("Error: Game not found.", ex.getMessage());
    }

    @Test
    @DisplayName("Update Game - Success")
    public void testUpdateGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "whiteUser", "blackUser", "Old Name", chessGame);
        int gameID = gameDAO.createGame(game);

        // Modify game data
        ChessGame updatedChessGame = new ChessGame(); // new state
        GameData updatedGame = new GameData(gameID, "whiteUser", "blackUser", "New Name", updatedChessGame);
        gameDAO.updateGame(updatedGame);

        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved);
        assertEquals("New Name", retrieved.gameName());
    }

    @Test
    @DisplayName("Update Game - Non-existent")
    public void testUpdateGameFailure() {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(9999, "whiteUser", "blackUser", "Non-existent", chessGame);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameDAO.updateGame(game));
        assertEquals("Error: No game updated, invalid game ID.", ex.getMessage());
    }

    @Test
    @DisplayName("List Games - Empty")
    public void testListGamesEmpty() throws DataAccessException {
        List<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty(), "Expected an empty game list");
    }

    @Test
    @DisplayName("List Games - Success")
    public void testListGamesSuccess() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame();
        ChessGame chessGame2 = new ChessGame();
        GameData game1 = new GameData(0, "white1", null, "Game1", chessGame1);
        GameData game2 = new GameData(0, "white2", "black2", "Game2", chessGame2);
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        List<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    @DisplayName("Clear Games - Success")
    public void testClearGames() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "whiteUser", "blackUser", "Test Game", chessGame);
        gameDAO.createGame(game);
        gameDAO.clear();
        List<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty(), "Game list should be empty after clear");
    }
}
