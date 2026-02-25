package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", this::clearHandler);
        javalin.post("/user", this::registerHandler);
        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clearHandler(Context ctx) {
        ClearService clearService = new ClearService();
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
        UserService userService = new UserService();
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
        AuthService authService = new AuthService();
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
        AuthService authService = new AuthService();
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
}
