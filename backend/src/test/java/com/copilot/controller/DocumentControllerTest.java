package com.copilot.controller;

import com.copilot.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocumentControllerTest {

    @Test
    void shouldUploadDocumentSuccessfully() throws Exception {

        DocumentService documentService = mock(DocumentService.class);

        DocumentController controller = new DocumentController(documentService);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "authentication.md",
                "text/markdown",
                "# Authentication\nJWT authentication".getBytes());

        when(documentService.ingest(file))
                .thenReturn(1);

        var response = controller.upload(file);

        assertEquals(200, response.getStatusCode().value());

        var body = response.getBody();

        assertEquals("authentication.md", body.fileName());
        assertEquals(1, body.chunksCreated());
        assertEquals(
                "Document successfully indexed",
                body.message());
    }
}