package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private static final Gson GSON = new Gson();


    /**
     * Creates a new game in the database and returns its game ID.
     *
     * @param game The GameData object containing game details.
     * @return The generated game ID.
     * @throws DataAccessException If the database operation fails.
     */
    @Override
    public int createGame(GameData game) throws DataAccessException {
        if (game == null || game.gameName() == null) {
            throw new DataAccessException("Error: Invalid game data");
        }

        String sql = "INSERT INTO Games (gameName, whiteUsername, blackUsername, gameState) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, game.gameName());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());

            // Convert ChessGame object to JSON string for storage
            String gameJson = GSON.toJson(game.game());
            stmt.setString(4, gameJson);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Error: Creating game failed, no rows affected.");
            }

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
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM Games WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Convert JSON string back to ChessGame object
                ChessGame chessGame = GSON.fromJson(rs.getString("gameState"), ChessGame.class);

                return new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        chessGame
                );
            } else {
                throw new DataAccessException("Error: Game not found.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
    }

    /**
     * Updates an existing game's data.
     *
     * @param game The updated GameData object containing the new game details.
     * @throws DataAccessException If the update fails.
     */
    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE Games SET gameName = ?, whiteUsername = ?, blackUsername = ?, gameState = ? WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.gameName());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());

            // Convert ChessGame object to JSON for storage
            String gameJson = GSON.toJson(game.game());
            stmt.setString(4, gameJson);
            stmt.setInt(5, game.gameID());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: No game updated, invalid game ID.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    /**
     * Retrieves all games from the database.
     *
     * @return A list of all games.
     * @throws DataAccessException If the database operation fails.
     */
    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM Games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Convert JSON to ChessGame
                ChessGame chessGame = GSON.fromJson(rs.getString("gameState"), ChessGame.class);

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
     * Clears all games from the database.
     */
    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsDeleted = stmt.executeUpdate();
            System.out.println("DEBUG: Games table cleared. Rows deleted: " + rowsDeleted);
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games: " + e.getMessage());
        }
    }
}
