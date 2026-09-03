<script setup>
import { ref } from 'vue'

const question = ref('')
const answer = ref('')
const sources = ref([])
const tools = ref([])
const loading = ref(false)

const selectedFile = ref(null)
const uploading = ref(false)
const uploadMessage = ref('')
const uploadError = ref(false)

const handleFileSelect = (event) => {
  selectedFile.value = event.target.files[0] || null
  uploadMessage.value = ''
  uploadError.value = false
}

const uploadDocument = async () => {
  if (!selectedFile.value) return

  uploading.value = true
  uploadMessage.value = ''
  uploadError.value = false

  const formData = new FormData()
  formData.append('file', selectedFile.value)

  try {
    const response = await fetch(
      'http://localhost:8080/api/documents/upload',
      {
        method: 'POST',
        body: formData
      }
    )

    if (!response.ok) {
      throw new Error(`Upload failed: ${response.status}`)
    }

    const result = await response.json()

    uploadMessage.value =
      `${result.fileName} indexed successfully — ` +
      `${result.chunksCreated} chunk(s) created.`

    selectedFile.value = null
  } catch (error) {
    uploadError.value = true
    uploadMessage.value =
      'Unable to index the document. Make sure the backend and Qdrant are running.'

    console.error(error)
  } finally {
    uploading.value = false
  }
}

const askQuestion = async () => {
  if (!question.value.trim() || loading.value) return

  loading.value = true
  answer.value = ''
  sources.value = []
  tools.value = []

  try {
    const response = await fetch(
      'http://localhost:8080/api/rag/ask',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          question: question.value.trim()
        })
      }
    )

    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`)
    }

    const result = await response.json()

    answer.value = result.answer || 'No answer was generated.'
    sources.value = result.sources || []
    tools.value = result.tools || []
  } catch (error) {
    answer.value =
      'Unable to connect to the AI backend. Make sure the Spring Boot backend and Ollama are running.'

    console.error(error)
  } finally {
    loading.value = false
  }
}

const useExample = (example) => {
  question.value = example
}

const clearConversation = () => {
  question.value = ''
  answer.value = ''
  sources.value = []
  tools.value = []
}
</script>

<template>
  <div class="app">

    <!-- Header -->
    <header class="header">
      <div class="brand">
        <div class="brand-icon">AI</div>

        <div>
          <h1>AI Engineering Copilot</h1>
          <p>RAG-powered project assistant with MCP</p>
        </div>
      </div>

      <div class="status">
        <span class="status-dot"></span>
        <span>Local AI</span>
      </div>
    </header>

    <main class="main">

      <!-- Upload -->
      <section class="upload-card">

        <div class="upload-header">
          <div>
            <h3>Project Knowledge</h3>
            <p>
              Upload documentation or project files to add them to the
              RAG knowledge base.
            </p>
          </div>

          <div class="upload-icon">
            📄
          </div>
        </div>

        <div class="upload-controls">

          <label for="file-upload" class="file-input">
            <span v-if="!selectedFile">
              Choose file
            </span>

            <span v-else>
              {{ selectedFile.name }}
            </span>

            <input type="file" id="file-upload" @change="handleFileSelect" />
          </label>

          <button class="upload-button" @click="uploadDocument" :disabled="!selectedFile || uploading">
            <span v-if="uploading" class="spinner small"></span>

            {{
              uploading
                ? 'Indexing...'
                : 'Upload & Index'
            }}
          </button>

        </div>

        <div v-if="uploadMessage" :class="[
          'upload-message',
          uploadError ? 'error' : 'success'
        ]">
          <span>
            {{ uploadError ? '⚠' : '✓' }}
          </span>

          {{ uploadMessage }}
        </div>

      </section>

      <!-- Welcome -->
      <section class="welcome">

        <div class="hero-badge">
          RAG + MCP + Local LLM
        </div>

        <h2>Ask anything about your project</h2>

        <p>
          Search documentation, understand source code, and explore
          your project using AI-powered retrieval and MCP tools.
        </p>

      </section>

      <!-- Chat -->
      <section class="chat-card">

        <!-- Loading -->
        <div v-if="loading" class="loading-state">

          <div class="spinner"></div>

          <div>
            <strong>Analyzing your project...</strong>

            <p>
              Searching project knowledge and inspecting relevant source code.
            </p>
          </div>

        </div>

        <!-- Answer -->
        <div v-if="answer && !loading" class="answer">

          <div class="answer-header">

            <div class="bot-icon">
              🤖
            </div>

            <div>
              <strong>Copilot</strong>
              <span>AI-generated project analysis</span>
            </div>

          </div>

          <div class="answer-content">
            {{ answer }}
          </div>

          <!-- MCP Activity -->
          <div v-if="tools.length" class="result-section">

            <div class="section-heading">

              <div>
                <span class="section-icon">🔧</span>
                MCP Activity
              </div>

              <span class="section-count">
                {{ tools.length }} tool{{ tools.length > 1 ? 's' : '' }}
              </span>

            </div>

            <div class="tool-list">

              <div v-for="tool in tools" :key="tool" class="tool-card">
                <span class="tool-check">✓</span>

                <div>
                  <strong>{{ tool }}</strong>
                  <span>
                    MCP tool executed successfully
                  </span>
                </div>
              </div>

            </div>

          </div>

          <!-- Sources -->
          <div v-if="sources.length" class="result-section">

            <div class="section-heading">

              <div>
                <span class="section-icon">📚</span>
                Knowledge Sources
              </div>

              <span class="section-count">
                {{ sources.length }}
              </span>

            </div>

            <div class="source-list">

              <div v-for="source in sources" :key="source" class="source-card">
                <span class="file-icon">📄</span>

                <span>{{ source }}</span>
              </div>

            </div>

          </div>

        </div>

        <!-- Input -->
        <div class="input-area">

          <textarea v-model="question" placeholder="Ask about your project..." rows="4" :disabled="loading"
            @keydown.ctrl.enter="askQuestion"></textarea>

          <div class="input-footer">

            <div class="input-hint">
              <span>Ctrl</span>
              <span>+</span>
              <span>Enter</span>
              <span>to ask</span>
            </div>

            <div class="input-actions">

              <button v-if="question || answer" class="clear-button" @click="clearConversation" :disabled="loading">
                Clear
              </button>

              <button class="ask-button" @click="askQuestion" :disabled="loading || !question.trim()">
                <span v-if="loading" class="spinner small"></span>

                {{
                  loading
                    ? 'Thinking...'
                    : 'Ask Copilot'
                }}
              </button>

            </div>

          </div>

        </div>

      </section>

      <!-- Examples -->
      <section class="examples">

        <div class="examples-header">
          <h3>Try asking</h3>
          <span>Explore the project with natural language</span>
        </div>

        <div class="example-grid">

          <button @click="useExample(
            'What components are involved in authentication?'
          )">
            <span class="example-icon">🔐</span>

            <div>
              <strong>Authentication</strong>
              <span>
                What components are involved?
              </span>
            </div>
          </button>

          <button @click="useExample(
            'Explain how JwtFilter validates a token.'
          )">
            <span class="example-icon">🔎</span>

            <div>
              <strong>Source Code</strong>
              <span>
                Explain how JwtFilter validates a token
              </span>
            </div>
          </button>

          <button @click="useExample(
            'What APIs are available in this project?'
          )">
            <span class="example-icon">🔌</span>

            <div>
              <strong>Project APIs</strong>
              <span>
                What APIs are available?
              </span>
            </div>
          </button>

        </div>

      </section>

    </main>

  </div>
</template>

<style>
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family:
    Inter,
    -apple-system,
    BlinkMacSystemFont,
    "Segoe UI",
    sans-serif;

  background: #f6f7f9;
  color: #1f2937;
}

button,
textarea,
input {
  font: inherit;
}

button {
  transition:
    background 0.15s ease,
    border-color 0.15s ease,
    transform 0.15s ease;
}

.app {
  min-height: 100vh;
}

/* Header */

.header {
  height: 72px;
  padding: 0 48px;

  background: white;
  border-bottom: 1px solid #e5e7eb;

  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-icon {
  width: 36px;
  height: 36px;

  border-radius: 9px;

  background: #111827;
  color: white;

  display: flex;
  align-items: center;
  justify-content: center;

  font-size: 12px;
  font-weight: 800;
}

.header h1 {
  margin: 0;

  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.3px;
}

.header p {
  margin: 3px 0 0;

  font-size: 13px;
  color: #6b7280;
}

.status {
  display: flex;
  align-items: center;
  gap: 8px;

  font-size: 13px;
  color: #4b5563;
}

.status-dot {
  width: 9px;
  height: 9px;

  border-radius: 50%;
  background: #22c55e;

  box-shadow: 0 0 0 3px #dcfce7;
}

/* Main */

.main {
  width: min(900px, calc(100% - 40px));

  margin: 0 auto;
  padding: 52px 0 70px;
}

/* Upload */

.upload-card {
  background: white;

  border: 1px solid #e5e7eb;
  border-radius: 14px;

  padding: 20px;
  margin-bottom: 44px;

  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.02);
}

.upload-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.upload-header h3 {
  margin: 0 0 5px;

  font-size: 16px;
}

.upload-header p {
  margin: 0;

  color: #6b7280;
  font-size: 13px;
}

.upload-icon {
  width: 36px;
  height: 36px;

  border-radius: 9px;

  background: #f3f4f6;

  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-controls {
  display: flex;
  gap: 12px;
  margin-top: 18px;
}

.file-input {
  flex: 1;

  border: 1px solid #d1d5db;
  border-radius: 8px;

  padding: 10px 13px;

  color: #4b5563;
  font-size: 13px;

  cursor: pointer;

  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-input:hover {
  border-color: #9ca3af;
}

.file-input input {
  display: none;
}

.upload-button {
  border: none;
  border-radius: 8px;

  padding: 10px 16px;

  background: #111827;
  color: white;

  cursor: pointer;
  font-weight: 600;
  font-size: 13px;

  display: flex;
  align-items: center;
  gap: 8px;
}

.upload-button:hover:not(:disabled) {
  background: #374151;
}

.upload-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.upload-message {
  display: flex;
  align-items: center;
  gap: 7px;

  margin-top: 12px;

  padding: 9px 11px;

  border-radius: 7px;

  font-size: 13px;
}

.upload-message.success {
  background: #f0fdf4;
  color: #166534;
}

.upload-message.error {
  background: #fef2f2;
  color: #b91c1c;
}

/* Welcome */

.welcome {
  text-align: center;
  margin-bottom: 28px;
}

.hero-badge {
  display: inline-block;

  padding: 5px 10px;
  margin-bottom: 13px;

  border: 1px solid #e5e7eb;
  border-radius: 999px;

  background: white;

  font-size: 11px;
  font-weight: 700;

  color: #6b7280;
  letter-spacing: 0.3px;
  text-transform: uppercase;
}

.welcome h2 {
  margin: 0 0 10px;

  font-size: 32px;
  letter-spacing: -0.8px;
}

.welcome p {
  max-width: 650px;

  margin: 0 auto;

  color: #6b7280;
  font-size: 15px;
  line-height: 1.6;
}

/* Chat */

.chat-card {
  background: white;

  border: 1px solid #e5e7eb;
  border-radius: 14px;

  overflow: hidden;

  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

/* Loading */

.loading-state {
  padding: 25px;

  border-bottom: 1px solid #e5e7eb;

  display: flex;
  align-items: center;
  gap: 15px;
}

.loading-state strong {
  font-size: 14px;
}

.loading-state p {
  margin: 4px 0 0;

  color: #6b7280;
  font-size: 12px;
}

/* Answer */

.answer {
  padding: 25px;

  border-bottom: 1px solid #e5e7eb;
}

.answer-header {
  display: flex;
  align-items: center;
  gap: 10px;

  margin-bottom: 17px;
}

.bot-icon {
  width: 36px;
  height: 36px;

  border-radius: 9px;

  background: #f3f4f6;

  display: flex;
  align-items: center;
  justify-content: center;
}

.answer-header strong {
  display: block;

  font-size: 14px;
}

.answer-header span {
  display: block;

  margin-top: 2px;

  color: #9ca3af;
  font-size: 11px;
}

.answer-content {
  white-space: pre-wrap;

  line-height: 1.7;
  font-size: 15px;
}

/* Result sections */

.result-section {
  margin-top: 22px;

  padding-top: 17px;

  border-top: 1px solid #e5e7eb;
}

.section-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;

  margin-bottom: 10px;

  font-size: 12px;
  font-weight: 700;
  color: #4b5563;
}

.section-icon {
  margin-right: 5px;
}

.section-count {
  padding: 3px 7px;

  border-radius: 999px;

  background: #f3f4f6;

  color: #6b7280;

  font-size: 10px;
  font-weight: 600;
}

/* MCP */

.tool-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tool-card {
  display: flex;
  align-items: center;
  gap: 8px;

  padding: 8px 10px;

  border: 1px solid #bbf7d0;
  border-radius: 8px;

  background: #f0fdf4;
}

.tool-check {
  width: 19px;
  height: 19px;

  border-radius: 50%;

  background: #22c55e;
  color: white;

  display: flex;
  align-items: center;
  justify-content: center;

  font-size: 11px;
  font-weight: 700;
}

.tool-card strong {
  display: block;

  color: #166534;
  font-size: 12px;
}

.tool-card span:last-child {
  display: block;

  margin-top: 1px;

  color: #4d7c5c;
  font-size: 10px;
}

/* Sources */

.source-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.source-card {
  display: flex;
  align-items: center;
  gap: 7px;

  padding: 8px 10px;

  background: #f9fafb;

  border: 1px solid #e5e7eb;
  border-radius: 8px;

  color: #4b5563;

  font-size: 12px;
}

.file-icon {
  font-size: 13px;
}

/* Input */

.input-area {
  padding: 20px;
}

textarea {
  width: 100%;

  resize: vertical;

  border: none;
  outline: none;

  font-size: 15px;
  line-height: 1.6;

  color: #1f2937;

  background: transparent;
}

textarea::placeholder {
  color: #9ca3af;
}

textarea:disabled {
  opacity: 0.6;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;

  margin-top: 12px;
}

.input-hint {
  display: flex;
  align-items: center;
  gap: 4px;

  color: #9ca3af;
  font-size: 11px;
}

.input-hint span:not(:last-child) {
  padding: 2px 5px;

  border: 1px solid #e5e7eb;
  border-radius: 4px;

  background: #f9fafb;

  color: #6b7280;
  font-weight: 600;
}

.input-actions {
  display: flex;
  gap: 8px;
}

.clear-button {
  border: 1px solid #e5e7eb;
  border-radius: 8px;

  padding: 10px 15px;

  background: white;

  color: #6b7280;

  cursor: pointer;
  font-weight: 600;
  font-size: 13px;
}

.clear-button:hover:not(:disabled) {
  background: #f9fafb;
}

.ask-button {
  border: none;
  border-radius: 8px;

  padding: 10px 18px;

  background: #111827;
  color: white;

  cursor: pointer;
  font-weight: 600;
  font-size: 13px;

  display: flex;
  align-items: center;
  gap: 8px;
}

.ask-button:hover:not(:disabled) {
  background: #374151;
}

.ask-button:disabled,
.clear-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Spinner */

.spinner {
  width: 22px;
  height: 22px;

  border: 2px solid #e5e7eb;
  border-top-color: #111827;

  border-radius: 50%;

  animation: spin 0.8s linear infinite;
}

.spinner.small {
  width: 14px;
  height: 14px;

  border-color: rgba(255, 255, 255, 0.35);
  border-top-color: white;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Examples */

.examples {
  margin-top: 32px;
}

.examples-header {
  display: flex;
  align-items: baseline;
  gap: 10px;

  margin-bottom: 12px;
}

.examples-header h3 {
  margin: 0;

  font-size: 14px;
}

.examples-header span {
  color: #9ca3af;
  font-size: 11px;
}

.example-grid {
  display: grid;

  grid-template-columns: repeat(3, 1fr);

  gap: 12px;
}

.example-grid button {
  display: flex;
  align-items: flex-start;
  gap: 10px;

  min-height: 82px;

  padding: 14px;

  text-align: left;

  background: white;

  border: 1px solid #e5e7eb;
  border-radius: 10px;

  cursor: pointer;
}

.example-grid button:hover {
  border-color: #9ca3af;
  background: #fafafa;

  transform: translateY(-1px);
}

.example-icon {
  font-size: 17px;
}

.example-grid strong {
  display: block;

  margin-bottom: 4px;

  color: #374151;
  font-size: 12px;
}

.example-grid button span:last-child {
  color: #6b7280;

  font-size: 12px;
  line-height: 1.4;
}

/* Responsive */

@media (max-width: 700px) {

  .header {
    padding: 0 20px;
  }

  .header p {
    display: none;
  }

  .main {
    width: min(100% - 24px, 900px);

    padding: 30px 0 50px;
  }

  .welcome h2 {
    font-size: 26px;
  }

  .upload-controls {
    flex-direction: column;
  }

  .upload-button {
    justify-content: center;
  }

  .example-grid {
    grid-template-columns: 1fr;
  }

  .input-footer {
    align-items: flex-end;
  }

  .input-hint {
    display: none;
  }

  .input-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .answer {
    padding: 20px;
  }

}
</style>