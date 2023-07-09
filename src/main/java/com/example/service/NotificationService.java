package com.example.service;

import com.example.entities.Log;
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

    public CompletableFuture<Void> sendNotification(Log log) {
        messagingTemplate.convertAndSend("/logs/log", log);
        return CompletableFuture.allOf();
    }
}
