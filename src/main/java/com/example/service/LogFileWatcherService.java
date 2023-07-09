package com.example.service;

import com.example.entities.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class LogFileWatcherService {

    private final Queue<Log> tailLogs; //Max size 1000
    private final File logFileToWatch;
    private final ExecutorService executorService;
    private final NotificationService notificationService;
    private final RandomAccessFile randomAccessFile;
    private final WatchService watchService;
    private final Path logFileDirectory;
    private long filePosition;

    @Autowired
    public LogFileWatcherService(NotificationService notificationService) throws IOException {
        this.notificationService = notificationService;
        this.logFileToWatch = new File("/Users/vaibhavsrivastava/workspace/Misc/LogWatcherService/src/main/resources/application-logs.log");
        this.randomAccessFile = new RandomAccessFile(this.logFileToWatch, "r");
        this.watchService = FileSystems.getDefault().newWatchService();
        this.tailLogs = new LinkedBlockingDeque<>();
        this.executorService = Executors.newFixedThreadPool(1);
        this.logFileDirectory = Paths.get(this.logFileToWatch.getParent());
        this.logFileDirectory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        filePosition = 0L;
        executorService.execute(this::startFileWatchAsync);
    }

    public CompletableFuture<Void> startFileWatchAsync() {

        this.readFileContent(this.logFileDirectory.resolve(this.logFileToWatch.getPath()));
        while (true) {
            try {
                WatchKey watchKey = watchService.take();
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    Path eventPath = (Path) event.context();
                    if (eventPath.toString().equals(this.logFileToWatch.getName())) {
                        this.readFileContent(this.logFileDirectory.resolve(eventPath));
                    }
                }
                watchKey.reset();
            } catch (Exception exception) {
                System.out.println("ERROR: Exception occurred while reading watch event. " + exception.getMessage());
            }
        }
    }

    private void readFileContent(Path path) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r");
//            if (this.filePosition > randomAccessFile.length()) {
//                this.filePosition = 0L;
//            }
            randomAccessFile.seek(filePosition);
            String logLine;
            while ((logLine = randomAccessFile.readLine()) != null) {
                Log log = new Log(logLine);
                this.appendLog(log);
                //Broadcast notification...
                this.notificationService.sendNotification(log);
            }
            this.filePosition = randomAccessFile.getFilePointer();
        } catch (Exception exception) {
            System.out.println("ERROR: Exception occurred while reading file. " + exception.getMessage());
        }
    }

    private void appendLog(Log log) {

        this.tailLogs.add(log);
        if (this.tailLogs.size() > 1000) {
            this.tailLogs.poll();
        }
    }

    public List<Log> readLogLines() {

        return new ArrayList<>(this.tailLogs);
    }
}
