package client;

import java.util.Scanner;

public class PreLoginRepl {
    private final ChessClient client;

    public PreLoginRepl(ChessClient client) {
        this.client = client;
    }

    public String run() {
        System.out.println("\n== Pre-login Commands ==");
        System.out.println(client.help());
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (true) {
            System.out.print("\n>>> ");
            String input = scanner.nextLine();

            try {
                result = client.eval(input);

                if (result.equals("login-success") || result.equals("register-success")) {
                    System.out.println("Login/Register successful!");
                    return result; // Transition to post-login
                } else if (result.equals("quit")) {
                    return "quit";
                }

                System.out.print(result);
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
