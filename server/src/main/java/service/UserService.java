package service;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    public void createUser(UserData user) throws DataAccessException {
        userDAO.createUser(user);
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
    }
}
