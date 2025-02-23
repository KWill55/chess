package dataaccess;

import model.UserData;
import model.GameData;
import model.AuthData;
import java.util.List;


public interface dDataAccess {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username);

    void createAuth(AuthData auth);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);

    void createGame(GameData game);
    GameData getGame(int gameID);
    List<GameData> listGames();
    void updateGame(GameData game);

    void clear();
}
