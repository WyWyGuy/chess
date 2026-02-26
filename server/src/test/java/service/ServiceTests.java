package service;

import dataaccess.DataAccessException;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

    @Test
    void successfulClear() throws DataAccessException {
        var clearService = new ClearService();
        clearService.clear();
    }

    @Test
    void successfulRegister() throws DataAccessException {
        var userService = new UserService();
        RegisterResult registerResult = userService.register(new RegisterRequest("Wyatt", "p@ssw0rd", "wywyguy@byu.edu"));
        Assertions.assertNotNull(registerResult.authToken());
        Assertions.assertEquals("Wyatt", registerResult.username());
    }

    @Test
    void failedRegisterTwice() throws DataAccessException {
        var userService = new UserService();
        RegisterResult registerResult1 = userService.register(new RegisterRequest("Bob", "ILoveBuilding", "bob@bob.bob"));
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    userService.register(
                            new RegisterRequest("Bob", "DidIAlreadySignUp?", "bob2@bob.bob")
                    );
                });
    }

    @Test
    void successfulLogin() throws DataAccessException {
        var userService = new UserService();
        var authService = new AuthService();
        RegisterResult registerResult = userService.register(new RegisterRequest("John", "#1baptist", "john@12apostles.cel"));
        LoginResult loginResult = authService.login(new LoginRequest("John", "#1baptist"));
        Assertions.assertNotNull(loginResult.authToken());
        Assertions.assertEquals("John", loginResult.username());
    }

    @Test
    void failedLoginWrongPassword() throws DataAccessException {
        var userService = new UserService();
        var authService = new AuthService();
        RegisterResult registerResult = userService.register(new RegisterRequest("Charles", "stinkingBishop500", "cheddarchesse@gmail.com"));
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    authService.login(
                            new LoginRequest("Charles", "chessnuts1019")
                    );
                });
    }

    @Test
    void successfulLogout() throws DataAccessException {
        var userService = new UserService();
        var authService = new AuthService();
        RegisterResult registerResult = userService.register(new RegisterRequest("Scooby-doo", "ruhRuh*600", "food@mystery.inc"));
        authService.logout(registerResult.authToken());
    }

    @Test
    void failedLogoutNotLoggedIn() throws DataAccessException {
        var authService = new AuthService();
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    authService.logout("11111111-00000000000-99999999999");
                });
    }

    @Test
    void successfulCreateGame() throws DataAccessException {
        var userService = new UserService();
        var gameService = new GameService();
        RegisterResult registerResult = userService.register(new RegisterRequest("Levi", "Levi", "Levi@Levi.com"));
        CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest("Super Levi Galaxy"), registerResult.authToken());
        Assertions.assertInstanceOf(Integer.class, createGameResult.gameID());
    }

    @Test
    void failedCreateGameUnauthorized() throws DataAccessException {
        var gameService = new GameService();
        Assertions.assertThrows(DataAccessException.class,
                () -> {
                    gameService.createGame(new CreateGameRequest("Checkers"), "0");
                });
    }
    
}
