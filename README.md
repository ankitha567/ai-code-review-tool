# AI Code Review Tool 🛠️

An AI-powered collaborative code review tool that combines static code analysis, generative AI explanations, and real-time collaboration — built entirely on a free tech stack.

Users write or paste Java code into a live editor, run instant static analysis to catch bugs and code smells, and get AI-generated plain-English explanations with suggested fixes for each issue. Multiple users can join the same session and edit code together in real time, similar to Google Docs.

## Feature
-  **Static Analysis** — PMD scans Java code for bugs, code smells, and best-practice violations
-  **AI Explanations** — Google Gemini API explains each issue in plain English and generates a corrected code snippet
-  **Real-Time Collaboration** — Multiple users can edit the same code session live via WebSockets (STOMP)
-  **Live Code Editor** — Monaco Editor (the engine behind VS Code)
-  **100% Free Stack** — No paid services required

## Tech Stack

**Backend:** Java, Spring Boot, Spring WebSocket (STOMP), PMD  
**Frontend:** React, Vite, Monaco Editor, SockJS + StompJS  
**AI:** Google Gemini API (free tier)

## Architecture

React Frontend (Monaco Editor)
│
├── REST: POST /api/analyze → PMD static analysis
├── REST: POST /api/explain → Gemini AI explanation
└── WebSocket: /ws (STOMP) → Real-time code sync
│
Spring Boot Backend

## Setup & Run Locally

### Prerequisites
- JDK 17+
- Node.js
- A free Gemini API key from [aistudio.google.com](https://aistudio.google.com)

### Backend
```bash
cd backend/demo
# Copy the example config and add your own Gemini API key
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Edit application.properties and paste your Gemini API key

./mvnw clean install
./mvnw spring-boot:run
```
Backend runs on `http://localhost:8080`

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:5173`

## How It Works

1. Paste or write Java code in the editor
2. Click **Review Code** — PMD analyzes the code and lists issues
3. Click **Explain with AI** on any issue — Gemini explains the problem and suggests a fix
4. Open the app in a second tab with the same Room ID to see live collaborative editing in action

## License
This project is for educational/portfolio purposes.
