package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import model.LoginRequest;
import model.LoginResponse;
import model.AuthData;
import model.UserData;
import dataaccess.DataAccessException;
import java.util.UUID;

public class GameService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

}
