package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.UUID;

public class AuthService {

    private DatabaseAuthDAO authDAO = new DatabaseAuthDAO();
    private DatabaseUserDAO userDAO = new DatabaseUserDAO();

    public LoginResult login(LoginRequest request) throws DataAccessException, ServiceException {
        if (!userDAO.userExists(request.username())) {
            throw new ServiceException("unauthorized", 401);
        }
        UserData user = userDAO.getUser(request.username());
        if (!BCrypt.checkpw(request.password(), user.password())) {
            throw new ServiceException("unauthorized", 401);
        }
        AuthData auth = new AuthData(UUID.randomUUID().toString(), request.username());
        authDAO.createAuth(auth);
        return new LoginResult(request.username(), auth.authToken());
    }

    public void logout(String authToken) throws DataAccessException, ServiceException {
        if (!authDAO.authExists(authToken)) {
            throw new ServiceException("unauthorized", 401);
        }
        authDAO.deleteAuth(authToken);
    }

}
