package client;

import client.websocket.NotificationHandler;
import exception.ResponseException;
import chess.*;
import ui.DrawBoard;

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
            throw new ResponseException(400, "Register Format: register <username> <password> <email>");
        }

        try{
            var response = server.register(params[0], params[1], params[2]);
            this.authToken = response.authToken();
            this.state = State.SIGNEDIN;
            return "Registered and signed in as " + response.username();
        }
        catch (ResponseException e){
            return "Error: User already exists in database";
        }

    }

    public String login(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Login Format: login <username> <password>");
        }

        try {
            var response = server.login(params[0], params[1]);
            this.authToken = response.authToken();
            this.state = State.SIGNEDIN;
            return "Logged in as " + response.username();
        } catch (ResponseException e) {
            return "Login failed: Invalid Username or Password";
        }
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
            throw new ResponseException(400, "createGame format: createGame <gameName>");
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
            throw new ResponseException(400, "joinGame format: joinGame <gameID> <WHITE|BLACK>");
        }

        try{
            int gameID = Integer.parseInt(params[0]);
            String playerColor = params[1].toUpperCase();
            var response = server.joinGame(authToken, gameID, playerColor);

            // For white's perspective:
            ChessGame newGame = new ChessGame();
            ChessBoard board = newGame.getBoard();

            if (playerColor.equals("WHITE")){
                DrawBoard whiteBoard = new DrawBoard(board, playerColor);
                whiteBoard.drawBoard();
            }
            else if (playerColor.equals("BLACK")){
                DrawBoard blackBoard = new DrawBoard(board, playerColor);
                blackBoard.drawBoard();
            }else{
                throw new IllegalArgumentException("Invalid player color: " + playerColor);
            }

            return "Joined game " + gameID + " as " + params[1].toUpperCase();
        }
        catch (ResponseException e){
            return "Error: Invalid Game Request";
        }
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length != 1) {
            throw new ResponseException(400, "observeGame format: observe <gameID>");
        }

        try{
            int gameID = Integer.parseInt(params[0]);
            var response = server.joinGame(authToken, gameID,null);

            // For white's perspective:
            ChessGame newObserveGame = new ChessGame();
            ChessBoard observeBoard = newObserveGame.getBoard();
            DrawBoard drawObserveBoard = new DrawBoard(observeBoard, "WHITE");
            drawObserveBoard.drawBoard();
            return "Joined game " + gameID + " as observer";
        } catch (ResponseException e) {
            System.out.println("[DEBUG] Server rejected observe request: " + e.getMessage());
            return "Error: Invalid Observe Request - " + e.getMessage();
        } catch (NumberFormatException e) {
            System.out.println("[DEBUG] Failed to parse game ID: " + params[0]);
            return "Error: Invalid game ID format.";
        } catch (Exception e) {
            System.out.println("[DEBUG] Unexpected error in observeGame: " + e.getMessage());
            e.printStackTrace();  // Optional: comment this out later
            return "Error: Unexpected issue while trying to observe.";
        }
    }

    public String help() {
        return (state == State.SIGNEDOUT) ? """
                Available commands:
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                - help
                """ : """
                Available commands:
                - logout
                - createGame <gameName>
                - listGames
                - joinGame <gameID> <WHITE|BLACK>
                - observe <gameID>
                - help
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

