package com.copilot.controller;

import com.copilot.model.RagQueryRequest;
import com.copilot.model.RagQueryResponse;
import com.copilot.service.RagService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RagControllerTest {

    @Test
    void shouldReturnRagAnswer() {

        RagService ragService = mock(RagService.class);

        RagController controller = new RagController(ragService);

        RagQueryResponse expected = new RagQueryResponse(
                "JWT authentication is used.",
                List.of("authentication.md"),
                List.of("search_code", "get_file"));

        when(ragService.ask("How does authentication work?"))
                .thenReturn(expected);

        RagQueryRequest request = new RagQueryRequest("How does authentication work?");

        RagQueryResponse response = controller.ask(request);

        assertEquals(
                "JWT authentication is used.",
                response.answer());

        assertEquals(
                List.of("authentication.md"),
                response.sources());

        assertEquals(
                List.of("search_code", "get_file"),
                response.tools());
    }
}