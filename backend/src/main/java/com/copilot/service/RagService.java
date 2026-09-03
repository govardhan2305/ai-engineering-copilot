package com.copilot.service;

import com.copilot.model.RagQueryResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

        private final VectorStore vectorStore;
        private final ChatClient chatClient;
        private final McpProjectService mcpProjectService;
        private final McpActivityService activityService;

        public RagService(
                        VectorStore vectorStore,
                        ChatClient.Builder chatClientBuilder,
                        McpProjectService mcpProjectService,
                        McpActivityService activityService) {

                this.vectorStore = vectorStore;
                this.mcpProjectService = mcpProjectService;
                this.activityService = activityService;

                /*
                 * MCP tools are now executed explicitly by Spring Boot.
                 *
                 * We intentionally do NOT register MCP tools with Ollama.
                 * This prevents the small local model from incorrectly chaining
                 * search_code() and get_file().
                 */
                this.chatClient = chatClientBuilder.build();
        }

        public List<Document> search(String query) {

                SearchRequest request = SearchRequest.builder()
                                .query(query)
                                .topK(5)
                                .similarityThreshold(0.3)
                                .build();

                return vectorStore.similaritySearch(request);
        }

        public RagQueryResponse ask(String question) {

                activityService.clear();

                /*
                 * ---------------------------------------------------------
                 * 1. RAG SEARCH
                 * ---------------------------------------------------------
                 */

                List<Document> documents = search(question);

                List<String> sources = documents.stream()
                                .map(document -> document.getMetadata().get("fileName"))
                                .filter(fileName -> fileName != null)
                                .map(Object::toString)
                                .distinct()
                                .toList();

                String context = documents.stream()
                                .map(Document::getText)
                                .collect(Collectors.joining("\n\n---\n\n"));

                /*
                 * ---------------------------------------------------------
                 * 2. DETERMINE WHETHER SOURCE CODE IS NEEDED
                 * ---------------------------------------------------------
                 */

                String sourceContext = "";

                String className = extractClassName(question);

                if (className != null) {

                        sourceContext = mcpProjectService.inspectClass(className);

                        activityService.record("search_code");
                        activityService.record("get_file");
                }

                /*
                 * ---------------------------------------------------------
                 * 3. BUILD GROUNDED PROMPT
                 * ---------------------------------------------------------
                 */

                String prompt = """
                                You are an AI Engineering Copilot.

                                Answer the user's question using ONLY the evidence
                                provided below.

                                =========================
                                INDEXED PROJECT DOCUMENTS
                                =========================

                                %s

                                =========================
                                ACTUAL SOURCE CODE
                                =========================

                                %s

                                =========================
                                STRICT RULES
                                =========================

                                1. Never invent project-specific information.

                                2. Never invent:
                                - classes
                                - files
                                - methods
                                - APIs
                                - libraries
                                - configuration
                                - authentication components
                                - implementation details
                                - line numbers

                                3. If actual source code is provided, treat it as the
                                authoritative source for implementation questions.

                                4. Describe the implementation literally.

                                5. Do not infer behavior that is not explicitly implemented.

                                6. Do not describe a token as cryptographically valid merely
                                because the code checks its prefix.

                                7. If a method only checks a condition, describe that condition.
                                Do not expand it into a broader security guarantee.

                                8. Never confuse documentation claims with actual implementation.

                                9. If the source code does not demonstrate a behavior,
                                do not claim that the behavior exists.

                                10. If the requested information is not available in the
                                provided project evidence, say exactly:

                                "That information is not available in the indexed project."
                                =========================
                                USER QUESTION
                                =========================

                                %s
                                """.formatted(
                                context,
                                sourceContext,
                                question);

                /*
                 * ---------------------------------------------------------
                 * 4. GENERATE ANSWER
                 * ---------------------------------------------------------
                 */

                String answer = chatClient.prompt()
                                .user(prompt)
                                .call()
                                .content();

                /*
                 * ---------------------------------------------------------
                 * 5. RETURN ANSWER + EVIDENCE
                 * ---------------------------------------------------------
                 */

                return new RagQueryResponse(
                                answer,
                                sources,
                                activityService.getActivities());
        }

        /**
         * Extract a Java class name from the user's question.
         *
         * Examples:
         *
         * "How does JwtFilter work?"
         * -> JwtFilter
         *
         * "Explain SecurityConfig"
         * -> SecurityConfig
         *
         * "What does UserController do?"
         * -> UserController
         */
        private String extractClassName(String question) {

                String[] knownClasses = {
                                "JwtFilter",
                                "SecurityConfig",
                                "UserController",
                                "UserService",
                                "AuthService"
                };

                for (String className : knownClasses) {

                        if (question.toLowerCase()
                                        .contains(className.toLowerCase())) {

                                return className;
                        }
                }

                return null;
        }
}