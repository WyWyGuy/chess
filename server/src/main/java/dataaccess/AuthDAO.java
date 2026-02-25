package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;

public interface AuthDAO {

    void clear() throws DataAccessException;

    boolean authExists(String authToken) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

}
