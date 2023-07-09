package com.example.controller;

import com.example.service.LogFileWatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import com.example.entities.Log;
import com.example.entities.Request;

import java.util.List;

@Controller
public class LogWebSocketController {

    @Autowired
    private LogFileWatcherService logFileWatcher;

    @MessageMapping("/subscribe")
    @SendTo("/logs/log")
    public List<Log> watch(Request request) throws Exception {
        return logFileWatcher.readLogLines();
    }
}
