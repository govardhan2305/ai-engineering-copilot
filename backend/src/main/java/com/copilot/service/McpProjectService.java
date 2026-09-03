package com.copilot.service;

import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class McpProjectService {

    private final ToolCallback[] toolCallbacks;

    public McpProjectService(
            SyncMcpToolCallbackProvider mcpToolCallbackProvider) {

        this.toolCallbacks = mcpToolCallbackProvider.getToolCallbacks();
    }

    public String searchCode(String query) {

        return callTool(
                "search_code",
                "{\"query\":\"" + escapeJson(query) + "\"}");
    }

    public String getFile(String path) {

        return callTool(
                "get_file",
                "{\"path\":\"" + escapeJson(path) + "\"}");
    }

    public String inspectClass(String className) {

        String searchResult = searchCode(className);

        if (searchResult == null || searchResult.isBlank()) {
            return "No source code found for: " + className;
        }

        // Spring AI MCP may return the tool result wrapped
        // inside a JSON text field with escaped characters.
        String normalizedResult = searchResult
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");

        Pattern pattern = Pattern.compile(
                "\"file\"\\s*:\\s*\"([^\"]+\\.java)\"");

        Matcher matcher = pattern.matcher(normalizedResult);

        while (matcher.find()) {

            String filePath = matcher.group(1)
                    .replace("\\", "/");

            if (filePath.endsWith(className + ".java")) {
                return getFile(filePath);
            }
        }

        return searchResult;
    }

    private String callTool(String toolName, String input) {

        ToolCallback callback = Arrays.stream(toolCallbacks)
                .filter(tool -> tool.getToolDefinition()
                        .name()
                        .equals(toolName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "MCP tool not found: " + toolName));

        return callback.call(input);
    }

    private String escapeJson(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}