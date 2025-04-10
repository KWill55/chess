package model;

import chess.ChessGame;

/**
 * Represents the data for a chess game.
 *
 * @param gameID        The unique identifier for the game.
 * @param whiteUsername The username of the player playing as white (null if unassigned).
 * @param blackUsername The username of the player playing as black (null if unassigned).
 * @param gameName      The name of the game.
 * @param game          The current state of the chess game.
 */
public record GameData(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game,
        boolean gameOver
) {
    // Add this helper constructor for compatibility with test code
    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this(gameID, whiteUsername, blackUsername, gameName, game, false);
    }
}
