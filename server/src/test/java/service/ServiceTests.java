package service;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

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

}
