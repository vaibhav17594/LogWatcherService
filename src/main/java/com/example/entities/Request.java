package com.example.entities;

public class Request {

    private String fileName;

    public Request() {
    }

    public Request(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}