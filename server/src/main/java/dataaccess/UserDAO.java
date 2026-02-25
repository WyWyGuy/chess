package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;

public interface UserDAO {

    void clear() throws DataAccessException;

    boolean userExists(String username) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;
    
}
