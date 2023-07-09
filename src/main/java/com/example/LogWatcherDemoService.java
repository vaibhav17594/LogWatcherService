package com.example;

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

    @Autowired
    public LogWatcherDemoService() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.executorService.execute(this::updateFileAsync);
    }

    private void updateFileAsync() {
        try {
            String file = "/Users/vaibhavsrivastava/workspace/Misc/LogWatcherService/src/main/resources/application-logs.log";
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            IntStream.range(11, 1000).forEach(count -> {
                StringBuilder logContent = new StringBuilder();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (count % 3 == 0) {
                    logContent.append("INFO: status logs /app/1.0/status ").append(count);
                } else if (count % 3 == 1) {
                    logContent.append("WARN: status logs /error ").append(count);
                } else {
                    logContent.append("ERROR: status logs /exception ").append(count);
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
