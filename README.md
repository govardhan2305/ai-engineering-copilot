# AI Engineering Copilot

An AI-powered engineering assistant that helps developers understand
project documentation and source code using **Retrieval-Augmented
Generation (RAG)** and **Model Context Protocol (MCP)**.

The application combines a Vue.js frontend, Spring Boot backend, Qdrant
vector database, Python MCP server, and a locally running Ollama LLM to
provide project-aware answers without requiring paid AI APIs.

## Features

- Upload project documentation and index it into a vector database
- Semantic search using Retrieval-Augmented Generation (RAG)
- Local LLM inference using Ollama
- Project-aware source-code inspection using MCP
- MCP tools for:
  - Listing project files
  - Searching source code
  - Reading project files
  - Searching API-related definitions
- Display knowledge sources used to generate an answer
- Display MCP tools executed during analysis
- Vue.js web interface
- Spring Boot REST backend
- Python-based MCP server
- Docker Compose deployment
- Unit and controller tests

## Architecture

``` mermaid
flowchart TD
    UI["Vue.js UI<br/>Port 5173"]
    API["Spring Boot Backend<br/>Port 8080"]
    RAG["RAG Pipeline"]
    Q["Qdrant<br/>Vector DB"]
    MCP["Python MCP Server<br/>Port 8000"]
    CODE["Project Source Code"]
    LLM["Ollama<br/>Local LLM"]

    UI -->|"REST API"| API
    API --> RAG
    RAG -->|"Semantic Search"| Q
    API -->|"MCP / Streamable HTTP"| MCP
    MCP --> CODE
    RAG -->|"Retrieved Context"| LLM
    MCP -->|"Source Code Context"| LLM
    API --> LLM
```

## How It Works

### 1. Document Ingestion

A user uploads project documentation through the Vue.js interface.

``` mermaid
flowchart TD
    D["Uploaded Document"]
    S["Spring Boot API"]
    T["Text Extraction"]
    C["Token-based Chunking"]
    E["Ollama Embeddings"]
    Q["Qdrant"]

    D --> S
    S --> T
    T --> C
    C --> E
    E --> Q
```

The document is split into smaller chunks and stored as vector
embeddings in Qdrant.

### 2. RAG-based Question Answering

When a user asks a question, the backend performs semantic similarity
search against the indexed project knowledge.

``` mermaid
flowchart TD
    U["User Question"]
    S["Spring Boot"]
    Q["Semantic Search"]
    V["Qdrant"]
    R["Relevant Documents"]
    C["Context Construction"]
    L["Ollama"]
    A["Generated Answer"]

    U --> S
    S --> Q
    Q --> V
    V --> R
    R --> C
    C --> L
    L --> A
```

The retrieved project context is provided to the local LLM to generate a
grounded response.

### 3. MCP-based Source Code Investigation

For implementation-oriented questions, the backend can use MCP to
inspect the actual project source code.

``` mermaid
flowchart TD
    U["User Question"]
    S["Spring Boot"]
    R["Qdrant<br/>RAG Context"]
    M["MCP Server"]
    SC["search_code()"]
    GF["get_file()"]
    C["Source Code"]
    L["Ollama"]
    A["Grounded Answer"]

    U --> S
    S --> R
    S --> M
    M --> SC
    M --> GF
    SC --> C
    GF --> C
    R --> L
    C --> L
    L --> A
```

The retrieved source code is combined with the RAG context before the
final answer is generated.

## RAG and MCP

RAG and MCP serve different purposes in the application.

### RAG

**Retrieval-Augmented Generation** provides semantic retrieval from
indexed project knowledge.

It is useful for questions such as:

> What authentication approach does this project use?

The system searches the indexed documentation and retrieves relevant
information before generating the answer.

### MCP

**Model Context Protocol** provides structured access to project tools
and resources.

It is useful for questions such as:

> Explain how JwtFilter validates a token.

The application can use MCP tools to search and retrieve the actual
source code.

### Combined Approach

``` mermaid
flowchart LR
    Q["Project Question"]
    R["RAG"]
    M["MCP"]
    K["Indexed Project Knowledge"]
    T["Project Tools / Source Code"]
    C["Grounded Context"]
    L["Ollama"]
    A["Answer"]

    Q --> R
    Q --> M
    R --> K
    M --> T
    K --> C
    T --> C
    C --> L
    L --> A
```

RAG provides **semantic knowledge retrieval**, while MCP provides
**controlled project interaction and source-code access**.

## MCP Tools

The Python MCP server exposes project inspection tools.

| Tool                   | Purpose                                            |
|------------------------|----------------------------------------------------|
| `list_project_files()` | Lists files available in the project               |
| `search_code(query)`   | Searches project source code                       |
| `get_file(path)`       | Retrieves the contents of a project file           |
| `search_api(query)`    | Searches project files for API-related definitions |

Spring Boot connects to the MCP server using **Streamable HTTP**.

## Technology Stack

### Frontend

- Vue.js
- JavaScript
- HTML
- CSS

### Backend

- Java 21
- Spring Boot 4
- Spring AI
- REST APIs

### AI and RAG

- Ollama
- Qdrant
- Vector embeddings
- Retrieval-Augmented Generation

### MCP

- Python 3.11
- FastMCP
- Streamable HTTP

### Testing

- JUnit
- Mockito
- Spring Boot Test

### DevOps

- Docker
- Docker Compose
- Nginx

## Project Structure

``` text
ai-engineering-copilot/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── mcp-server/
│   ├── server.py
│   ├── requirements.txt
│   └── Dockerfile
│
├── sample-project/
│   ├── src/
│   ├── authentication.md
│   └── README.md
│
├── docker-compose.yml
└── README.md
```

## Prerequisites

The following software is required:

- Java 21
- Maven
- Node.js
- Python 3.11+
- Docker Desktop
- Ollama

## Ollama Setup

Ollama is used to run the LLM and embedding model locally.

The project uses:

- `qwen2.5:1.5b` for chat generation
- `nomic-embed-text` for embeddings

Verify the installed models:

``` bash
ollama list
```

Make sure Ollama is running before starting the application.

## Running the Application

### Option 1: Docker Compose

The recommended way to run the complete application is:

``` bash
docker compose up --build
```

The application consists of:

| Service         | Address                    |
|-----------------|----------------------------|
| Frontend        | `http://localhost:5173`    |
| Backend         | `http://localhost:8080`    |
| Qdrant REST API | `http://localhost:6333`    |
| Qdrant gRPC     | `localhost:6334`           |
| MCP Server      | `http://localhost:8000`    |
| Ollama          | Host machine, port `11434` |

Stop the application:

``` bash
docker compose down
```

### Option 2: Run Frontend Locally

Navigate to the frontend directory:

``` bash
cd frontend
```

Install dependencies:

``` bash
npm install
```

Start the development server:

``` bash
npm run dev
```

The frontend will be available at:

``` text
http://localhost:5173
```

### Option 3: Run Backend Locally

Navigate to the backend:

``` bash
cd backend
```

Run the application:

``` bash
mvn spring-boot:run
```

The backend will be available at:

``` text
http://localhost:8080
```

When running the backend locally, Qdrant and the MCP server must also be
available according to the application's configuration.

## API Endpoints

### Upload Document

``` text
POST /api/documents/upload
```

Uploads a document and indexes its content into Qdrant.

### Ask Question

``` text
POST /api/rag/ask
```

Accepts a project-related question and returns:

- Generated answer
- Knowledge sources
- MCP activity

### Health Check

``` text
GET /actuator/health
```

Used to verify the Spring Boot application health.

## Example Questions

### Documentation / RAG

``` text
What authentication approach does this project use?
```

### Source Code + MCP

``` text
Explain how JwtFilter validates a token.
```

### Project Understanding

``` text
What components are involved in authentication?
```

### API Exploration

``` text
What APIs are available in this project?
```

## Example MCP Flow

For a source-code question such as:

``` text
Explain how JwtFilter validates a token.
```

the request flows through the system as follows:

``` mermaid
flowchart TD
    U["User"]
    UI["Vue.js"]
    S["Spring Boot"]
    Q["Qdrant<br/>Relevant Project Knowledge"]
    M["MCP Server"]
    SC["search_code()"]
    GF["get_file()"]
    J["JwtFilter.java"]
    L["Ollama"]
    A["Answer"]

    U -->|"Question"| UI
    UI --> S
    S --> Q
    S --> M
    M --> SC
    M --> GF
    SC --> J
    GF --> J
    Q --> L
    J --> L
    L --> A
```

The frontend displays the MCP activity and knowledge sources associated
with the response.

## Document Ingestion Flow

``` mermaid
flowchart TD
    D["Uploaded Document"]
    API["Spring Boot API"]
    T["Text Extraction"]
    C["TokenTextSplitter"]
    CH["Document Chunks"]
    E["Ollama Embeddings"]
    Q["Qdrant"]

    D --> API
    API --> T
    T --> C
    C --> CH
    CH --> E
    E --> Q
```

The indexed documents become searchable through semantic similarity.

## Testing

The backend uses JUnit, Mockito, and Spring Boot Test.

Run the complete test suite:

``` bash
cd backend
mvn clean test
```

The project includes tests covering:

- MCP activity tracking
- Document ingestion
- Document upload controller
- RAG controller

The test suite validates the core backend functionality independently
from the running frontend.

## Docker Architecture

Docker Compose manages the application services.

``` mermaid
flowchart TD
    D["Docker Compose"]

    F["Frontend<br/>Nginx :5173"]
    B["Backend<br/>Spring Boot :8080"]
    Q["Qdrant<br/>gRPC :6334 / REST :6333"]
    M["MCP Server<br/>Python :8000"]
    O["Ollama<br/>Host :11434"]

    D --> F
    D --> B
    D --> Q
    D --> M

    F --> B
    B --> Q
    B --> M
    B --> O
```

Ollama remains on the host machine and is accessed by the backend
container through:

``` text
host.docker.internal:11434
```

## Design Decisions

### Why RAG?

Traditional LLMs do not automatically know the contents of a private
project.

RAG allows the application to retrieve relevant project information and
provide that information as context to the LLM.

This improves the grounding of project-specific responses.

### Why MCP?

MCP provides a standardized way to expose tools that allow an AI
application to interact with external resources.

In this project, MCP is used to provide project inspection capabilities
such as:

``` text
list_project_files()
search_code()
get_file()
search_api()
```

This keeps project interaction separate from the core LLM generation
logic.

### Why Qdrant?

Qdrant is used as the vector database for storing document embeddings
and performing semantic similarity search.

It provides a dedicated vector-search layer for the RAG pipeline.

### Why Ollama?

Ollama allows the application to run the LLM locally.

This provides:

- No dependency on paid AI APIs
- Local inference
- Easier experimentation
- Better control over development data

### Why Spring Boot?

Spring Boot acts as the central application layer.

It handles:

- REST APIs
- Document ingestion
- RAG orchestration
- Vector database integration
- LLM interaction
- MCP client integration

## Security and Grounding

The application is designed to reduce unsupported project-specific
claims.

The LLM prompt instructs the model to:

- Use provided project evidence
- Prefer actual source code when available
- Avoid inventing classes, files, methods, APIs, or implementation
  details
- Distinguish documentation from actual implementation
- Avoid making security claims that are not demonstrated by the source
  code

If the requested information is not available in the supplied project
evidence, the application instructs the model to respond:

``` text
That information is not available in the indexed project.
```

## Current Limitations

This project is an engineering-focused prototype.

Current limitations include:

- Local Ollama inference depends on available system resources.
- The current source-code inspection flow identifies a predefined set of
  project classes.
- The MCP server operates against the configured sample project.
- Document ingestion currently focuses on text-based project content.
- The Copilot itself does not currently implement user authentication or
  authorization.
- Conversation history is not persisted.

## Future Improvements

Potential improvements include:

- Dynamic project/repository indexing
- Git repository integration
- Support for multiple repositories
- Improved code symbol detection
- Conversation history
- Streaming LLM responses
- Authentication and user management
- Additional MCP tools
- Code dependency visualization
- Repository-wide code analysis
- Automated test generation
- Pull-request analysis
- Code review assistance

## Key Learning Outcomes

This project demonstrates practical implementation of:

- Full-stack application development
- Java and Spring Boot
- Vue.js
- REST API design
- Retrieval-Augmented Generation
- Vector databases
- Local LLM inference
- Embedding-based semantic search
- Model Context Protocol
- MCP tool integration
- Unit and controller testing
- Docker containerization
- Multi-container application orchestration

## Project Goal

The goal of this project is to demonstrate how modern AI application
patterns can be integrated with traditional enterprise application
development.

The application combines:

``` text
Java / Spring Boot
        +
Vue.js
        +
RAG
        +
Qdrant
        +
Ollama
        +
MCP
        +
Docker
```

to create a practical AI-assisted software engineering application
capable of answering project-specific questions using both indexed
knowledge and controlled source-code inspection.

## Author

Built as an AI engineering project demonstrating full-stack development,
RAG-based information retrieval, MCP-based tool integration, local LLM
inference, automated testing, and containerized deployment.
