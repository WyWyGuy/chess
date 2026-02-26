package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.GameData;

import java.util.Collection;

public class GameService {

    private MemoryGameDAO gameDAO = new MemoryGameDAO();
    private MemoryAuthDAO authDAO = new MemoryAuthDAO();

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        if (!authDAO.authExists(authToken)) {
            throw new DataAccessException("Auth token does not exist");
        }
        ChessGame game = new ChessGame();
        int ID = gameDAO.createGame(new GameData(0, null, null, request.gameName(), game));
        return new CreateGameResult(ID);
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        if (!authDAO.authExists(authToken)) {
            throw new DataAccessException("Auth token does not exist");
        }
        Collection<GameData> allGames = gameDAO.listGames();
        return new ListGamesResult(allGames);
    }

}
