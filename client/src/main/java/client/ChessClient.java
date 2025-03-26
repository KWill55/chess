package client;

import client.websocket.NotificationHandler;
import exception.ResponseException;
import model.*;
import client.ServerFacade;

import java.util.Arrays;

public class ChessClient {
    private final ServerFacade server;
    private final NotificationHandler notificationHandler;
    private String authToken = null;
    private State state = State.SIGNEDOUT;

    public ChessClient(int serverUrl, NotificationHandler notificationHandler) {
        this.server = new ServerFacade(serverUrl);
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd.toLowerCase()) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "creategame" -> createGame();
                case "listgames" -> listGames();
                case "joingame" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                case "help" -> help();
                case "clear" -> clearDatabase();
                default -> "Unknown command. Type 'help' to see options.";
            };
        } catch (ResponseException ex) {
            return "Error: " + ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length != 3) {
            throw new ResponseException(400, "Usage: register <username> <password> <email>");
        }
        var response = server.register(params[0], params[1], params[2]);
        this.authToken = response.authToken();
        this.state = State.SIGNEDIN;
        return "Registered and signed in as " + response.username();
    }

    public String login(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Usage: login <username> <password>");
        }
        var response = server.login(params[0], params[1]);
        this.authToken = response.authToken();
        this.state = State.SIGNEDIN;
        return "Logged in as " + response.username();
    }

    public String logout() throws ResponseException {

        if (authToken == null) {
            throw new ResponseException(400, "Not logged in");
        }
        server.logout(authToken);
        authToken = null;
        state = State.SIGNEDOUT;
        return "Logged out.";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: createGame <gameName>");
        }

        String gameName = params[0];
        var response = server.createGame(authToken, gameName);
        return "Game created with ID: " + response.gameID();
    }


    public String listGames() throws ResponseException {
        assertSignedIn();
        var response = server.listGames(authToken);
        var builder = new StringBuilder("Available games:\n");
        for (var game : response.games()) {
            builder.append("ID: ").append(game.gameID())
                    .append(" | Name: ").append(game.gameName()).append("\n");
        }
        return builder.toString();
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length != 2) {
            throw new ResponseException(400, "Usage: joinGame <gameID> <WHITE|BLACK>");
        }
        int gameID = Integer.parseInt(params[0]);
        var response = server.joinGame(authToken, gameID, params[1].toUpperCase());
        return "Joined game " + gameID + " as " + params[1].toUpperCase();
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length != 1) {
            throw new ResponseException(400, "Usage: observe <gameID>");
        }
        int gameID = Integer.parseInt(params[0]);
        var response = server.observeGame(authToken, gameID);
        return "Now observing game " + gameID;
    }

    public String help() {
        return (state == State.SIGNEDOUT) ? """
                Available commands:
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                """ : """
                Available commands:
                - logout
                - createGame <gameName>
                - listGames
                - joinGame <gameID> <WHITE|BLACK>
                - observe <gameID>
                - quit
                """;
    }

    public String clearDatabase() throws ResponseException {
        server.clear(); // Calls ServerFacade
        return "Database cleared.";
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(401, "You must be logged in.");
        }
    }

    public boolean isLoggedIn() {
        return this.authToken != null && !this.authToken.isEmpty();
    }

    private enum State {
        SIGNEDOUT,
        SIGNEDIN
    }
}

