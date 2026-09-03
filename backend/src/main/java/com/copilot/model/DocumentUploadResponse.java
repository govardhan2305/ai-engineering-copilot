package com.copilot.model;

public record DocumentUploadResponse(
        String fileName,
        int chunksCreated,
        String message) {
}