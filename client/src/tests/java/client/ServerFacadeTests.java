package client;

import dataaccess.DataAccessException;
import dataaccess.DatabaseAuthDAO;
import dataaccess.DatabaseGameDAO;
import dataaccess.DatabaseUserDAO;
import model.AuthData;
import model.RegisterRequest;
import org.junit.jupiter.api.*;
import server.Server;
import service.ServiceException;

import java.beans.ExceptionListener;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static DatabaseUserDAO userDAO = new DatabaseUserDAO();
    private static DatabaseAuthDAO authDAO = new DatabaseAuthDAO();
    private static DatabaseGameDAO gameDAO = new DatabaseGameDAO();

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade("localhost", port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void wipeDatabase() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @Test
    public void clientRegisterSuccess() {
        try {
            AuthData received = serverFacade.register("WyWyGuy", "MyPasswordHere", "wywyguy@byu.edu");
            Assertions.assertEquals("WyWyGuy", received.username());
            Assertions.assertNotNull(received.authToken());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void clientRegisterFail() {
        try {
            AuthData received1 = serverFacade.register("WyWyGuy", "IWasFirst", "wywyguy@byu.edu");
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertThrows(Exception.class,
                () -> {
                    AuthData received2 = serverFacade.register("WyWyGuy", "IWantYourUsername", "wywyguy@byu.edu");
                });
    }

}
