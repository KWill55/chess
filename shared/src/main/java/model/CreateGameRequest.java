package model;

/**
 * Represents a request to create a new game.
 *
 * @param authToken The authentication token of the user making the request.
 * @param gameName The name of the game to be created.
 *
 */
public record CreateGameRequest(String gameName) {}
