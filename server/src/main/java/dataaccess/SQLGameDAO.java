package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private static final Gson GSON = new Gson();

    @Override
    public int createGame(GameData game) throws DataAccessException {
        if (game == null || game.gameName() == null) {
            throw new DataAccessException("Error: Invalid game data");
        }

        String sql = "INSERT INTO Games (gameName, whiteUsername, blackUsername, gameState, gameOver) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, game.gameName());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());
            stmt.setString(4, GSON.toJson(game.game()));
            stmt.setBoolean(5, game.gameOver());

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

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM Games WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ChessGame chessGame = GSON.fromJson(rs.getString("gameState"), ChessGame.class);
                return new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        chessGame,
                        rs.getBoolean("gameOver")
                );
            } else {
                throw new DataAccessException("Error: Game not found.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE Games SET gameName = ?, whiteUsername = ?, blackUsername = ?, gameState = ?, gameOver = ? WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.gameName());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());
            stmt.setString(4, GSON.toJson(game.game()));
            stmt.setBoolean(5, game.gameOver());
            stmt.setInt(6, game.gameID());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: No game updated, invalid game ID.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM Games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ChessGame chessGame = GSON.fromJson(rs.getString("gameState"), ChessGame.class);
                games.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        chessGame,
                        rs.getBoolean("gameOver")
                ));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving games: " + e.getMessage());
        }
        return games;
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsDeleted = stmt.executeUpdate();
//            System.out.println("DEBUG: Games table cleared. Rows deleted: " + rowsDeleted);
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games: " + e.getMessage());
        }
    }
}
