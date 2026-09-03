package com.copilot.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @Test
    void shouldSplitDocumentAndStoreChunks() throws Exception {

        VectorStore vectorStore = mock(VectorStore.class);

        DocumentService service = new DocumentService(vectorStore);

        String content = """
                # Authentication

                The application uses JWT-based authentication.

                JwtFilter validates incoming bearer tokens.

                SecurityConfig configures the authentication flow.
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "authentication.md",
                "text/markdown",
                content.getBytes(StandardCharsets.UTF_8));

        int chunks = service.ingest(file);

        assertEquals(1, chunks);

        verify(vectorStore, times(1))
                .add(anyList());
    }
}