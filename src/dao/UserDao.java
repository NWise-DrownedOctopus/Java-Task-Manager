package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

import model.User;
import util.DbConnection;

/**
 * Data access operations for {@link User}.
 */
public class UserDao {

    private static final String USER_COLUMNS =
            "id, username, fullname, email, password_hash, admin, created_at";

    private final DbConnection connection;

    public UserDao(DbConnection connection) {
        this.connection = connection;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        Timestamp createdAt = rs.getTimestamp("created_at");
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("fullname"),
                rs.getString("email"),
                rs.getString("password_hash"),
                createdAt == null ? null : createdAt.toLocalDateTime(),
                rs.getBoolean("admin"));
    }

    public int create(User user) {
        String query = "INSERT INTO tb_user (username, fullname, email, password_hash, admin) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setBoolean(5, user.getIsAdmin());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalStateException("User insert failed: no rows were affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new IllegalStateException("User insert failed: generated ID was not returned.");
        } catch (SQLException ex) {
            throw new IllegalStateException("Unable to save user details.", ex);
        }
    }

    public Optional<User> readOneUser(int id) {
        String query = "SELECT " + USER_COLUMNS + " FROM tb_user WHERE id = ?";

        try (Connection conn = connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }

            return Optional.empty();
        } catch (SQLException ex) {
            throw new IllegalStateException("Unable to read user by ID.", ex);
        }
    }

    public Optional<User> readByUsername(String username) {
        String query = "SELECT " + USER_COLUMNS + " FROM tb_user WHERE LOWER(username) = LOWER(?)";

        try (Connection conn = connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }

            return Optional.empty();
        } catch (SQLException ex) {
            throw new IllegalStateException("Unable to read user by username.", ex);
        }
    }
}
