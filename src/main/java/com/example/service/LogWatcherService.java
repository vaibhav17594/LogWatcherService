package com.example.service;

import com.example.entities.ReverseLineInputStream;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;

public class LogWatcherService implements Runnable {

    private final File logFile;
    private final RandomAccessFile randomAccessFile;
    private final WatchService watchService;

    public LogWatcherService(String logFilePath) throws IOException {
        logFile = new File(logFilePath);
        randomAccessFile = new RandomAccessFile(logFile, "r");
        watchService = FileSystems.getDefault().newWatchService();
        Path logDirectory = Paths.get(logFile.getParent());
        logDirectory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public String readLastTenLines() throws IOException {
        StringBuilder lastTenLines = new StringBuilder();
        try (ReverseLineInputStream inputStream = new ReverseLineInputStream(randomAccessFile)) {
            for (int i = 0; i < 10; i++) {
                String line = inputStream.readLine();
                if (line == null) {
                    break;
                }
                lastTenLines.insert(0, line).insert(0, "\n");
            }
        }
        return lastTenLines.toString();
    }

    @Override
    public void run() {
        try {
            WatchKey watchKey;
            while ((watchKey = watchService.take()) != null) {
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        String updatedLines = readUpdatedLines();
                        if (!updatedLines.isEmpty()) {
                            //LogWebSocketController.broadcastLogUpdate(updatedLines);
                        }
                    }
                }
                watchKey.reset();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private String readUpdatedLines() throws IOException {
        StringBuilder updatedLines = new StringBuilder();
        try (ReverseLineInputStream inputStream = new ReverseLineInputStream(randomAccessFile)) {
            String line;
            while ((line = inputStream.readLine()) != null) {
                updatedLines.insert(0, line).insert(0, "\n");
            }
        }
        return updatedLines.toString();
    }
}
