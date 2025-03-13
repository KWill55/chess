package dataaccess;

import model.GameData;
import java.util.*;

public class InMemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public int createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("Error: Game ID already exists.");
        }
        games.put(game.gameID(), game);
        return game.gameID();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Error: Game ID not found.");
        }
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Error: Game ID not found.");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public void clear() {
        games.clear();
    }
}
