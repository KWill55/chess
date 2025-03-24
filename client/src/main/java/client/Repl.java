package client;

import java.util.Scanner;
import client.websocket.ChessNotificationHandler;
import client.websocket.NotificationHandler;

public class Repl {
    private final ChessClient client;

    public Repl(int serverUrl) {
        NotificationHandler notificationHandler = new ChessNotificationHandler();
        this.client = new ChessClient(serverUrl, notificationHandler);
    }

    public void run() {
        System.out.println("♔ Welcome to 240 Chess!");
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();

            try {
                result = client.eval(input);
                System.out.print(result);
            } catch (Exception e) {
                System.out.print("Error: " + e.getMessage());
            }
        }

        System.out.println("\nGoodbye!");
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }
}
