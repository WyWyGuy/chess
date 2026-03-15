package client;

import com.google.gson.Gson;
import model.*;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;

public class ServerFacade {

    private String hostname;
    private int port;
    private static final int TIMEOUT = 5000;
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String authToken;

    public ServerFacade(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    private <T> T makeRequest(String method, String endpoint, Object request, Class<T> responseClass) throws Exception {
        String requestString = gson.toJson(request);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI("http://" + hostname + ":" + port + endpoint))
                .timeout(java.time.Duration.ofMillis(TIMEOUT));
        if (authToken != null) {
            builder.header("Authorization", authToken);
        }
        switch (method) {
            case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(requestString, StandardCharsets.UTF_8));
            case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.ofString(requestString, StandardCharsets.UTF_8));
            case "GET" -> builder.GET();
            case "DELETE" -> builder.DELETE();
        }
        HttpRequest httpRequest = builder.build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() != 200) {
            throw new Exception(httpResponse.body());
        }
        return gson.fromJson(httpResponse.body(), responseClass);
    }

    public AuthData register(String username, String password, String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        RegisterResult registerResult = makeRequest("POST", "/user", registerRequest, RegisterResult.class);
        this.authToken = registerResult.authToken();
        return new AuthData(registerResult.authToken(), registerResult.username());
    }

    public AuthData login() throws Exception {
        throw new Exception("not implemented");
    }

    public void logout() throws Exception {
        //CLEAR THIS.AUTHTOKEN
        throw new Exception("not implemented");
    }

    public int createGame() throws Exception {
        throw new Exception("not implemented");
    }

    public Collection<GameData> listGames() throws Exception {
        throw new Exception("not implemented");
    }

    public void joinGame() throws Exception {
        throw new Exception("not implemented");
    }

}
