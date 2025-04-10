package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    /**
     * Creates a new user and stores their data in the database.
     *
     * @param user The UserData object containing username, password, and email.
     */
    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (user == null || user.username() == null) {
            throw new DataAccessException("Error: Invalid user data");
        }

        String checkSql = "SELECT username FROM Users WHERE username = ?";
        String insertSql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, user.username());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                throw new DataAccessException("Error: Username already taken");
            }

            // Hash the password using bcrypt before storing it
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());


            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, user.username());
                insertStmt.setString(2, hashedPassword);
                insertStmt.setString(3, user.email());
                int rowsInserted = insertStmt.executeUpdate();

                if (rowsInserted == 0) {
                    throw new DataAccessException("Error: User was not inserted.");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user: " + e.getMessage());
        }
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return The UserData object containing the user's details.
     */
    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                } else {
                    // Return null if user not found, as expected by the test.
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching user: " + e.getMessage());
        }
    }



    /**
     * Clears all users from the database.
     */
    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Users"; // This will also delete related AuthTokens due to foreign key constraints

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsDeleted = stmt.executeUpdate();
//            System.out.println("DEBUG: Users table cleared. Rows deleted: " + rowsDeleted);
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users: " + e.getMessage());
        }
    }
}
