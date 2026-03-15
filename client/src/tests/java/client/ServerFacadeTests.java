package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseAuthDAO;
import dataaccess.DatabaseGameDAO;
import dataaccess.DatabaseUserDAO;
import model.AuthData;
import model.GameData;
import model.RegisterRequest;
import org.junit.jupiter.api.*;
import server.Server;
import service.ServiceException;

import java.beans.ExceptionListener;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


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
    public void clientRegisterFailDuplicateUser() {
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

    @Test
    public void clientLoginSuccess() {
        try {
            AuthData received1 = serverFacade.register("WyWyGuy", "MyPasswordHere", "wywyguy@byu.edu");
            AuthData received2 = serverFacade.login("WyWyGuy", "MyPasswordHere");
            Assertions.assertEquals("WyWyGuy", received2.username());
            Assertions.assertNotNull(received2.authToken());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void clientLoginFailWrongPassword() {
        try {
            AuthData received1 = serverFacade.register("WyWyGuy", "MyPasswordHere", "wywyguy@byu.edu");
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertThrows(Exception.class,
                () -> {
                    AuthData received2 = serverFacade.login("WyWyGuy", "IWantYourAccount!");
                });
    }

    @Test
    public void clientLogoutSuccess() {
        try {
            AuthData received = serverFacade.register("WyWyGuy", "MyPasswordHere", "wywyguy@byu.edu");
            serverFacade.logout();
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertThrows(Exception.class,
                () -> {
                    serverFacade.logout();
                });
    }

    @Test
    public void clientLogoutFailUnauthorized() {
        Assertions.assertThrows(Exception.class,
                () -> {
                    serverFacade.logout();
                });
    }

    @Test
    public void clientCreateGameSuccess() {
        try {
            AuthData received = serverFacade.register("WyWyGuy", "MyPassword", "wywyguy@byu.edu");
            int gameID = serverFacade.createGame("WyWyGame");
            Assertions.assertEquals(1, gameID);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void clientCreateGameFailedNullName() {
        try {
            AuthData received = serverFacade.register("WyWyGuy", "MyPassword", "wywyguy@byu.edu");
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertThrows(Exception.class,
                () -> {
                    int gameID = serverFacade.createGame(null);
                });
    }

    @Test
    public void clientListGamesSuccess() {
        try {
            AuthData received = serverFacade.register("WyWyGuy", "MyPassword", "wywyguy@byu.edu");
            serverFacade.createGame("Test Game 1");
            serverFacade.createGame("Test Game 2");
            serverFacade.createGame("Test Game 3");
        } catch (Exception e) {
            Assertions.fail();
        }
        try {
            Collection<GameData> games = serverFacade.listGames();
            Set<String> expectedNames = Set.of("Test Game 1", "Test Game 2", "Test Game 3");
            Set<String> actualNames = games.stream()
                    .map(GameData::gameName)
                    .collect(Collectors.toSet());
            Assertions.assertEquals(expectedNames, actualNames);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void clientListGamesFailedUnauthorized() {
        Assertions.assertThrows(Exception.class,
                () -> {
                    Collection<GameData> games = serverFacade.listGames();
                });
    }

    @Test
    public void clientJoinGameSuccess() {
        try {
            AuthData received = serverFacade.register("WyWyGuy", "MyPassword", "wywyguy@byu.edu");
            int gameID = serverFacade.createGame("WyWyGame");
            serverFacade.joinGame(ChessGame.TeamColor.WHITE, gameID);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void clientJoinGameFailedInvalidGameID() {
        try {
            AuthData received = serverFacade.register("WyWyGuy", "MyPassword", "wywyguy@byu.edu");
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertThrows(Exception.class,
                () -> {
                    serverFacade.joinGame(ChessGame.TeamColor.BLACK, 1);
                });
    }

}
