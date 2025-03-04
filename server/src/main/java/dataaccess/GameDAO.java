package dataaccess;

import model.GameData;
import java.util.*;

public class GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1; // initial gameID

    // Create a new game and return its ID
    public int createGame(GameData game) throws DataAccessException {
        if (game == null || game.gameName() == null) {
            throw new DataAccessException("Error: Invalid game data");
        }

        // Generate new gameID and store the game
        int gameID = nextGameID++;
        GameData newGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(gameID, newGame);
        return gameID;
    }

    // Retrieve a game by gameID
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Error: Game not found");
        }
        return games.get(gameID);
    }

    // Retrieve all games (sorted by gameID for consistency)
    public List<GameData> getAllGames() {
        List<GameData> gameList = new ArrayList<>(games.values());
        gameList.sort(Comparator.comparingInt(GameData::gameID)); // Ensure sorted order
        return gameList;
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
