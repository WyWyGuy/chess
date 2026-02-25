package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class AuthService {

    private MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private MemoryUserDAO userDAO = new MemoryUserDAO();

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (!userDAO.userExists(request.username())) {
            throw new DataAccessException("User does not exist: " + request.username());
        }
        UserData user = userDAO.getUser(request.username());
        if (!Objects.equals(request.password(), user.password())) {
            throw new DataAccessException("Incorrect password entered for user " + request.username());
        }
        AuthData auth = new AuthData(UUID.randomUUID().toString(), request.username());
        authDAO.createAuth(auth);
        return new LoginResult(request.username(), auth.authToken());
    }

    public void logout(String authToken) throws DataAccessException {
        if (!authDAO.authExists(authToken)) {
            throw new DataAccessException("Auth token does not exist");
        }
        authDAO.deleteAuth(authToken);
    }

}
