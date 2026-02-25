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

/*
    @Test
    void successfulLogin() {
        var authService = AuthorizationService.getInstance();
        var registerRequest = new RegisterRequest("name", "password", "email");
        LoginResponse registerResponse = authService.register(registerRequest);

        var loginRequest = new LoginRequest("name", "password");
        LoginResponse loginResponse = authService.login(loginRequest);

        Assertions.assertTrue(loginResponse.message() == null || loginResponse.message().equals(""));
        Assertions.assertEquals("name", loginResponse.username());
        Assertions.assertFalse(loginResponse.authToken() == null || loginResponse.authToken().equals(???));
    }

    @Test
    void failedLogin() {
        var loginService = AuthorizationService.getInstance();
        var loginRequest = new LoginRequest("name", "password");
        Assertions.assertThrows(???);
    }
*/
}
