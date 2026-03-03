package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import javax.xml.crypto.Data;
import java.util.UUID;

public class UserService {

    private DatabaseAuthDAO authDAO = new DatabaseAuthDAO();
    private DatabaseUserDAO userDAO = new DatabaseUserDAO();

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (userDAO.userExists(request.username())) {
            throw new DataAccessException(request.username() + " already exists, cannot create new user.");
        }
        userDAO.createUser(new UserData(request.username(), request.password(), request.email()));
        AuthData auth = new AuthData(UUID.randomUUID().toString(), request.username());
        authDAO.createAuth(auth);
        return new RegisterResult(request.username(), auth.authToken());
    }

}
