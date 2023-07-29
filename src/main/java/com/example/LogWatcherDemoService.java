package com.example;

import com.example.service.LogFileWatcherOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
public class LogWatcherDemoService {

    private final ExecutorService executorService;
    private final LogFileWatcherOrchestrator logFileWatcherOrchestrator;

    @Autowired
    public LogWatcherDemoService(LogFileWatcherOrchestrator logFileWatcherOrchestrator) throws IOException {
        this.logFileWatcherOrchestrator = logFileWatcherOrchestrator;

        String fileNameAppA = "/Users/vaibhavsrivastava/workspace/Misc/LogWatcherService/src/main/resources/application-logs-app-a.log";
        String fileNameAppB = "/Users/vaibhavsrivastava/workspace/Misc/LogWatcherService/src/main/resources/application-logs-app-b.log";

        this.logFileWatcherOrchestrator.createLogFileWatcher(fileNameAppA);
        this.logFileWatcherOrchestrator.createLogFileWatcher(fileNameAppB);

        this.executorService = Executors.newFixedThreadPool(2);
        this.executorService.execute(() -> updateFileAsync(fileNameAppA, "APP - A"));
        this.executorService.execute(() -> updateFileAsync(fileNameAppB, "APP - B"));
    }

    private void updateFileAsync(String file, String appName) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            IntStream.range(11, 1000).forEach(count -> {
                StringBuilder logContent = new StringBuilder();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (count % 3 == 0) {
                    logContent.append(" INFO: status logs /app/1.0/status - " + appName + " ").append(count);
                } else if (count % 3 == 1) {
                    logContent.append("WARN: status logs /error - " + appName + " ").append(count);
                } else {
                    logContent.append("ERROR: status logs /exception - " + appName + " ").append(count);
                }

                try {
                    Files.write(Paths.get(file), logContent.toString().getBytes(), StandardOpenOption.APPEND);
                    Files.write(Paths.get(file), System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception exception) {
            System.out.println("ERROR: Exception occurred while generating demo logs. " + exception.getMessage());
        }
    }
}
