package dataaccess;

import model.AuthData;

public class DatabaseAuthDAO implements AuthDAO {

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public boolean authExists(String authToken) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

}
