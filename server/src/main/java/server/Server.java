package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import org.eclipse.jetty.server.Authentication;
import service.*;

import javax.xml.crypto.Data;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    private AuthService authService = new AuthService();
    private GameService gameService = new GameService();
    private UserService userService = new UserService();
    private ClearService clearService = new ClearService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", this::clearHandler);
        javalin.post("/user", this::registerHandler);
        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);
        javalin.post("/game", this::createGameHandler);
        javalin.get("/game", this::listGamesHandler);
        javalin.put("/game", this::joinGameHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clearHandler(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
            ctx.result(gson.toJson(new Object()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new Message("Error: " + e.getMessage())));
        }
    }

    private void registerHandler(Context ctx) {
        RegisterRequest registerRequest;
        try {
            registerRequest = gson.fromJson(ctx.body(), RegisterRequest.class);
        } catch (Exception e) {
            ctx.status(400);
            ctx.result(gson.toJson(new Message("Error: bad request")));
            return;
        }
        if (registerRequest.username() == null ||
                registerRequest.password() == null ||
                registerRequest.email() == null) {
            ctx.status(400);
            ctx.result(gson.toJson(new Message("Error: bad request")));
            return;
        }
        try {
            RegisterResult registerResult = userService.register(registerRequest);
            ctx.status(200);
            ctx.result(gson.toJson(registerResult));
        } catch (DataAccessException e) {
            ctx.status(403);
            ctx.result(gson.toJson(new Message("Error: already taken")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new Message("Error: " + e.getMessage())));
        }
    }

    public void loginHandler(Context ctx) {
        LoginRequest loginRequest;
        try {
            loginRequest = gson.fromJson(ctx.body(), LoginRequest.class);
        } catch (Exception e) {
            ctx.status(400);
            ctx.result(gson.toJson(new Message("Error: bad request")));
            return;
        }
        if (loginRequest.username() == null ||
                loginRequest.password() == null) {
            ctx.status(400);
            ctx.result(gson.toJson(new Message("Error: bad request")));
            return;
        }
        try {
            LoginResult loginResult = authService.login(loginRequest);
            ctx.status(200);
            ctx.result(gson.toJson(loginResult));
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.result(gson.toJson(new Message("Error: unauthorized")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new Message("Error: " + e.getMessage())));
        }
    }

    public void logoutHandler(Context ctx) {
        String authToken = ctx.header("Authorization");
        if (authToken == null) {
            ctx.status(401);
            ctx.result(gson.toJson(new Message("Error: unauthorized")));
        }
        try {
            authService.logout(authToken);
            ctx.status(200);
            ctx.result(gson.toJson(new Object()));
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.result(gson.toJson(new Message("Error: unauthorized")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new Message("Error: " + e.getMessage())));
        }
    }

    public void createGameHandler(Context ctx) {
        CreateGameRequest createGameRequest;
        String authToken = ctx.header("Authorization");
        if (authToken == null) {
            ctx.status(401);
            ctx.result(gson.toJson(new Message("Error: unauthorized")));
        }
        try {
            createGameRequest = gson.fromJson(ctx.body(), CreateGameRequest.class);
        } catch (Exception e) {
            ctx.status(400);
            ctx.result(gson.toJson(new Message("Error: bad request")));
            return;
        }
        if (createGameRequest.gameName() == null) {
            ctx.status(400);
            ctx.result(gson.toJson(new Message("Error: bad request")));
            return;
        }
        try {
            CreateGameResult createGameResult = gameService.createGame(createGameRequest, authToken);
            ctx.status(200);
            ctx.result(gson.toJson(createGameResult));
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.result(gson.toJson(new Message("Error: unauthorized")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new Message("Error: " + e.getMessage())));
        }
    }

    public void listGamesHandler(Context ctx) {
        String authToken = ctx.header("Authorization");
        if (authToken == null) {
            ctx.status(401);
            ctx.result(gson.toJson(new Message("Error: unauthorized")));
        }
        try {
            ListGamesResult listGamesResult = gameService.listGames(authToken);
            ctx.status(200);
            ctx.result(gson.toJson(listGamesResult));
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.result(gson.toJson(new Message("Error: unauthorized")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new Message("Error: " + e.getMessage())));
        }
    }

    public void joinGameHandler(Context ctx) {
        JoinGameRequest joinGameRequest;
        String authToken = ctx.header("Authorization");
        if (authToken == null) {
            ctx.status(401);
            ctx.result(gson.toJson(new Message("Error: unauthorized")));
        }
        try {
            joinGameRequest = gson.fromJson(ctx.body(), JoinGameRequest.class);
        } catch (Exception e) {
            ctx.status(400);
            ctx.result(gson.toJson(new Message("Error: bad request")));
            return;
        }
        if ((joinGameRequest.playerColor() == null) ||
                joinGameRequest.gameID() <= 0) {
            ctx.status(400);
            ctx.result(gson.toJson(new Message("Error: bad request")));
            return;
        }
        try {
            gameService.joinGame(joinGameRequest, authToken);
            ctx.status(200);
            ctx.result(gson.toJson(new Object()));
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().contains("already taken")) {
                ctx.status(403);
                ctx.result(gson.toJson(new Message("Error: already taken")));
            } else if (e.getMessage().contains("Auth token does not exist")) {
                ctx.status(401);
                ctx.result(gson.toJson(new Message("Error: unauthorized")));
            } else {
                ctx.status(400);
                ctx.result(gson.toJson(new Message("Error: bad request")));
            }
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new Message("Error: " + e.getMessage())));
        }
    }
}
