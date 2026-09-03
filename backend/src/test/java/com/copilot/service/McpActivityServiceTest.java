package com.copilot.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class McpActivityServiceTest {

    @Test
    void shouldRecordToolActivity() {

        McpActivityService service = new McpActivityService();

        service.record("search_code");
        service.record("get_file");

        assertEquals(
                List.of("search_code", "get_file"),
                service.getActivities());
    }

    @Test
    void shouldReturnEmptyActivitiesInitially() {

        McpActivityService service = new McpActivityService();

        assertTrue(service.getActivities().isEmpty());
    }

    @Test
    void shouldClearActivities() {

        McpActivityService service = new McpActivityService();

        service.record("search_code");
        service.record("get_file");

        service.clear();

        assertTrue(service.getActivities().isEmpty());
    }
}