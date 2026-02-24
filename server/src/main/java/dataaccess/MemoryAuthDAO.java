package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO {

    private static ArrayList<AuthData> auths = new ArrayList<AuthData>();

    @Override
    public void clear() throws DataAccessException {
        try {
            this.auths.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear auths from the database", e);
        }
    }

}
