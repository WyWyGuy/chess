package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class DatabaseGameDAO implements GameDAO {

    private Gson gson = new Gson();

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
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM games WHERE gameID = ?")) {
            stmt.setString(1, String.valueOf(id));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DataAccessException("Could not determine if game " + id + " exists");
        }
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM games WHERE gameID = ?")) {
            stmt.setString(1, String.valueOf(id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int col1 = rs.getInt("gameID");
                String col2 = rs.getString("whiteUsername");
                String col3 = rs.getString("blackUsername");
                String col4 = rs.getString("gameName");
                String col5 = rs.getString("game");
                ChessGame col5game = gson.fromJson(col5, ChessGame.class);
                return new GameData(col1, col2, col3, col4, col5game);
            }
            throw new DataAccessException("Could not find game " + id);
        } catch (SQLException e) {
            throw new DataAccessException("Could not return game " + id);
        }
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
