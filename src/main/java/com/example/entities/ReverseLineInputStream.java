package com.example.entities;

import java.io.*;

public class ReverseLineInputStream extends InputStream {

    private final RandomAccessFile file;
    private long currentLineStart = -1;
    private long currentLineEnd = -1;
    private long currentPos = -1;
    private long lastPosInFile = -1;

    public ReverseLineInputStream(RandomAccessFile file) throws IOException {
        this.file = file;
        currentPos = file.length();
        currentLineStart = currentPos;
        currentLineEnd = currentPos;
        lastPosInFile = currentPos;
    }

    @Override
    public int read() throws IOException {
        if (currentPos < 0) {
            return -1;
        }
        int nextByte = file.read();
        currentPos--;
        return nextByte;
    }

    public String readLine() throws IOException {
        StringBuilder line = new StringBuilder();
        int nextByte;
        boolean foundLineEnd = false;

        while (!foundLineEnd) {
            if (currentPos < 0) {
                break;
            }
            nextByte = read();
            if (nextByte == '\n') {
                foundLineEnd = true;
                break;
            }
            if (nextByte == '\r') {
                foundLineEnd = true;
                if (read() != '\n') {
                    currentPos++;
                }
                break;
            }
            line.insert(0, (char) nextByte);
        }

        return line.toString();
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
