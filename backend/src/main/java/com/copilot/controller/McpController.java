package com.copilot.controller;

import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mcp")
public class McpController {

    private final SyncMcpToolCallbackProvider toolCallbackProvider;

    public McpController(SyncMcpToolCallbackProvider toolCallbackProvider) {
        this.toolCallbackProvider = toolCallbackProvider;
    }

    @GetMapping("/tools")
    public List<Map<String, String>> tools() {

        return Arrays.stream(toolCallbackProvider.getToolCallbacks())
                .map(callback -> Map.of(
                        "name", callback.getToolDefinition().name(),
                        "description", callback.getToolDefinition().description(),
                        "inputSchema", callback.getToolDefinition().inputSchema()))
                .toList();
    }
}