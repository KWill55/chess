package client;

import webSocketMessages.Notification;

public class ChessNotificationHandler implements NotificationHandler {

    @Override
    public void notify(Notification notification) {
        System.out.println("\n🔔 " + notification.message());
        System.out.print(">>> ");
    }
}

