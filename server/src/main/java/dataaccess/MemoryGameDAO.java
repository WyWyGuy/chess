package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private static HashMap<Integer, GameData> games = new HashMap<>();
    private static int gameCounter = 1;

    @Override
    public void clear() throws DataAccessException {
        try {
            this.games.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear games from the database", e);
        }
    }

    @Override
    public boolean gameExists(int ID) throws DataAccessException {
        return this.games.containsKey(ID);
    }

    public int createGame(GameData gameInfo) throws DataAccessException {
        if (gameExists(gameInfo.gameID())) {
            throw new DataAccessException("Game with ID " + gameInfo.gameID() + " already exists, cannot create new game.");
        }
        int id = gameCounter;
        GameData game = new GameData(id, gameInfo.whiteUsername(), gameInfo.blackUsername(), gameInfo.gameName(), gameInfo.game());
        this.gameCounter += 1;
        this.games.put(game.gameID(), game);
        return id;
    }

}
