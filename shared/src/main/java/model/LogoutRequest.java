package model;

/**
 * Represents a request to log out a user.
 *
 * @param authToken The authentication token of the user who wants to log out.
 */
public record LogoutRequest(String authToken) {}
