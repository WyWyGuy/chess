package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {

    @Override
    public void clear() throws DataAccessException {
        Connection conn = DatabaseManager.getConnection();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users")) {
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("ALTER TABLE users AUTO_INCREMENT = 1")) {
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new DataAccessException("Could not rollback users table clearing", e);
            }
            throw new DataAccessException("Could not clear the users table", e);
        }
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DataAccessException("Could not determine if " + username + " exists", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String col1 = rs.getString("username");
                String col2 = rs.getString("password");
                String col3 = rs.getString("email");
                return new UserData(col1, col2, col3);
            }
            throw new DataAccessException("Could not find user " + username);
        } catch (SQLException e) {
            throw new DataAccessException("Could not return user " + username, e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
            stmt.setString(1, user.username());
            stmt.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not create user " + user.username(), e);
        }
    }

}
