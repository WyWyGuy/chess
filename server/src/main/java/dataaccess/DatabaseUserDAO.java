package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not clear the database");
        }
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

}
