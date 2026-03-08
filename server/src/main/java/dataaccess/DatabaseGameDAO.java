package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class DatabaseGameDAO implements GameDAO {

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM games")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not clear the games table");
        }
    }

    @Override
    public boolean gameExists(int id) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public void updateWhitePlayer(int id, String player) throws DataAccessException {

    }

    @Override
    public void updateBlackPlayer(int id, String player) throws DataAccessException {

    }

}
