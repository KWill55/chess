package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SQLGameDAOTests {

    private SQLGameDAO gameDAO;
    private SQLUserDAO userDAO;
    private static final Gson GSON = new Gson();

    @BeforeEach
    public void setUp() throws DataAccessException {
        clearAllTables();
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        userDAO.createUser(new UserData("whiteUser", "pass", "white@example.com"));
        userDAO.createUser(new UserData("blackUser", "pass", "black@example.com"));
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        clearAllTables();
    }

    private void clearAllTables() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0")) {
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM AuthTokens")) {
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Games")) {
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users")) {
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=1")) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing database: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("createGame - Success (positive)")
    public void testCreateGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "whiteUser", "blackUser", "Test Game", chessGame);

        int gameID = gameDAO.createGame(game);
        assertTrue(gameID > 0, "Game ID should be positive");
    }

    @Test
    @DisplayName("createGame - Fail (negative) with invalid data")
    public void testCreateGameFailure() {
        ChessGame chessGame = new ChessGame();
        GameData invalidGame = new GameData(0, "whiteUser", "blackUser", null, chessGame);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameDAO.createGame(invalidGame));
        assertEquals("Error: Invalid game data", ex.getMessage());
    }

    @Test
    @DisplayName("getGame - Success (positive)")
    public void testGetGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "whiteUser", "blackUser", "Chess Match", chessGame);
        int gameID = gameDAO.createGame(game);
        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved, "Game should be retrieved");
        assertEquals("Chess Match", retrieved.gameName());
        assertEquals("whiteUser", retrieved.whiteUsername());
        assertEquals("blackUser", retrieved.blackUsername());
        assertEquals(GSON.toJson(chessGame), GSON.toJson(retrieved.game()));
    }

    @Test
    @DisplayName("getGame - Fail (negative) with non-existent gameID")
    public void testGetGameFailure() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameDAO.getGame(9999));
        assertEquals("Error: Game not found.", ex.getMessage());
    }

    @Test
    @DisplayName("updateGame - Success (positive)")
    public void testUpdateGameSuccess() throws DataAccessException {
        ChessGame originalChess = new ChessGame();
        GameData originalGame = new GameData(0, "whiteUser", "blackUser", "Old Name", originalChess);
        int gameID = gameDAO.createGame(originalGame);
        ChessGame updatedChess = new ChessGame(); // new state
        GameData updatedGame = new GameData(gameID, "whiteUser", "blackUser", "New Name", updatedChess);
        gameDAO.updateGame(updatedGame);
        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved);
        assertEquals("New Name", retrieved.gameName());
        assertEquals(GSON.toJson(updatedChess), GSON.toJson(retrieved.game()));
    }

    @Test
    @DisplayName("updateGame - Fail (negative) for non-existent ID")
    public void testUpdateGameFailure() {
        ChessGame chessGame = new ChessGame();
        GameData nonExistentGame = new GameData(9999, "whiteUser", "blackUser", "Doesn't Exist", chessGame);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameDAO.updateGame(nonExistentGame));
        assertEquals("Error: No game updated, invalid game ID.", ex.getMessage());
    }

    @Test
    @DisplayName("listGames - Success (positive) with multiple games")
    public void testListGamesSuccess() throws DataAccessException {
        ChessGame c1 = new ChessGame();
        ChessGame c2 = new ChessGame();
        GameData g1 = new GameData(0, "whiteUser", "blackUser", "Game1", c1);
        GameData g2 = new GameData(0, "whiteUser", null, "Game2", c2);

        gameDAO.createGame(g1);
        gameDAO.createGame(g2);

        List<GameData> allGames = gameDAO.listGames();
        assertEquals(2, allGames.size(), "Should have 2 games stored");
    }

    @Test
    @DisplayName("listGames - Fail (negative) / or 'empty' scenario")
    public void testListGamesEmpty() throws DataAccessException {
        List<GameData> allGames = gameDAO.listGames();
        assertTrue(allGames.isEmpty(), "Expected no games in the database");
    }

    @Test
    @DisplayName("clear - Success (positive)")
    public void testClearGames() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "whiteUser", "blackUser", "Some Game", chessGame);
        gameDAO.createGame(game);
        gameDAO.clear();
        List<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty(), "Game list should be empty after clear");
    }
}
