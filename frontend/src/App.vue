<script setup>
import { ref } from 'vue'

const question = ref('')
const answer = ref('')
const loading = ref(false)

const askQuestion = async () => {
  if (!question.value.trim()) return

  loading.value = true
  answer.value = ''

  try {
    const response = await fetch('http://localhost:8080/api/rag/ask', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        question: question.value
      })
    })

    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`)
    }

    answer.value = await response.text()
  } catch (error) {
    answer.value = 'Unable to connect to the AI backend. Make sure Spring Boot is running on port 8080.'
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="app">

    <header class="header">
      <div>
        <h1>AI Engineering Copilot</h1>
        <p>RAG-powered project assistant with MCP</p>
      </div>

      <div class="status">
        <span class="status-dot"></span>
        Local AI
      </div>
    </header>

    <main class="main">

      <section class="welcome">
        <h2>Ask anything about your project</h2>
        <p>
          Search documentation, understand source code, and explore your
          project using AI.
        </p>
      </section>

      <section class="chat-card">

        <div v-if="answer" class="answer">
          <div class="answer-header">
            <span class="bot-icon">🤖</span>
            <strong>Copilot</strong>
          </div>

          <div class="answer-content">
            {{ answer }}
          </div>
        </div>

        <div class="input-area">
          <textarea
            v-model="question"
            placeholder="Ask about your project..."
            rows="4"
            @keydown.ctrl.enter="askQuestion"
          ></textarea>

          <div class="input-footer">
            <span>Ctrl + Enter to ask</span>

            <button
              @click="askQuestion"
              :disabled="loading || !question.trim()"
            >
              {{ loading ? 'Thinking...' : 'Ask Copilot' }}
            </button>
          </div>
        </div>

      </section>

      <section class="examples">

        <h3>Try asking</h3>

        <div class="example-grid">

          <button
            @click="question = 'Where is JWT authentication implemented in this project?'"
          >
            🔐 Where is JWT authentication implemented?
          </button>

          <button
            @click="question = 'Explain how JwtFilter validates a token.'"
          >
            🔎 Explain how JwtFilter validates a token
          </button>

          <button
            @click="question = 'What APIs are available in this project?'"
          >
            🔌 What APIs are available?
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
textarea {
  font: inherit;
}

.app {
  min-height: 100vh;
}

.header {
  height: 72px;
  padding: 0 48px;
  background: white;
  border-bottom: 1px solid #e5e7eb;

  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
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
}

.main {
  width: min(900px, calc(100% - 40px));
  margin: 0 auto;
  padding: 70px 0;
}

.welcome {
  text-align: center;
  margin-bottom: 32px;
}

.welcome h2 {
  margin: 0 0 10px;
  font-size: 32px;
}

.welcome p {
  margin: 0;
  color: #6b7280;
  font-size: 15px;
}

.chat-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.answer {
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.answer-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.bot-icon {
  font-size: 20px;
}

.answer-content {
  white-space: pre-wrap;
  line-height: 1.7;
  font-size: 15px;
}

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
}

textarea::placeholder {
  color: #9ca3af;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;

  margin-top: 12px;
}

.input-footer span {
  font-size: 12px;
  color: #9ca3af;
}

.input-footer button {
  border: none;
  border-radius: 8px;

  padding: 10px 18px;

  background: #111827;
  color: white;

  cursor: pointer;
  font-weight: 600;
}

.input-footer button:hover:not(:disabled) {
  background: #374151;
}

.input-footer button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.examples {
  margin-top: 32px;
}

.examples h3 {
  font-size: 14px;
  margin-bottom: 12px;
}

.example-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.example-grid button {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 10px;

  padding: 15px;

  text-align: left;
  cursor: pointer;

  font-size: 13px;
  color: #4b5563;
}

.example-grid button:hover {
  border-color: #9ca3af;
  background: #fafafa;
}

@media (max-width: 700px) {
  .header {
    padding: 0 20px;
  }

  .main {
    padding: 40px 0;
  }

  .welcome h2 {
    font-size: 26px;
  }

  .example-grid {
    grid-template-columns: 1fr;
  }
}
</style>