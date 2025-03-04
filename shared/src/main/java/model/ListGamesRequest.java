package model;

/**
 * Represents a request to retrieve a list of available games.
 *
 * @param authToken The authentication token of the user making the request.
 */
public record ListGamesRequest(String authToken) {}
