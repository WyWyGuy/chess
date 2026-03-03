package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import service.ClearService;
import service.RegisterRequest;

import javax.xml.crypto.Data;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataAccessTests {

    DatabaseUserDAO userDAO = new DatabaseUserDAO();
    DatabaseAuthDAO authDAO = new DatabaseAuthDAO();
    DatabaseGameDAO gameDAO = new DatabaseGameDAO();

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

}
