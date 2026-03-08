package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAuthDAO implements AuthDAO {

    @Override
    public void clear() throws DataAccessException {
        Connection conn = DatabaseManager.getConnection();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM auths")) {
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("ALTER TABLE auths AUTO_INCREMENT = 1")) {
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new DataAccessException("Could not rollback auths table clearing");
            }
            throw new DataAccessException("Could not clear the auths table");
        }
    }

    @Override
    public boolean authExists(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM auths WHERE authToken = ?")) {
            stmt.setString(1, authToken);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DataAccessException("Could not determine if " + authToken + " exists");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM auths WHERE authToken = ?")) {
            stmt.setString(1, authToken);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String col1 = rs.getString("authToken");
                String col2 = rs.getString("username");
                return new AuthData(col1, col2);
            }
            throw new DataAccessException("Could not find auth " + authToken);
        } catch (SQLException e) {
            throw new DataAccessException("Could not return auth " + authToken);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO auths (authToken, username) VALUES (?, ?)")) {
            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not create auth " + auth.authToken());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM auths WHERE authToken = ?")) {
            stmt.setString(1, authToken);
            int deleted = stmt.executeUpdate();
            if (deleted != 1) {
                throw new DataAccessException("Expected to delete " + authToken + " but " + deleted + " auths were deleted");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete auth " + authToken);
        }
    }

}
