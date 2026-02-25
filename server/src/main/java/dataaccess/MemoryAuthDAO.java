package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    private static HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        try {
            this.auths.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear auths from the database", e);
        }
    }

    @Override
    public boolean authExists(String authToken) throws DataAccessException {
        return this.auths.containsKey(authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authExists(authToken)) {
            return this.auths.get(authToken);
        }
        throw new DataAccessException("Auth token does not exist: " + authToken);
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (authExists(auth.authToken())) {
            throw new DataAccessException("Auth token already exists: " + auth.authToken());
        }
        this.auths.put(auth.authToken(), auth);
    }


}
