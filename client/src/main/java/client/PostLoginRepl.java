package client;

import exception.ResponseException;

import java.util.Scanner;

public class PostLoginRepl {
    private final ChessClient client;

    public PostLoginRepl(ChessClient client) {
        this.client = client;
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
