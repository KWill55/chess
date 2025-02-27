package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.*;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class GameService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    //TODO
    public ListGamesResponse listGames(ListGamesRequest request) throws DataAccessException {
        List<GameData> gamesList = new ArrayList<>();
        return new ListGamesResponse(gamesList);
    }

    //TODO
    public CreateGameResponse createGame(CreateGameRequest request) throws DataAccessException {
        int gameID = 1234; //TODO generate random gameID?

//        GameData newGame = new GameData(gameID, null, null, request.gameName());
//        gameDAO.createGame(newGame);

        return new CreateGameResponse(gameID);
    }

    //TODO
    public JoinGameResponse joinGame(JoinGameRequest request) throws DataAccessException {
        System.out.println("Joined game unsuccessfully");
        return new JoinGameResponse();
    }


    public void clearAll() throws DataAccessException {
        gameDAO.clear();  // Clears all game data
    }
}
