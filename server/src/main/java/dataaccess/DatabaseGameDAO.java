package dataaccess;

import model.GameData;

import java.util.Collection;

public class DatabaseGameDAO implements GameDAO {

    @Override
    public void clear() throws DataAccessException {

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
