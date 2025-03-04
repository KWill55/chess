package model;

/**
 * Represents a request to log in a user.
 *
 * @param username The username of the user attempting to log in.
 * @param password The password associated with the username.
 */
public record LoginRequest(String username, String password) {}
