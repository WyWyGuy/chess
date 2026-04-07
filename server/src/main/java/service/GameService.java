package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.List;
import java.util.Objects;

public class GameService {

    private DatabaseGameDAO gameDAO = new DatabaseGameDAO();
    private DatabaseAuthDAO authDAO = new DatabaseAuthDAO();

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException, ServiceException {
        if (!authDAO.authExists(authToken)) {
            throw new ServiceException("Error: unauthorized", 401);
        }
        ChessGame game = new ChessGame();
        int id = gameDAO.createGame(new GameData(0, null, null, request.gameName(), game));
        return new CreateGameResult(id);
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException, ServiceException {
        if (!authDAO.authExists(authToken)) {
            throw new ServiceException("Error: unauthorized", 401);
        }
        List<GameData> allGames = gameDAO.listGames();
        return new ListGamesResult(allGames);
    }

    public void joinGame(JoinGameRequest request, String authToken) throws DataAccessException, ServiceException {
        if (!authDAO.authExists(authToken)) {
            throw new ServiceException("Error: unauthorized", 401);
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (!gameDAO.gameExists(request.gameID())) {
            throw new DataAccessException("Error: game " + request.gameID() + " does not exist");
        }
        GameData game = gameDAO.getGame(request.gameID());
        // Adding checks here and throwing a service exception could prevent joining the game as both teams.
        if ((request.playerColor() == ChessGame.TeamColor.WHITE) && game.whiteUsername() == null) {
            gameDAO.updateWhitePlayer(request.gameID(), auth.username());
        } else if ((request.playerColor() == ChessGame.TeamColor.BLACK) && game.blackUsername() == null) {
            gameDAO.updateBlackPlayer(request.gameID(), auth.username());
        } else {
            throw new ServiceException("Error: already taken", 403);
        }
    }

}
