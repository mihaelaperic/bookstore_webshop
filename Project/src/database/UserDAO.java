package database;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Represents a data access object (DAO) for {@link User} entities in the bookstore application.
 * <p>
 * The primary responsibilities of this DAO include:
 * <ul>
 *     <li>Retrieving a user based on their username and password for authentication purposes.</li>
 *     <li>Fetching a user by their username.</li>
 *     <li>Obtaining a user based on their unique identifier (user ID).</li>
 *     <li>Adding new users to the database.</li>
 * </ul>
 * </p>
 * This class uses JDBC for database interactions and maintains a connection instance for operations.
 * All retrieval operations provide their results wrapped in {@link Optional} for safer usage.
 *
 * @see User
 * @see DatabaseConnectionManager
 */

public class UserDAO {
    private Connection connection;

    public UserDAO()
    {
        this.connection = DatabaseConnectionManager.getInstance().getConnection();
    }

    /**
     * Retrieves a user based on the given username and password.
     *
     * @param username      The username of the desired user.
     * @param hashedPassword The hashed password of the desired user.
     * @return An Optional containing the User object if found, otherwise returns an empty Optional.
     */
    public Optional<User> getUserByUsernameAndPassword(String username, String hashedPassword) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("username"), rs.getString("email"),rs.getString("password"), rs.getString("role") );
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Fetches a user based solely on the provided username.
     *
     * @param username The username of the desired user.
     * @return An Optional containing the User object if found, otherwise returns an empty Optional.
     */
    public Optional<User> getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("username"), rs.getString("email"),rs.getString("password"), rs.getString("role") );
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId The unique identifier of the desired user.
     * @return A User object if found, otherwise returns null.
     */
    public static User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE id = ?";
        User foundUser = null;

        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                foundUser = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foundUser;
    }


    /**
     * Adds a new user record to the database.
     *
     * @param username The User object containing the details of the user to be added.
     * @return true if the user was successfully added, false otherwise.
     */
    public boolean addUser(User username) {
        String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username.getUsername());
            stmt.setString(2, username.getEmail());
            stmt.setString(3, username.getPassword());
            stmt.setString(4, username.getRole());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
