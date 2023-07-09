package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
public class CountService {

    private final NotificationService  notificationService;
    private final ExecutorService executorService;

    @Autowired
    public CountService(NotificationService notificationService) {
        this.notificationService = notificationService;
        this.executorService = Executors.newSingleThreadExecutor();
        this.runAsync();
    }

    private CompletableFuture<Void> runAsync() {

        Runnable runnableTask = () -> IntStream.range(1, 1000).forEach(counter -> {
            //notificationService.sendNotification(counter);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                //
            }
        });
        executorService.execute(runnableTask);

        return CompletableFuture.allOf();
    }
}
