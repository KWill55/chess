package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

/*
implements DAO methods that handle User Data
 */
public class UserDAO {

    //temporary db structure until SQL
    private final Map<String, UserData> users = new HashMap<>();

    //createUser
    void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Error: Username already taken");
        }
        users.put(user.username(), user);
    }

    //getUser
    public UserData getUser(UserData user) throws DataAccessException {
        return users.get(user.username());
    }
}
