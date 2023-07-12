package com.example.controller;

import com.example.service.LogFileWatcherService;
import com.example.service.NotificationService;
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
    private LogFileWatcherService logFileWatcher;

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/subscribe")
    public void watch(SimpMessageHeaderAccessor sha, Request request) throws Exception {

        List<Log> listLogs = logFileWatcher.readLogLines();
        System.out.println("Username: " + sha.getUser().getName());
        System.out.println("Request.fileName: " + request.getFileName());
        notificationService.sendNotificationToUser(sha.getUser().getName(), listLogs);
        return;
    }
}
