package com.copilot.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class McpActivityService {

    private final List<String> activities = Collections.synchronizedList(new ArrayList<>());

    public void record(String toolName) {
        activities.add(toolName);
    }

    public List<String> getActivities() {
        synchronized (activities) {
            return List.copyOf(activities);
        }
    }

    public void clear() {
        activities.clear();
    }
}