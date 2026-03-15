package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataAccessTests {

    DatabaseUserDAO userDAO = new DatabaseUserDAO();
    DatabaseAuthDAO authDAO = new DatabaseAuthDAO();
    DatabaseGameDAO gameDAO = new DatabaseGameDAO();

    @BeforeEach
    void wipeDatabase() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
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
        userDAO.createUser(new UserData("mySpecialUser", "null", "null@null.null"));
        authDAO.createAuth(new AuthData("mySpecialAuthToken", "mySpecialUser"));
        Assertions.assertTrue(authDAO.authExists("mySpecialAuthToken"));
    }

    @Test
    void successfulAuthDoesntExist() throws DataAccessException {
        Assertions.assertFalse(authDAO.authExists("whatAmIAuthenticating...?"));
    }

    @Test
    void successfulGetAuth() throws DataAccessException {
        userDAO.createUser(new UserData("usernameHere", "null", "null@null.null"));
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

    @Test
    void successfulCreateAuth() throws DataAccessException {
        userDAO.createUser(new UserData("WyWyGuy", "null", "null@null.null"));
        authDAO.createAuth(new AuthData("wywyguyAuthToken", "WyWyGuy"));
    }

    @Test
    void failedCreateAuthNullParameters() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    authDAO.createAuth(new AuthData(null, null));
                });
    }

    @Test
    void successfulDeleteAuth() throws DataAccessException {
        userDAO.createUser(new UserData("abcd", "null", "null@null.null"));
        authDAO.createAuth(new AuthData("dontRevokeMyAccess!", "abcd"));
        authDAO.deleteAuth("dontRevokeMyAccess!");
    }

    @Test
    void failedDeleteAuthDoesntExist() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    authDAO.deleteAuth("whosThere???");
                });
    }

    @Test
    void successfulGameClear() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    void successfulGameExists() throws DataAccessException {
        userDAO.createUser(new UserData("WhiteWyGuy", "null", "null@null.null"));
        userDAO.createUser(new UserData("WyBlackGuy", "null", "null@null.null"));
        gameDAO.createGame(new GameData(1, "WhiteWyGuy", "WyBlackGuy", "WyGuyWy", new ChessGame()));
        Assertions.assertTrue(gameDAO.gameExists(1));
    }

    @Test
    void successfulGameDoesntExist() throws DataAccessException {
        Assertions.assertFalse(gameDAO.gameExists(982952867));
    }

    @Test
    void successfulGetGame() throws DataAccessException {
        userDAO.createUser(new UserData("white", "null", "null@null.null"));
        userDAO.createUser(new UserData("black", "null", "null@null.null"));
        int givenID = gameDAO.createGame(new GameData(6, "white", "black", "testGame", new ChessGame()));
        GameData returned = gameDAO.getGame(givenID);
        Assertions.assertEquals(1, returned.gameID());
        Assertions.assertEquals("white", returned.whiteUsername());
        Assertions.assertEquals("black", returned.blackUsername());
        Assertions.assertEquals("testGame", returned.gameName());
        Assertions.assertEquals(new ChessGame(), returned.game());
    }

    @Test
    void failedGetGameDoesNotExist() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    gameDAO.getGame(25980638);
                });
    }

    @Test
    void successfulCreateGame() throws DataAccessException {
        userDAO.createUser(new UserData("WyWyGuy1", "null", "null@null.null"));
        userDAO.createUser(new UserData("WyWyGuy2", "null", "null@null.null"));
        gameDAO.createGame(new GameData(0, "WyWyGuy1", "WyWYGuy2", "WyWyGame", new ChessGame()));
    }

    @Test
    void failedCreateGameNullParameters() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    gameDAO.createGame(new GameData(-1, null, null, null, null));
                });
    }

    @Test
    void successfulWhiteUpdate() throws DataAccessException {
        userDAO.createUser(new UserData("wtest1", "null", "null@null.null"));
        userDAO.createUser(new UserData("wtest2", "null", "null@null.null"));
        userDAO.createUser(new UserData("NEWwtest1", "null", "null@null.null"));
        int id = gameDAO.createGame(new GameData(0, "wtest1", "wtest2", "wtest", new ChessGame()));
        gameDAO.updateWhitePlayer(id, "NEWwtest1");
        GameData game = gameDAO.getGame(id);
        Assertions.assertEquals("NEWwtest1", game.whiteUsername());
    }

    @Test
    void successfulBlackUpdate() throws DataAccessException {
        userDAO.createUser(new UserData("btest1", "null", "null@null.null"));
        userDAO.createUser(new UserData("btest2", "null", "null@null.null"));
        userDAO.createUser(new UserData("NEWbtest1", "null", "null@null.null"));
        int id = gameDAO.createGame(new GameData(0, "btest1", "btest2", "btest", new ChessGame()));
        gameDAO.updateWhitePlayer(id, "NEWbtest1");
        GameData game = gameDAO.getGame(id);
        Assertions.assertEquals("NEWbtest1", game.whiteUsername());
    }

    @Test
    void failedWhiteUpdate() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    gameDAO.updateWhitePlayer(-1, "I Wanna Play!");
                });
    }

    @Test
    void failedBlackUpdate() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    gameDAO.updateBlackPlayer(-1, "I Wanna Play Too!");
                });
    }

    @Test
    void successfulGamesList() throws DataAccessException {
        userDAO.createUser(new UserData("1", "null", "null@null.null"));
        userDAO.createUser(new UserData("2", "null", "null@null.null"));
        int id = gameDAO.createGame(new GameData(0, "1", "2", "1-2", new ChessGame()));
        Collection<GameData> games = gameDAO.listGames();
        ArrayList<GameData> gameList = new ArrayList<>(games);
        Assertions.assertEquals(gameList.get(0), new GameData(1, "1", "2", "1-2", new ChessGame()));
    }

    @Test
    void successfulEmptyGamesList() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertEquals(new ArrayList<GameData>(), games);
    }

    @Test
    void chessGameSerializeDeserialize() throws DataAccessException, InvalidMoveException {
        ChessGame game = new ChessGame();
        ChessPosition start = new ChessPosition(2, 5);
        ChessPosition end = new ChessPosition(4, 5);
        ChessMove move = new ChessMove(start, end, null);
        game.makeMove(move);
        GameData initialGame = new GameData(1, null, null, "One Move", game);
        int id = gameDAO.createGame(initialGame);
        GameData receivedGame = gameDAO.getGame(id);
        Assertions.assertEquals(initialGame, receivedGame);
    }

}
