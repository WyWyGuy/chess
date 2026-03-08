package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAuthDAO implements AuthDAO {

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM auths")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not clear the auth table");
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

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

}
