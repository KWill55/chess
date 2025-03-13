package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private static final Gson gson = new Gson();

    /**
     * Creates a new game in the database.
     *
     * @param game The GameData object containing game details.
     * @return The generated game ID.
     * @throws DataAccessException If the database operation fails.
     */
    public int createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO Games (gameName, whiteUsername, blackUsername, gameState) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, game.gameName());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());

            // Convert ChessGame object to JSON string
            String gameJson = gson.toJson(game.game());
            stmt.setString(4, gameJson);

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return generated gameID
            } else {
                throw new DataAccessException("Error: Game ID not generated.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting game: " + e.getMessage());
        }
    }

    /**
     * Retrieves a game by its game ID.
     *
     * @param gameID The unique identifier of the game.
     * @return The GameData object representing the game.
     * @throws DataAccessException If the game retrieval fails.
     */
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM Games WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Convert JSON string back to ChessGame object
                ChessGame chessGame = gson.fromJson(rs.getString("gameState"), ChessGame.class);

                return new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        chessGame // Store as object instead of raw JSON
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all games from the database.
     *
     * @return A list of all games.
     * @throws DataAccessException If the database operation fails.
     */
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM Games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Convert JSON to ChessGame
                ChessGame chessGame = gson.fromJson(rs.getString("gameState"), ChessGame.class);

                games.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        chessGame
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving games: " + e.getMessage());
        }
        return games;
    }

    /**
     * Updates an existing game in the database.
     *
     * @param game The updated GameData object containing new details.
     * @throws DataAccessException If the database update fails.
     */
    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE Games SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameState = ? WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());

            // Convert ChessGame object to JSON string
            String gameJson = gson.toJson(game.game());
            stmt.setString(4, gameJson);
            stmt.setInt(5, game.gameID());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: Game not found or not updated.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    /**
     * Clears all game records from the database.
     *
     * @throws DataAccessException If the database operation fails.
     */
    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games: " + e.getMessage());
        }
    }
}
