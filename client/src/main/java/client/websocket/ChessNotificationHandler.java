package client.websocket;

import websocketmessages.Notification;

public class ChessNotificationHandler implements NotificationHandler {
    public void notify(Notification notification) {
        // Placeholder for now
        System.out.println("[NOTIFICATION] " + notification.message());
    }
}


