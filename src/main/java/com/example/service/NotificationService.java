package com.example.service;

import com.example.entities.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public CompletableFuture<Void> sendNotification(int counter) {
        String message = "Counter value: " + counter;
        System.out.println("Vaibhav inside message: " + message);
        messagingTemplate.convertAndSend("/topic/greetings", new Greeting(message));
        return CompletableFuture.allOf();
    }
}
