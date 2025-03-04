package model;

/**
 * Represents authentication data containing a unique auth token
 * and the associated username.
 *
 * @param authToken Unique token assigned to a user session.
 * @param username The username associated with this authentication session.
 */
public record AuthData(String authToken, String username) {}
