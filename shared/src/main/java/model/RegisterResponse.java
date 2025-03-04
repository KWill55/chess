package model;

/**
 * Represents the response to a successful user registration.
 *
 * @param username  The username of the newly registered user.
 * @param authToken The authentication token assigned to the user.
 */
public record RegisterResponse(String username, String authToken) {}
