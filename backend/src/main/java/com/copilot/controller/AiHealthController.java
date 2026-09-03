package com.copilot.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiHealthController {

    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public AiHealthController(
            EmbeddingModel embeddingModel,
            VectorStore vectorStore) {
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/api/ai/health")
    public String health() {
        return "EmbeddingModel: "
                + embeddingModel.getClass().getSimpleName()
                + ", VectorStore: "
                + vectorStore.getClass().getSimpleName();
    }
}