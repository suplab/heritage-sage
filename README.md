# Heritage Sage — Lost Skills Revival Agent

An AI-powered platform that teaches rare and traditional skills (calligraphy, weaving, pottery, etc.), evaluates learner submissions, and provides adaptive feedback.

## Monorepo Structure

```
heritage-sage/
├── frontend/          React + Vite + TypeScript + Tailwind CSS
├── backend/           Spring Boot 3 (Java 17)
├── eval-service/      FastAPI image evaluation microservice (Python)
└── docker-compose.yml PostgreSQL 15
```

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 18, Vite, TypeScript, Tailwind CSS v4 |
| Backend | Spring Boot 3.2, Spring Data JPA |
| AI | LangChain4j 1.7.1 — Groq / HuggingFace / OpenAI |
| Database | PostgreSQL 15 (Docker) |
| Evaluation | FastAPI + OpenCV + scikit-image (SSIM) |

## Architecture

```
React (port 5173)
      ↓ REST API
Spring Boot (port 8080)
      ├── LangChain4j → Groq / HuggingFace / OpenAI
      ├── EvaluationService → FastAPI (port 8001)
      └── PostgreSQL (skills, lessons, evaluations)
```

## AI Provider Configuration

Set `AI_PROVIDER` to switch inference backend. Defaults to **Groq**.

### Groq (default — fastest)
```bash
export AI_PROVIDER=groq
export GROQ_API_KEY=your_key
export GROQ_MODEL=llama-3.3-70b-versatile   # optional
```

### HuggingFace Serverless Inference
```bash
export AI_PROVIDER=huggingface
export HUGGINGFACE_API_KEY=your_hf_token
export HUGGINGFACE_MODEL=meta-llama/Llama-3.2-3B-Instruct   # optional — any chat model
```

### OpenAI
```bash
export AI_PROVIDER=openai
export OPENAI_API_KEY=your_key
```

If no API key is set the app runs in mock mode and returns placeholder responses.

## Running Locally

**1. Start PostgreSQL**
```bash
docker-compose up -d
```

**2. Start image evaluation service**
```bash
cd eval-service
pip install -r requirements.txt
uvicorn app:app --reload --host 0.0.0.0 --port 8001
```

**3. Start backend**
```bash
cd backend
export AI_PROVIDER=groq
export GROQ_API_KEY=your_key
mvn spring-boot:run
```

**4. Start frontend**
```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

## API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/skills` | List all skills |
| `POST` | `/api/skills` | Create a skill `{"name","description"}` |
| `GET` | `/api/skills/{name}/lesson?level=beginner` | Generate AI lesson (beginner/intermediate/advanced) |
| `POST` | `/api/skills/{name}/evaluate` | Evaluate submission `{"learnerId","imageUrl","referenceUrl"}` |
| `GET` | `/api/skills/evaluation/{id}/adaptive-feedback` | Get adaptive feedback for an evaluation |

## Frontend Pages

| Route | Page |
|---|---|
| `/` | Skills list + create |
| `/skills/:name/lesson` | Level picker + AI lesson |
| `/skills/:name/evaluate` | Submit image URLs for evaluation |
| `/evaluation/:id/feedback` | View adaptive feedback |

## Running Tests

```bash
cd backend && mvn test
```

12 unit tests cover: provider initialisation (Groq/HF/OpenAI), JSON parsing, lesson fallback chain, lesson persistence, and adaptive feedback rule-based fallback.
