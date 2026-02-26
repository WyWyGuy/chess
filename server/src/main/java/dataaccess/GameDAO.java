package dataaccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDAO {

    void clear() throws DataAccessException;

    boolean gameExists(int id) throws DataAccessException;

    GameData getGame(int id) throws DataAccessException;

    int createGame(GameData game) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateWhitePlayer(int id, String player) throws DataAccessException;

    void updateBlackPlayer(int id, String player) throws DataAccessException;

}
