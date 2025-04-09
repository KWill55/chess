package client;

import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import chess.*;
import model.GameData;
import ui.DrawBoard;
import websocket.commands.UserGameCommand;

import java.util.Arrays;

public class ChessClient {
    private final ServerFacade server;
    private final NotificationHandler notificationHandler;
    private final int serverPort;
    private WebSocketFacade webSocket;
    private String authToken = null;
    private State state = State.SIGNEDOUT;

    public ChessClient(int serverUrl, NotificationHandler notificationHandler) {
        this.server = new ServerFacade(serverUrl);
        this.notificationHandler = notificationHandler;
        this.serverPort = serverUrl;
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
            throw new ResponseException(400, "createGame format: createGame <game name>");
        }

        String gameName = params[0];
        var response = server.createGame(authToken, gameName);
        return "Game created";
    }


    public String listGames() throws ResponseException {
        assertSignedIn();
        var response = server.listGames(authToken);
        var builder = new StringBuilder("available games\n");
        int gameNumber = 1;

        if (response.games().size() == 0){
            System.out.print("There are no ");
        }

        for (var game : response.games()) {
            String blackUser = (game.blackUsername() != null) ? game.blackUsername() : "Empty";
            String whiteUser = (game.whiteUsername() != null) ? game.whiteUsername() : "Empty";

            builder.append("Game: ").append(gameNumber++)
                    .append("\n | Name: ").append(game.gameName()).append("\n")
                    .append(" | Black User : ").append(blackUser).append("\n")
                    .append(" | White User: ").append(whiteUser).append("\n");
        }
        return builder.toString();
    }

    public void makeMove(int gameID, String from, String to) {
        try {
            ChessPosition fromPos = parsePosition(from);
            ChessPosition toPos = parsePosition(to);
            ChessMove move = new ChessMove(fromPos, toPos, null); // handle promotion later

            UserGameCommand command = new UserGameCommand(
                    UserGameCommand.CommandType.MAKE_MOVE,
                    authToken,
                    gameID,
                    move
            );

            webSocket.send(command);
        } catch (Exception e) {
            System.out.println("Invalid move: " + e.getMessage());
        }
    }



    public void redrawBoard() {
        System.out.println("Redrawing board...");
        ChessBoard board = webSocket.getLatestBoard();
        String color = webSocket.getPlayerColor();
        if (board == null || color == null) {
            System.out.println("No board state available yet.");
            return;
        }
        DrawBoard drawBoard = new DrawBoard(board, color);
        drawBoard.drawBoard();
    }



    public void resignGame(int gameID) {
        try {
            UserGameCommand resignCmd = new UserGameCommand(
                    UserGameCommand.CommandType.RESIGN,
                    authToken,
                    gameID
            );
            webSocket.send(resignCmd);
        } catch (ResponseException e) {
            System.out.println("Error resigning: " + e.getMessage());
        }
    }


    public void leaveGame(int gameID) {
        try {
            UserGameCommand leaveCmd = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            webSocket.send(leaveCmd);
        } catch (Exception e) {
            System.out.println("Failed to leave game: " + e.getMessage());
        }
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();

        if (params.length != 2) {
            throw new ResponseException(400, "joinGame format: joinGame <game number> <WHITE|BLACK>");
        }

        try {
            int gameNumber = Integer.parseInt(params[0]);
            String playerColor = params[1].toUpperCase();

            var response = server.listGames(authToken);
            var games = response.games();

            if (gameNumber < 1 || gameNumber > games.size()) {
                throw new ResponseException(404, "Game number " + gameNumber + " does not exist.");
            }

            var selectedGame = games.get(gameNumber - 1);
            int gameID = selectedGame.gameID();

            if (playerColor.equals("WHITE")) {
                if (selectedGame.whiteUsername() != null) {
                    throw new ResponseException(403, "White player slot is already taken.");
                }
            } else if (playerColor.equals("BLACK")) {
                if (selectedGame.blackUsername() != null) {
                    throw new ResponseException(403, "Black player slot is already taken.");
                }
            } else {
                throw new IllegalArgumentException("Invalid player color: " + playerColor);
            }

            var joinResponse = server.joinGame(authToken, gameID, playerColor);

            this.webSocket = new WebSocketFacade("http://localhost:" + serverPort, notificationHandler);
            webSocket.setPlayerColor(playerColor);
            webSocket.connectToGame(authToken, gameID);

            GameData joinedGame = null;
            for (GameData g : games) {
                if (g.gameID() == gameID) {
                    joinedGame = g;
                    break;
                }
            }
            if (joinedGame == null) {
                return "Error: Game not found.";
            }
            GameRepl gameRepl = new GameRepl(this, joinedGame, playerColor);
            gameRepl.run();


            return "Exited Game " + gameNumber + ".";
        } catch (NumberFormatException e) {
            throw new ResponseException(400, params[0] + " is not a valid game number.");
        }
    }


    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();

        if (params.length != 1) {
            throw new ResponseException(400, "observeGame format: observe <game ID>");
        }
        ChessGame newGame = new ChessGame();
        ChessBoard board = newGame.getBoard();
        webSocket.setPlayerColor("WHITE");
        DrawBoard whiteBoard = new DrawBoard(board, "WHITE");
        whiteBoard.drawBoard();

        return "success";
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

    private ChessPosition parsePosition(String pos) {
        int col = pos.charAt(0) - 'a';                // 'a' = 0
        int row = 8 - (pos.charAt(1) - '0');          // '1' = 7, ..., '8' = 0

        return new ChessPosition(row + 1, col + 1);   // shift to 1-based indexing
    }
}

