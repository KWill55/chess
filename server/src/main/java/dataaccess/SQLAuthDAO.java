package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (auth == null || auth.username() == null) {
            throw new DataAccessException("Error: authToken cannot be null");
        }

        // If the incoming AuthData doesn't have a token, generate one
        String token = auth.authToken();
        if (token == null) {
            token = UUID.randomUUID().toString();
        }

        // Insert into database
        String insertSql = "INSERT INTO AuthTokens (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            stmt.setString(1, token);
            stmt.setString(2, auth.username());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: authToken was not inserted");
            }

            System.out.println("DEBUG: Inserted authToken: " + token + " for user: " + auth.username());
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("foreign key") ||
                    e.getMessage().toLowerCase().contains("constraint fails")) {
                throw new DataAccessException("Error inserting authToken: User does not exist");
            }
            throw new DataAccessException("Error inserting authToken: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM AuthTokens WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                System.out.println("DEBUG: Retrieved auth for " + username);
                return new AuthData(authToken, username);
            } else {
                System.out.println("DEBUG: authToken not found: " + authToken);
                throw new DataAccessException("Error: authToken not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving authToken: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM AuthTokens WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataAccessException("Error: authToken not found for deletion");
            }

            System.out.println("DEBUG: Deleted authToken: " + authToken);

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting authToken: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM AuthTokens"; // Only clears auth tokens, not users

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsDeleted = stmt.executeUpdate();
            System.out.println("DEBUG: Auth table cleared. Rows deleted: " + rowsDeleted);
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing authTokens: " + e.getMessage());
        }
    }
}
