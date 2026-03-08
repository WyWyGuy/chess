package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.ClearService;
import service.RegisterRequest;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataAccessTests {

    DatabaseUserDAO userDAO = new DatabaseUserDAO();
    DatabaseAuthDAO authDAO = new DatabaseAuthDAO();
    DatabaseGameDAO gameDAO = new DatabaseGameDAO();

    @BeforeEach
    void wipeDatbase() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    void successfulUserClear() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    void successfulUserExists() throws DataAccessException {
        userDAO.createUser(new UserData("test", "pass", "a@b.com"));
        Assertions.assertTrue(userDAO.userExists("test"));
    }

    @Test
    void successfulUserDoesntExist() throws DataAccessException {
        Assertions.assertFalse(userDAO.userExists("IwasNeverBorn"));
    }

    @Test
    void successfulGetUser() throws DataAccessException {
        userDAO.createUser(new UserData("hello", "world", "hw@helloworld.com"));
        UserData returned = userDAO.getUser("hello");
        Assertions.assertEquals("hello", returned.username());
        Assertions.assertNotNull(returned.password());
        Assertions.assertEquals("hw@helloworld.com", returned.email());
    }

    @Test
    void failedGetUserDoesNotExist() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    userDAO.getUser("IalsoWasNeverBorn");
                });
    }

    @Test
    void successfulCreateUser() throws DataAccessException {
        userDAO.createUser(new UserData("new guy", "ILOVECHESS", "noob@chess.com"));
    }

    @Test
    void failedCreateUserNullParameters() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    userDAO.createUser(new UserData(null, null, null));
                });
    }

    @Test
    void failedCreateUserDuplicateUsername() throws DataAccessException {
        userDAO.createUser(new UserData("twin", "I-am-better-twin", "twin1@gmail.com"));
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    userDAO.createUser(new UserData("twin", "no-I-am", "twin2@gmail.com"));
                });
    }

    @Test
    void successfulAuthClear() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    void successfulAuthExists() throws DataAccessException {
        authDAO.createAuth(new AuthData("mySpecialAuthToken", "mySpecialUser"));
        Assertions.assertTrue(authDAO.authExists("mySpecialAuthToken"));
    }

    @Test
    void successfulAuthDoesntExist() throws DataAccessException {
        Assertions.assertFalse(authDAO.authExists("whatAmIAuthenticating...?"));
    }

    @Test
    void successfulGetAuth() throws DataAccessException {
        authDAO.createAuth(new AuthData("authTokenHere", "usernameHere"));
        AuthData returned = authDAO.getAuth("authTokenHere");
        Assertions.assertEquals("authTokenHere", returned.authToken());
        Assertions.assertEquals("usernameHere", returned.username());
    }

    @Test
    void failedGetAuthDoesNotExist() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    authDAO.getAuth("niceTryHacker!");
                });
    }

}
