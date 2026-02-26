package dataaccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDAO {

    void clear() throws DataAccessException;

    boolean gameExists(int ID) throws DataAccessException;

    GameData getGame(int ID) throws DataAccessException;

    int createGame(GameData game) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateWhitePlayer(int ID, String player) throws DataAccessException;

    void updateBlackPlayer(int ID, String player) throws DataAccessException;

}
