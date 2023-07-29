package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class LogFileWatcherOrchestrator {

    private Map<String, LogFileWatcher> fileWatcherMap;
    private final NotificationService notificationService;

    @Autowired
    public LogFileWatcherOrchestrator(NotificationService notificationService) {
        this.fileWatcherMap = new HashMap<>();
        this.notificationService = notificationService;
    }

    public LogFileWatcher createLogFileWatcher(String logfileName) throws IOException {
        LogFileWatcher logFileWatcher = new LogFileWatcher(notificationService, logfileName);
        return this.fileWatcherMap.put(logfileName, logFileWatcher);
    }

    public LogFileWatcher deleteLogFileWatcher(String logfileName) throws IOException {
        LogFileWatcher logFileWatcher = fileWatcherMap.get(logfileName);
        if (logFileWatcher != null) {
            logFileWatcher.stop();
        }
        return this.fileWatcherMap.remove(logfileName);
    }

    public LogFileWatcher getLogFileWatcher(String logfileName) {
        return fileWatcherMap.get(logfileName);
    }
}
