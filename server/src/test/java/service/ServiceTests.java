package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

    @BeforeAll
    static void wipeDatabase() throws DataAccessException {
        var clearService = new ClearService();
        clearService.clear();
    }

    @Test
    void successfulClear() throws DataAccessException, ServiceException {
        var clearService = new ClearService();
        clearService.clear();
    }

    @Test
    void successfulRegister() throws DataAccessException, ServiceException {
        var userService = new UserService();
        RegisterResult registerResult = userService.register(new RegisterRequest("Wyatt", "p@ssw0rd", "wywyguy@byu.edu"));
        Assertions.assertNotNull(registerResult.authToken());
        Assertions.assertEquals("Wyatt", registerResult.username());
    }

    @Test
    void failedRegisterTwice() throws DataAccessException, ServiceException {
        var userService = new UserService();
        RegisterResult registerResult1 = userService.register(new RegisterRequest("Bob", "ILoveBuilding", "bob@bob.bob"));
        Assertions.assertThrows(ServiceException.class,
                () -> {
                    userService.register(
                            new RegisterRequest("Bob", "DidIAlreadySignUp?", "bob2@bob.bob")
                    );
                });
    }

    @Test
    void successfulLogin() throws DataAccessException, ServiceException {
        var userService = new UserService();
        var authService = new AuthService();
        RegisterResult registerResult = userService.register(new RegisterRequest("John", "#1baptist", "john@12apostles.cel"));
        LoginResult loginResult = authService.login(new LoginRequest("John", "#1baptist"));
        Assertions.assertNotNull(loginResult.authToken());
        Assertions.assertEquals("John", loginResult.username());
    }

    @Test
    void failedLoginWrongPassword() throws DataAccessException, ServiceException {
        var userService = new UserService();
        var authService = new AuthService();
        RegisterResult registerResult = userService.register(new RegisterRequest("Charles", "stinkingBishop500", "cheddarchesse@gmail.com"));
        Assertions.assertThrows(ServiceException.class,
                () -> {
                    authService.login(
                            new LoginRequest("Charles", "chessnuts1019")
                    );
                });
    }

    @Test
    void successfulLogout() throws DataAccessException, ServiceException {
        var userService = new UserService();
        var authService = new AuthService();
        RegisterResult registerResult = userService.register(new RegisterRequest("Scooby-doo", "ruhRuh*600", "food@mystery.inc"));
        authService.logout(registerResult.authToken());
    }

    @Test
    void failedLogoutNotLoggedIn() throws DataAccessException, ServiceException {
        var authService = new AuthService();
        Assertions.assertThrows(ServiceException.class,
                () -> {
                    authService.logout("11111111-00000000000-99999999999");
                });
    }

    @Test
    void successfulCreateGame() throws DataAccessException, ServiceException {
        var userService = new UserService();
        var gameService = new GameService();
        RegisterResult registerResult = userService.register(new RegisterRequest("Levi", "Levi", "Levi@Levi.com"));
        CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest("Super Levi Galaxy"), registerResult.authToken());
        Assertions.assertInstanceOf(Integer.class, createGameResult.gameID());
    }

    @Test
    void failedCreateGameUnauthorized() throws DataAccessException, ServiceException {
        var gameService = new GameService();
        Assertions.assertThrows(ServiceException.class,
                () -> {
                    gameService.createGame(new CreateGameRequest("Checkers"), "0");
                });
    }

    @Test
    void successfulListGames() throws DataAccessException, ServiceException {
        var userService = new UserService();
        var gameService = new GameService();
        RegisterResult registerResult = userService.register(new RegisterRequest("Steven", "theCakeIsALie", "Jumpman@mushroomKingdom"));
        CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest("Ready Player 1"), registerResult.authToken());
        ListGamesResult listGamesResult = gameService.listGames(registerResult.authToken());
        boolean found = listGamesResult.games().stream().anyMatch(game -> game.gameName().equals("Ready Player 1"));
        Assertions.assertTrue(found);
    }

    @Test
    void failedListGamesUnauthorized() throws DataAccessException, ServiceException {
        var gameService = new GameService();
        Assertions.assertThrows(ServiceException.class,
                () -> {
                    gameService.listGames("0");
                });
    }

    @Test
    void successfulJoinGame() throws DataAccessException, ServiceException {
        ClearService clearService = new ClearService();
        clearService.clear();
        var userService = new UserService();
        var gameService = new GameService();
        RegisterResult registerResult = userService.register(new RegisterRequest("Your Mom", "lol", "your@mom"));
        CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest("Your mom's house"), registerResult.authToken());
        gameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, createGameResult.gameID()), registerResult.authToken());
        ListGamesResult listGamesResult = gameService.listGames(registerResult.authToken());
        boolean found = listGamesResult.games().stream().anyMatch(game -> game.whiteUsername().equals("Your Mom"));
        Assertions.assertTrue(found);
    }

    @Test
    void failedJoinGameAlreadyTaken() throws DataAccessException, ServiceException {
        var userService = new UserService();
        var gameService = new GameService();
        RegisterResult registerResult1 = userService.register(new RegisterRequest("Guy 1", "secretTunnel", "guy1@gmail.com"));
        RegisterResult registerResult2 = userService.register(new RegisterRequest("Guy 2", "password", "guy2@gmail.com"));
        CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest("Guy Game"), registerResult1.authToken());
        gameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.BLACK, createGameResult.gameID()), registerResult2.authToken());
        Assertions.assertThrows(ServiceException.class,
                () -> {
                    gameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.BLACK, createGameResult.gameID()), registerResult1.authToken());
                });
    }

}
