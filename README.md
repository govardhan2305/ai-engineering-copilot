# AI Engineering Copilot

An AI-powered engineering assistant that helps developers understand project documentation and source code using **Retrieval-Augmented Generation (RAG)** and **Model Context Protocol (MCP)**.

The application combines a Vue.js frontend, Spring Boot backend, Qdrant vector database, Python MCP server, and a locally running Ollama LLM to provide project-aware answers without requiring paid AI APIs.

---

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

---

## Architecture

```text
                         ┌──────────────────────┐
                         │      Vue.js UI       │
                         │      Port 5173       │
                         └──────────┬───────────┘
                                    │
                                    │ REST API
                                    ▼
                         ┌──────────────────────┐
                         │    Spring Boot       │
                         │      Backend         │
                         │      Port 8080       │
                         └───────┬───────┬──────┘
                                 │       │
                         RAG     │       │ MCP
                                 │       │
                                 ▼       ▼
                       ┌────────────┐  ┌──────────────┐
                       │   Qdrant   │  │ MCP Server   │
                       │ Vector DB  │  │ Python       │
                       │   :6334    │  │    :8000     │
                       └────────────┘  └──────┬───────┘
                                              │
                                              ▼
                                      ┌──────────────┐
                                      │ Source Code  │
                                      │ Project Files│
                                      └──────────────┘

                         Spring AI
                             │
                             ▼
                       ┌────────────┐
                       │   Ollama   │
                       │ Local LLM  │
                       └────────────┘
```

## How It Works

### 1. Document Ingestion

A user uploads project documentation through the Vue.js interface.

Document
│
▼
Spring Boot
│
▼
Text Extraction
│
▼
Token-based Chunking
│
▼
Ollama Embeddings
│
▼
Qdrant

The document is split into smaller chunks and stored as vector embeddings in Qdrant.

### 2. RAG-based Question Answering

When a user asks a question, the backend performs semantic similarity search against the indexed project knowledge.

User Question
│
▼
Spring Boot
│
▼
Semantic Search
│
▼
Qdrant
│
▼
Relevant Documents
│
▼
Context Construction
│
▼
Ollama
│
▼
Generated Answer

The retrieved project context is provided to the local LLM to generate a grounded response.

### 3. MCP-based Source Code Investigation

For implementation-oriented questions, the backend can use MCP to inspect the actual project source code.

User Question
│
▼
Spring Boot
│
├──────────────► Qdrant
│ │
│ ▼
│ RAG Context
│
└──────────────► MCP Server
│
├── search_code()
│
└── get_file()
│
▼
Source Code

The retrieved source code is combined with the RAG context before the final answer is generated.

## RAG and MCP

RAG and MCP serve different purposes in the application.

### RAG

\*\*Retrieval-Augmented Generation provides semantic retrieval from indexed project knowledge.

It is useful for questions such as:

What authentication approach does this project use?

The system searches the indexed documentation and retrieves relevant information before generating the answer.

### MCP

\*\*Model Context Protocol provides structured access to project tools and resources.

It is useful for questions such as:

Explain how JwtFilter validates a token.

The application can use MCP tools to search and retrieve the actual source code.

### Combined Approach

```text
RAG
 │
 └── Project Knowledge
      └── Indexed Documentation


MCP
 │
 └── Project Interaction
      └── Source Code / Tools


RAG + MCP
 │
 └── Grounded Project Understanding
```

---

## MCP Tools

The Python MCP server exposes project inspection tools.

Tool Purpose
list_project_files() Lists files available in the project
search_code(query) Searches project source code
get_file(path) Retrieves the contents of a project file
search_api(query) Searches project files for API-related definitions

Spring Boot connects to the MCP server using Streamable HTTP.

## Technology Stack

### Frontend

Vue.js
JavaScript
HTML
CSS
Backend
Java 21
Spring Boot 4
Spring AI
REST APIs
AI and RAG
Ollama
Qdrant
Vector embeddings
Retrieval-Augmented Generation
MCP
Python 3.11
MCP
FastMCP
Streamable HTTP

### Testing

- JUnit
  Mockito
  Spring Boot Test

### DevOps

- Docker
  Docker Compose
  Nginx
  Project Structure
  ai-engineering-copilot/
  │
  ├── backend/
  │ ├── src/
  │ │ ├── main/
  │ │ └── test/
  │ ├── Dockerfile
  │ └── pom.xml
  │
  ├── frontend/
  │ ├── src/
  │ ├── Dockerfile
  │ ├── nginx.conf
  │ └── package.json
  │
  ├── mcp-server/
  │ ├── server.py
  │ ├── requirements.txt
  │ └── Dockerfile
  │
  ├── sample-project/
  │ ├── src/
  │ ├── authentication.md
  │ └── README.md
  │
  ├── docker-compose.yml
  └── README.md

## Prerequisites

The following software is required:

Java 21
Maven
Node.js
Python 3.11+
Docker Desktop
Ollama

## Ollama Setup

Ollama is used to run the LLM and embedding model locally.

The project uses:

qwen2.5:1.5b

for chat generation and:

nomic-embed-text

for embeddings.

Verify the installed models:

ollama list

Make sure Ollama is running before starting the application.

## Running the Application

### Option 1: Docker Compose

The recommended way to run the complete application is:

docker compose up --build

The application consists of:

Frontend → http://localhost:5173
Backend → http://localhost:8080
Qdrant → http://localhost:6333
MCP Server → http://localhost:8000
Ollama → Local host

Stop the application:

docker compose down

### Option 2: Run Frontend Locally

Navigate to the frontend directory:

cd frontend

Install dependencies:

npm install

Start the development server:

npm run dev

The frontend will be available at:

http://localhost:5173

### Option 3: Run Backend Locally

Navigate to the backend:

cd backend

Run the application:

mvn spring-boot:run

The backend will be available at:

http://localhost:8080

## API Endpoints

### Upload Document

POST /api/documents/upload

Uploads a document and indexes its content into Qdrant.

Ask Question
POST /api/rag/ask

Accepts a project-related question and returns:

Generated answer
Knowledge sources
MCP activity
Health Check
GET /actuator/health

Used to verify the Spring Boot application health.

Example Questions
Documentation / RAG
What authentication approach does this project use?
Source Code + MCP
Explain how JwtFilter validates a token.
Project Understanding
What components are involved in authentication?
API Exploration
What APIs are available in this project?

## Example MCP Flow

For a source-code question such as:

Explain how JwtFilter validates a token.

the request flows through the system as follows:

User
│
│ Question
▼
Vue.js
│
▼
Spring Boot
│
├──────────────► Qdrant
│ │
│ └── Relevant project knowledge
│
└──────────────► MCP Server
│
├── search_code()
│
└── get_file()
│
▼
JwtFilter.java
│
▼
Ollama
│
▼
Answer

The frontend displays the MCP activity and knowledge sources associated with the response.

## Document Ingestion Flow

                 Uploaded Document
                        │
                        ▼
                Spring Boot API
                        │
                        ▼
                 Text Extraction
                        │
                        ▼
                TokenTextSplitter
                        │
                        ▼
                Document Chunks
                        │
                        ▼
                Ollama Embeddings
                        │
                        ▼
                     Qdrant

The indexed documents become searchable through semantic similarity.

## Testing

The backend uses JUnit, Mockito, and Spring Boot Test.

Run the complete test suite:

cd backend
mvn clean test

The project includes tests covering:

MCP activity tracking
Document ingestion
Document upload controller
RAG controller

The test suite validates the core backend functionality independently from the running frontend.

## Docker Architecture

Docker Compose manages the application services.

┌────────────────────────────────────────────────────────┐
│ Docker Compose │
│ │
│ ┌──────────────┐ ┌─────────────────────────┐ │
│ │ Frontend │──────►│ Backend │ │
│ │ Nginx │ │ Spring Boot │ │
│ │ :5173 │ │ :8080 │ │
│ └──────────────┘ └───────┬─────────┬───────┘ │
│ │ │ │
│ ▼ ▼ │
│ ┌──────────┐ ┌───────────┐ │
│ │ Qdrant │ │ MCP Server│ │
│ │ :6334 │ │ :8000 │ │
│ └──────────┘ └───────────┘ │
│ │
└────────────────────────────────────────────────────────┘
│
▼
Ollama Host

Ollama remains on the host machine and is accessed by the backend through:

host.docker.internal:11434

## Design Decisions

### Why RAG?

Traditional LLMs do not automatically know the contents of a private project.

RAG allows the application to retrieve relevant project information and provide that information as context to the LLM.

This improves the grounding of project-specific responses.

### Why MCP?

MCP provides a standardized way to expose tools that allow an AI application to interact with external resources.

In this project, MCP is used to provide project inspection capabilities such as:

list_project_files()
search_code()
get_file()
search_api()

This keeps project interaction separate from the core LLM generation logic.

### Why Qdrant?

Qdrant is used as the vector database for storing document embeddings and performing semantic similarity search.

It provides a dedicated vector-search layer for the RAG pipeline.

### Why Ollama?

Ollama allows the application to run the LLM locally.

This provides:

No dependency on paid AI APIs
Local inference
Easier experimentation
Better control over development data

### Why Spring Boot?

Spring Boot acts as the central application layer.

It handles:

REST APIs
Document ingestion
RAG orchestration
Vector database integration
LLM interaction
MCP client integration

## Security and Grounding

The application is designed to reduce unsupported project-specific claims.

The LLM prompt instructs the model to:

Use provided project evidence
Prefer actual source code when available
Avoid inventing classes, files, methods, APIs, or implementation details
Distinguish documentation from actual implementation
Avoid making security claims that are not demonstrated by the source code

If the requested information is not available in the supplied project evidence, the application instructs the model to respond:

That information is not available in the indexed project.

## Current Limitations

This project is an engineering-focused prototype.

Current limitations include:

Local Ollama inference depends on available system resources.
The current source-code inspection flow identifies a predefined set of project classes.
The MCP server operates against the configured sample project.
Document ingestion currently focuses on text-based project content.
The Copilot itself does not currently implement user authentication or authorization.
Conversation history is not persisted.

## Future Improvements

Potential improvements include:

Dynamic project/repository indexing
Git repository integration
Support for multiple repositories
Improved code symbol detection
Conversation history
Streaming LLM responses
Authentication and user management
Additional MCP tools
Code dependency visualization
Repository-wide code analysis
Automated test generation
Pull-request analysis
Code review assistance

## Key Learning Outcomes

This project demonstrates practical implementation of:

Full-stack application development
Java and Spring Boot
Vue.js
REST API design
Retrieval-Augmented Generation
Vector databases
Local LLM inference
Embedding-based semantic search
Model Context Protocol
MCP tool integration
Unit and controller testing
Docker containerization
Multi-container application orchestration

## Project Goal

The goal of this project is to demonstrate how modern AI application patterns can be integrated with traditional enterprise application development.

The application combines:

Java / Spring Boot +
Vue.js +
RAG +
Qdrant +
Ollama +
MCP +
Docker

to create a practical AI-assisted software engineering application capable of answering project-specific questions using both indexed knowledge and controlled source-code inspection.

## Author

Built as an AI engineering project demonstrating full-stack development,
RAG-based information retrieval, MCP-based tool integration,
local LLM inference, automated testing, and containerized deployment.

```

```
