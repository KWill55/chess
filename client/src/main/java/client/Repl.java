package client;

import client.websocket.ChessNotificationHandler;
import client.websocket.NotificationHandler;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;
    private final PreLoginRepl preLogin;
    private final PostLoginRepl postLogin;

    public Repl(int serverPort) {
        NotificationHandler notificationHandler = new ChessNotificationHandler();
        this.client = new ChessClient(serverPort, notificationHandler);
        this.preLogin = new PreLoginRepl(client);
        this.postLogin = new PostLoginRepl(client);
    }

    public void run() {
        System.out.println("♔ Welcome to 240 Chess!");
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();

            try {
                if (client.isLoggedIn()) {
                    result = postLogin.eval(input);
                } else {
                    result = preLogin.eval(input);
                }
                System.out.print(result);
            } catch (Exception e) {
                e.printStackTrace(); // for debugging
                System.out.print("Error: " + e.getMessage());
            }
        }

        System.out.println("\nGoodbye!");
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }
}
