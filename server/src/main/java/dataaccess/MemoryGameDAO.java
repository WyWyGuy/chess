package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {

    private static ArrayList<GameData> games = new ArrayList<GameData>();

    @Override
    public void clear() throws DataAccessException {
        try {
            this.games.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear games from the database", e);
        }
    }

}
