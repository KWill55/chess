package model;

/**
 * Represents user data including username, password, and email.
 *
 * @param username The unique identifier for the user.
 * @param password The password associated with the user.
 * @param email    The email address of the user.
 */
public record UserData(String username, String password, String email) {}
