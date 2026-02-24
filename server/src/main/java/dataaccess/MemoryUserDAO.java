package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {

    private static ArrayList<UserData> users = new ArrayList<UserData>();

    @Override
    public void clear() throws DataAccessException {
        try {
            this.users.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear users from the database", e);
        }
    }

}
