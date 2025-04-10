package client;

import client.websocket.WebSocketFacade;
import model.GameData;
import ui.DrawBoard;

import java.util.Scanner;

public class GameRepl {
    private final ChessClient client;
    private final GameData game;
    private final String playerColor;
    private final Scanner scanner = new Scanner(System.in);

    public GameRepl(ChessClient client, GameData game, String playerColor) {
        this.client = client;
        this.game = game;
        this.playerColor = playerColor;
    }

    public void run() {
        System.out.println("\u2654 Entered Game '" + game.gameName() + "' as " + playerColor);
        System.out.println("Type 'help' to see available in-game commands.\n");

        while (true) {
            System.out.print("[Game " + game.gameName() + "] >>> ");
            String input = scanner.nextLine().trim();
            String[] tokens = input.split("\\s+");

            if (tokens.length == 0) {
                continue;
            }

            switch (tokens[0].toLowerCase()) {
                case "move" -> {
                    if (tokens.length != 3) {
                        System.out.println("Usage: move <from> <to>");
                        continue;
                    }
                    String from = tokens[1];
                    String to = tokens[2];
                    client.makeMove(game.gameID(), from, to);
                }
                case "redraw" -> client.redrawBoard();
                case "highlight" -> {
                    if (tokens.length != 2) {
                        System.out.println("Usage: highlight <pos>");
                        continue;
                    }
                    String from = tokens[1];
                    client.highlightValidMoves(from);
                }
                case "resign" -> {
                    client.resignGame(game.gameID());
                    System.out.println("\u2620 You resigned the game.");
                }
                case "leave" -> {
                    client.leaveGame(game.gameID());
                    System.out.println("\u21a9 You left the game.");
                    return;
                }
                case "help" -> printHelp();
                default -> System.out.println("Unknown command. Type 'help' to see options.");
            }
        }
    }

    private void printHelp() {
        System.out.println("In-game commands:");
        System.out.println("- move <from> <to> (e.g. move e2 e4)");
        System.out.println("- resign (forfeit the game)");
        System.out.println("- leave (exit to lobby)");
        System.out.println("- redraw (reprint the board)");
        System.out.println("- highlight <pos> (highlight valid moves for piece)");
        System.out.println("- help (show this help)");
    }
}
