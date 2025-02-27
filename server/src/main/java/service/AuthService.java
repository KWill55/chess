package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.LoginRequest;
import model.LoginResponse;
import model.AuthData;
import model.UserData;
import dataaccess.DataAccessException;
import java.util.UUID;

/*
* manages logging in and logging out
* Generates authTokens
* validates authTokens
 */

public class AuthService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public AuthService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public LoginResponse login(LoginRequest request) throws DataAccessException {
        try {
            UserData user = userDAO.getUser(request.username());

            if (user == null) {
                System.out.println("DEBUG: User not found in database for username: " + request.username());
                throw new DataAccessException("Error: Unauthorized");
            }


            System.out.println("Password from user request is " + request.password());
            if (user.username() != null){
                System.out.println("Password from userDAO for " + user.username() + " is " + user.password());
            }
            else{
                System.out.println("User.username() is null");
            }


            if (!user.password().equals(request.password())) {
                throw new DataAccessException("Error: Unauthorized");
            }

            // Generate and store auth token
            String authToken = UUID.randomUUID().toString();
            authDAO.createAuth(new AuthData(authToken, request.username()));

            return new LoginResponse(request.username(), authToken);

        } catch (NullPointerException | DataAccessException e) {
            throw new DataAccessException("Error: Unauthorized");
        }
    }

    public void logout(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }

    public boolean validateAuth(String authToken) {
        try {
            return authDAO.getAuth(authToken) != null;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public void clearAll() throws DataAccessException {
        authDAO.clear();  // Clears all authentication tokens
    }

}
