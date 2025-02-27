package service;


import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.RegisterRequest;
import model.RegisterResponse;
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


    public RegisterResponse register(RegisterRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("Error: Missing required fields");
        }

        // Check if username is already taken
        if (userDAO.getUser(request.username()) != null) {
            throw new DataAccessException("Error: Username already taken");
        }

        // Create and store new user
        UserData newUserData = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(newUserData);

        //TODO do i need to create a new authToken when registering?
        //create and store new authToken
        String authToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(authToken, newUserData.username());
        authDAO.createAuth(newAuthData);

        return new RegisterResponse(newUserData.username(),newAuthData.authToken());
    }


    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    public void clearAll() throws DataAccessException {
        userDAO.clear();  // Clears all user data
    }

}