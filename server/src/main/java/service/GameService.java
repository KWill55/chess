package service;

import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;
import chess.*;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public List<GameData> listGames() throws DataAccessException {
        return gameDAO.getAllGames();
    }

    public int createGame(String gameName) throws DataAccessException {
        ChessGame newGame = new ChessGame(); // Create a new ChessGame instance
        GameData gameData = new GameData(0, null, null, gameName, newGame); // Default values
        return gameDAO.createGame(gameData); // Store in database
    }


    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        gameDAO.updateGame(gameID, updatedGame);
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}
