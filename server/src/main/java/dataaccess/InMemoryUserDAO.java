package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class InMemoryUserDAO implements UserDAO {

    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (user == null || user.username() == null) {
            throw new DataAccessException("Error: Invalid user data");
        }
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Error: Username already taken");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("Error: Username cannot be null");
        }

        UserData user = users.get(username);

        if (user == null) {
            throw new DataAccessException("Error: User not found");
        }

        return user;
    }

    @Override
    public void clear() {
        users.clear();
    }
}
