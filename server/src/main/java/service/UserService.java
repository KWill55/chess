package service;


import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.LoginRequest;
import model.LoginResponse;
import model.UserData;
import model.AuthData;
import dataaccess.DataAccessException;
import java.util.UUID; //for creating authTokens


public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

//fix these later
//    public RegisterResult register(RegisterRequest registerRequest) {
//
//    }


//    public RegisterResult getUser(String username) {
//
//    }



}