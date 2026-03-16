package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        try {
            HttpRequest ping = HttpRequest.newBuilder()
                    .uri(new URI("http://" + hostname + ":" + port + "/"))
                    .timeout(java.time.Duration.ofMillis(TIMEOUT))
                    .GET()
                    .build();
            HttpResponse<String> pingResult = httpClient.send(ping, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception("Error: could not connect to the server");
        }
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
            Message error = gson.fromJson(httpResponse.body(), Message.class);
            throw new Exception(error.message());
        }
        if (responseClass == null) {
            return null;
        }
        return gson.fromJson(httpResponse.body(), responseClass);
    }

    public AuthData register(String username, String password, String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        RegisterResult registerResult = makeRequest("POST", "/user", registerRequest, RegisterResult.class);
        this.authToken = registerResult.authToken();
        return new AuthData(registerResult.authToken(), registerResult.username());
    }

    public AuthData login(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult loginResult = makeRequest("POST", "/session", loginRequest, LoginResult.class);
        this.authToken = loginResult.authToken();
        return new AuthData(loginResult.authToken(), loginResult.username());
    }

    public void logout() throws Exception {
        makeRequest("DELETE", "/session", null, null);
        this.authToken = null;
    }

    public int createGame(String gameName) throws Exception {
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
        CreateGameResult createGameResult = makeRequest("POST", "/game", createGameRequest, CreateGameResult.class);
        return createGameResult.gameID();
    }

    public List<GameData> listGames() throws Exception {
        ListGamesResult listGamesResult = makeRequest("GET", "/game", null, ListGamesResult.class);
        return listGamesResult.games();
    }

    public void joinGame(ChessGame.TeamColor color, int gameID) throws Exception {
        JoinGameRequest joinGameRequest = new JoinGameRequest(color, gameID);
        makeRequest("PUT", "/game", joinGameRequest, null);
    }

}
