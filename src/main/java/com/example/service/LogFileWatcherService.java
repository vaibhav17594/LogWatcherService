package com.example.service;

import com.example.entities.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

@Service
public class LogFileWatcherService {

    private Queue<Log> tailLogs; //Max size 10
    private String fileToWatch;
    private ExecutorService executorService;
    private NotificationService notificationService;

    @Autowired
    public LogFileWatcherService(NotificationService notificationService) {
        this.notificationService = notificationService;
        this.tailLogs = new LinkedBlockingDeque<>();
        this.executorService = Executors.newFixedThreadPool(1);
        this.startFileWatchAsync();
    }

    public CompletableFuture<Void> startFileWatchAsync() {

        Runnable runnable = () -> IntStream.range(1, 1000).forEach(num -> {
            Log log = new Log("Executed counter: " + num);
            this.notificationService.sendNotification(log);
            this.addTailLog(log);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        this.executorService.execute(runnable);

        return CompletableFuture.allOf();
    }

    private void addTailLog(Log log) {

        this.tailLogs.add(log);
        if (this.tailLogs.size() > 10) {
            this.tailLogs.poll();
        }
    }

    public List<Log> readLogLines() {

        return new ArrayList<>(this.tailLogs);
    }
}
