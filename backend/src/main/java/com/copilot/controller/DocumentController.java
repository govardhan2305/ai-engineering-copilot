package com.copilot.controller;

import com.copilot.model.DocumentUploadResponse;
import com.copilot.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentUploadResponse> upload(
            @RequestParam("file") MultipartFile file) {

        try {

            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new DocumentUploadResponse(
                                file.getOriginalFilename(),
                                0,
                                "File is empty"));
            }

            int chunks = documentService.ingest(file);

            return ResponseEntity.ok(
                    new DocumentUploadResponse(
                            file.getOriginalFilename(),
                            chunks,
                            "Document successfully indexed"));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(new DocumentUploadResponse(
                            file.getOriginalFilename(),
                            0,
                            "Failed to process document: " + e.getMessage()));
        }
    }
}