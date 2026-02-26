package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Objects;

public class GameService {

    private MemoryGameDAO gameDAO = new MemoryGameDAO();
    private MemoryAuthDAO authDAO = new MemoryAuthDAO();

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        if (!authDAO.authExists(authToken)) {
            throw new DataAccessException("Auth token does not exist");
        }
        ChessGame game = new ChessGame();
        int id = gameDAO.createGame(new GameData(0, null, null, request.gameName(), game));
        return new CreateGameResult(id);
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        if (!authDAO.authExists(authToken)) {
            throw new DataAccessException("Auth token does not exist");
        }
        Collection<GameData> allGames = gameDAO.listGames();
        return new ListGamesResult(allGames);
    }

    public void joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        if (!authDAO.authExists(authToken)) {
            throw new DataAccessException("Auth token does not exist");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (!gameDAO.gameExists(request.gameID())) {
            throw new DataAccessException("Game " + request.gameID() + " does not exist");
        }
        GameData game = gameDAO.getGame(request.gameID());
        if ((request.playerColor() == ChessGame.TeamColor.WHITE) && game.whiteUsername() == null) {
            gameDAO.updateWhitePlayer(request.gameID(), auth.username());
        } else if ((request.playerColor() == ChessGame.TeamColor.BLACK) && game.blackUsername() == null) {
            gameDAO.updateBlackPlayer(request.gameID(), auth.username());
        } else {
            throw new DataAccessException("Cannot join game, already taken");
        }
    }

}
