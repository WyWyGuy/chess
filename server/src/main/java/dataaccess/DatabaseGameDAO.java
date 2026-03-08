package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class DatabaseGameDAO implements GameDAO {

    private Gson gson = new Gson();

    @Override
    public void clear() throws DataAccessException {
        Connection conn = DatabaseManager.getConnection();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM games")) {
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("ALTER TABLE games AUTO_INCREMENT = 1")) {
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new DataAccessException("Could not rollback games table clearing", ex);
            }
            throw new DataAccessException("Could not clear the games table", e);
        }
    }

    @Override
    public boolean gameExists(int id) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM games WHERE gameID = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DataAccessException("Could not determine if game " + id + " exists", e);
        }
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM games WHERE gameID = ?")) {
            stmt.setInt(1, id);
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
            throw new DataAccessException("Could not return game " + id, e);
        }
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO games " +
                             "(whiteUsername, blackUsername, gameName, game) " +
                             "VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gson.toJson(game.game()));
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new DataAccessException("Creating game " + game.game() + " did not return a game ID.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not create game " + game.gameName(), e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM games")) {
            ResultSet rs = stmt.executeQuery();
            Collection<GameData> games = new ArrayList<GameData>();
            while (rs.next()) {
                int col1 = rs.getInt("gameID");
                String col2 = rs.getString("whiteUsername");
                String col3 = rs.getString("blackUsername");
                String col4 = rs.getString("gameName");
                String col5 = rs.getString("game");
                ChessGame col5game = gson.fromJson(col5, ChessGame.class);
                games.add(new GameData(col1, col2, col3, col4, col5game));
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Could not return games list", e);
        }
    }

    @Override
    public void updateWhitePlayer(int id, String player) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE games SET whiteUsername = ? WHERE gameID = ?")) {
            stmt.setString(1, player);
            stmt.setInt(2, id);
            int changed = stmt.executeUpdate();
            if (changed != 1) {
                throw new DataAccessException("Updating (white) game " + id + " should have modified one row but " + changed + " were changed");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not update white player in game " + id, e);
        }
    }

    @Override
    public void updateBlackPlayer(int id, String player) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE games SET blackUsername = ? WHERE gameID = ?")) {
            stmt.setString(1, player);
            stmt.setInt(2, id);
            int changed = stmt.executeUpdate();
            if (changed != 1) {
                throw new DataAccessException("Updating (black) game " + id + " should have modified one row but " + changed + " were changed");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not update black player in game " + id, e);
        }
    }

}
