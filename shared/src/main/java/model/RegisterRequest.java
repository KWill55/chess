package model;

/**
 * Represents a request to register a new user.
 *
 * @param username The desired username of the new user.
 * @param password The password for the new user.
 * @param email The email address associated with the new user.
 */
public record RegisterRequest(String username, String password, String email) {}
