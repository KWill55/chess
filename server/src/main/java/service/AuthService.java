package service;

import dataaccess.AuthDAO;
import model.AuthData;
import dataaccess.DataAccessException;
import java.util.UUID;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, username));
        return authToken;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }

    public String getUserFromAuth(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: Invalid auth token");
        }
        return authData.username();
    }

    public void clear() throws DataAccessException {
        authDAO.clear();
    }
}
