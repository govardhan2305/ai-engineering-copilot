package com.copilot.controller;

import com.copilot.model.RagQueryRequest;
import com.copilot.service.RagService;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/search")
    public List<Document> search(@RequestParam String query) {
        return ragService.search(query);
    }

    @PostMapping("/ask")
    public String ask(@RequestBody RagQueryRequest request) {
        return ragService.ask(request.question());
    }
}