package model;

import java.util.List;

/**
 * Represents a response containing a list of available games.
 *
 * @param games The list of games available.
 */
public record ListGamesResponse(List<GameData> games) {}
