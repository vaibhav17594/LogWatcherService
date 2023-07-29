package com.example.controller;

import com.example.service.LogFileWatcherOrchestrator;
import com.example.service.NotificationService;
import com.example.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import com.example.entities.Log;
import com.example.entities.Request;

import java.util.List;

@Controller
public class LogWebSocketController {

    @Autowired
    private LogFileWatcherOrchestrator logFileWatcherOrchestrator;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SubscriptionService subscriptionService;

    @MessageMapping("/subscribe")
    public void watch(SimpMessageHeaderAccessor sha, Request request) throws Exception {

        System.out.println("Username: " + sha.getUser().getName());
        System.out.println("Request.fileName: " + request.getFileName());
        List<Log> listLogs = logFileWatcherOrchestrator.getLogFileWatcher(request.getFileName()).readLogLines();
        notificationService.sendNotificationToUser(sha.getUser().getName(), listLogs);
        subscriptionService.subscribe(sha.getUser().getName(), request.getFileName());
    }
}
