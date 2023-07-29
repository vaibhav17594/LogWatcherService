package com.example.service;

import com.example.entities.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final SubscriptionService subscriptionService;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate, SubscriptionService subscriptionService) {

        this.messagingTemplate = messagingTemplate;
        this.subscriptionService = subscriptionService;
    }

    public CompletableFuture<Void> sendNotification(Log log) {
        messagingTemplate.convertAndSend("/topic/logs", log);
        return CompletableFuture.allOf();
    }

    public CompletableFuture<Void> sendNotificationToUser(String userName, Log log) {
        messagingTemplate.convertAndSendToUser(userName, "/queue/logs", log);
        return CompletableFuture.allOf();
    }

    public CompletableFuture<Void> sendNotificationToUser(String userName, List<Log> logs) {
        messagingTemplate.convertAndSendToUser(userName, "/queue/logs", logs);
        return CompletableFuture.allOf();
    }

    public CompletableFuture<Void> sendNotificationToLogfileSubscribedUser(String logfile, List<Log> logs) {

        System.out.println("Inside : sendNotificationToLogfileSubscribedUser for logfile: " + logfile);
        System.out.println("subscriptionService.usersSubscribedTo: " + subscriptionService.usersSubscribedTo(logfile).toString());
        subscriptionService.usersSubscribedTo(logfile)
                        .forEach(user -> {
                            System.out.println("Sending notification to user: " + user + ", logs: " + logs);
                            messagingTemplate.convertAndSendToUser(user, "/queue/logs", logs);
                        });

        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> sendNotificationToUser(String userName, String endpoint, List<Log> logs) {
        messagingTemplate.convertAndSendToUser(userName, endpoint, logs);
        return CompletableFuture.allOf();
    }
}
