package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {

    void clear() throws DataAccessException;

    boolean gameExists(int id) throws DataAccessException;

    GameData getGame(int id) throws DataAccessException;

    int createGame(GameData game) throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;

    void updateWhitePlayer(int id, String player) throws DataAccessException;

    void updateBlackPlayer(int id, String player) throws DataAccessException;

    void markGameOver(int id) throws DataAccessException;

    boolean gameIsOver(int id) throws DataAccessException;

}
