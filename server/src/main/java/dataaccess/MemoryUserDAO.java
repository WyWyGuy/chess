package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private static HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        try {
            this.users.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear users from the database", e);
        }
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        return this.users.containsKey(username);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (userExists(username)) {
            return this.users.get(username);
        }
        throw new DataAccessException("No user exists with name " + username);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (userExists(user.username())) {
            throw new DataAccessException(user.username() + " already exists, cannot create new user.");
        }
        this.users.put(user.username(), user);
    }

}
