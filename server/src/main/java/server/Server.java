package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import service.ClearResponse;
import service.ClearService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", ctx -> clearHandler(ctx));
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
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(gson.toJson(new ClearResponse("Error: " + e.getMessage())));
        }
    }
}
