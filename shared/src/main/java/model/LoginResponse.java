package model;

/**
 * Represents the response returned after a successful login.
 *
 * @param username  The username of the authenticated user.
 * @param authToken The authentication token generated for the session.
 */
public record LoginResponse(String username, String authToken) {}
