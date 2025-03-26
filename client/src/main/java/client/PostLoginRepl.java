package client;

import exception.ResponseException;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginRepl {
    private final ChessClient client;

    public PostLoginRepl(ChessClient client) {
        this.client = client;
    }

    public String eval(String input) throws ResponseException {
        var tokens = input.split(" ");
        var command = tokens[0];
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (command.toLowerCase()) {
            case "logout" -> client.logout();
            case "creategame" -> client.createGame(params);
            case "listgames" -> client.listGames();
            case "joingame" -> client.joinGame(params);
            case "observe" -> client.observeGame(params);
            case "help" -> client.help();
            case "quit" -> "quit";
            case "clear" -> client.clearDatabase();
            default -> "Unknown command. Type 'help' to see options.";
        };
    }


    public void run() {
        System.out.println("\n== Post-login Commands ==");
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("logout") && !result.equals("quit")) {
            System.out.print("\n>>> ");
            String input = scanner.nextLine();

            try {
                result = client.eval(input);

                if (result.equals("logout")) {
                    System.out.println("Logged out.");
                    break;
                } else if (result.equals("quit")) {
                    System.out.println("Goodbye.");
                    break;
                }

                System.out.print(result);
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
