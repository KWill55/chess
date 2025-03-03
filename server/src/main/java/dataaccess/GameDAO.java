package dataaccess;

import model.GameData;
import java.util.*;

public class GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1; //initial gameID

    // Create a new game and return its ID
    public int createGame(GameData game) throws DataAccessException {
        if (game == null || game.gameName() == null) {
            throw new DataAccessException("Error: Invalid game data");
        }

        //generate new gameID and store game into games
        int gameID = nextGameID++;
        games.put(gameID, game);
        return gameID;
    }

    // Retrieve a game by gameID
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Error: Game not found");
        }
        return games.get(gameID);
    }

    // Retrieve all games
    public List<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }

    // Update game data
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Error: Game not found");
        }
        games.put(gameID, updatedGame);
    }

    // Delete all games (for clear API)
    public void clear() {
        games.clear();
        nextGameID = 1; // Reset game ID counter
    }
}

