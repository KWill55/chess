package model;

/**
 * Represents a request to join a chess game.
 *
 * @param authToken   The authentication token of the user attempting to join the game.
 * @param playerColor The color the player wants to play as ("WHITE" or "BLACK").
 * @param gameID      The unique identifier of the game the player wants to join.
 */
public record JoinGameRequest(String authToken, String playerColor, int gameID) {
}
