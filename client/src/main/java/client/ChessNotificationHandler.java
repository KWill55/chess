package client;

import client.websocket.NotificationHandler;
import websocket.messages.NotificationMessage;

public class ChessNotificationHandler implements NotificationHandler {
    @Override
    public void notify(NotificationMessage notification) {
        System.out.println("NOTIFICATION: " + notification.getMessage());
        System.out.print(">>> ");
    }
}