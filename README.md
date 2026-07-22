# AlgoMind AI 🧠

AlgoMind AI is an interactive, AI-powered Data Structures and Algorithms (DSA) learning platform. 
It moves beyond static code execution by dynamically tracing variables, unrolling recursion trees, visualising Dynamic Programming matrices, and providing AI-guided mentoring in real time.

## 🌟 Features

- **Dynamic Execution Engine:** Parses and dynamically executes Java code, capturing deep state snapshots (variables, call stack, loop bounds) step-by-step.
- **WebSocket Streaming:** Broadcasts execution timelines and visualization states in real-time.
- **Recursion & DP Visualization:** Automatically generates visual trees for recursive algorithms and grids for tabulation/memoization strategies.
- **Intelligent AI Mentor:** Translates obscure JVM exceptions into beginner-friendly explanations, detects infinite loops, and provides conceptual hints without giving away the answer.
- **Big-O Analysis:** Statically and dynamically infers Time and Space complexities.

## 🛠 Tech Stack

- **Backend:** Java 21, Spring Boot 3, Spring Security, JWT, Spring WebSockets, JavaParser AST
- **Database:** PostgreSQL, Flyway, Spring Data JPA, H2 (Testing)
- **AI Integration:** OpenAI/Gemini API via Spring WebFlux, Caffeine Caching
- **Frontend:** React 18, Vite, Tailwind CSS, Framer Motion
- **DevOps:** Docker, Docker Compose, GitHub Actions CI/CD

## 🚀 Quick Start (Docker)

To run the entire stack (PostgreSQL, Backend, Frontend) locally:

```bash
docker-compose up --build
```
- Frontend available at: `http://localhost:3000`
- Backend API at: `http://localhost:8080`
- Swagger Docs at: `http://localhost:8080/swagger-ui.html`

## 🏗 Architecture

1. **Parser Layer:** Uses `JavaParser` to generate an Abstract Syntax Tree from user inputs.
2. **Simulation Layer:** Executes safe state machines that mimic the exact control flow of the parsed algorithm.
3. **Tracking Layer:** Captures deep copies of variables and dependencies into an `ExecutionState`.
4. **Presentation Layer:** Formats states and streams them via STOMP WebSockets to the React client.

## 🤝 Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.
