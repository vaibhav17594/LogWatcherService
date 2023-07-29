package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SubscriptionService {

    private final Map<String, HashSet<String>> subscriptions;

    @Autowired
    public SubscriptionService() {
        this.subscriptions = new ConcurrentHashMap<>();
    }

    public boolean subscribe(String username, String logfile) {
        HashSet<String> usersOfLogfile = subscriptions.getOrDefault(logfile, new HashSet<String>());
        usersOfLogfile.add(username);
        this.subscriptions.put(logfile, usersOfLogfile);
        return true;
    }

    public boolean unsubscribe(String username, String logfile) {
        HashSet<String> usersOfLogfile = subscriptions.getOrDefault(logfile, new HashSet<String>());
        usersOfLogfile.remove(username);
        this.subscriptions.put(logfile, usersOfLogfile);
        return true;
    }

    public Set<String> usersSubscribedTo(String logfile) {
        return this.subscriptions.getOrDefault(logfile, new HashSet<>());
    }
}
