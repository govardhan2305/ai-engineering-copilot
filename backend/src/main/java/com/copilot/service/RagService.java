package com.copilot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RagService(
            VectorStore vectorStore,
            ChatClient.Builder chatClientBuilder,
            SyncMcpToolCallbackProvider mcpToolCallbackProvider) {

        this.vectorStore = vectorStore;

        this.chatClient = chatClientBuilder
                .defaultTools(mcpToolCallbackProvider)
                .build();
    }

    public List<Document> search(String query) {

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(5)
                .similarityThreshold(0.3)
                .build();

        return vectorStore.similaritySearch(request);
    }

    public String ask(String question) {

        List<Document> documents = search(question);

        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        String prompt = """
                You are an AI Engineering Copilot.

                Answer questions about the project using the indexed context
                and MCP tools.

                MCP TOOL RULES:

                1. search_code(query)
                   - Use this to search source code.
                   - The argument is named "query".

                2. get_file(path)
                   - Use this to read the contents of a specific file.
                   - The argument is named "path".
                   - NEVER pass "query" to get_file.

                3. list_project_files()
                   - Use this to discover available files.

                4. search_api(query)
                   - Use this to search for API-related code.
                   - The argument is named "query".

                When you need information from a specific source file:
                first use search_code(query) to find the file,
                then use get_file(path) to inspect its contents.

                Do not invent source code, line numbers, methods, or behavior.
                Base source-code claims on the actual tool results.

                Indexed project context:
                %s

                User question:
                %s
                """.formatted(context, question);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}