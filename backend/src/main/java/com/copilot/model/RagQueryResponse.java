package com.copilot.model;

import java.util.List;

public record RagQueryResponse(
                String answer,
                List<String> sources,
                List<String> tools) {
}