package dataaccess;

import model.GameData;

public interface GameDAO {

    void clear() throws DataAccessException;

    boolean gameExists(int ID) throws DataAccessException;

    int createGame(GameData game) throws DataAccessException;

}
