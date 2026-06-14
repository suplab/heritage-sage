# Heritage Sage — Lost Skills Revival Agent

An AI-powered platform that teaches rare and traditional skills (calligraphy, weaving, pottery, etc.), evaluates learner work, and generates adaptive feedback. The agent selects lessons based on proficiency level, scores image submissions by comparing them to a reference, and uses the evaluation result to tailor its next suggestions.

## Monorepo Structure

```
heritage-sage/
├── frontend/          React + Vite + TypeScript + Tailwind CSS
├── backend/           Spring Boot 3 (Java 17) — REST API + AI + DB
├── eval-service/      FastAPI microservice — image similarity scoring
└── docker-compose.yml PostgreSQL 15
```

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 18, Vite, TypeScript, Tailwind CSS v4 |
| Backend | Spring Boot 3.2, Spring Data JPA, LangChain4j 1.7.1 |
| AI Providers | Groq (default) / HuggingFace / OpenAI — switchable via env var |
| Database | PostgreSQL 15 (Docker) — tables: `skills`, `lessons`, `evaluations` |
| Evaluation | FastAPI + OpenCV + scikit-image (SSIM image similarity) |

---

## Core Agent Flows

### 1. Lesson Generation

```
React UI → GET /api/skills/{name}/lesson?level=beginner
         → LessonService
              → LangChainLessonService (LangChain4j + Groq/HF/OpenAI)
                   ↳ Prompt: "Create a {level} lesson for '{skill}'…"
                   ↳ Falls back to direct REST call if LangChain unavailable
              → Lesson saved to PostgreSQL
         ← AI-generated lesson text returned to UI
```

Level options: `beginner`, `intermediate`, `advanced`.

---

### 2. Image Evaluation

```
React UI → POST /api/skills/{name}/evaluate
           { learnerId, imageUrl, referenceUrl }
         → EvaluationService (Java)
              → Downloads both images from public URLs
              → Sends multipart request to eval-service (port 8001)
         → FastAPI eval-service
              → Decodes images with OpenCV
              → Computes SSIM score (0.0 – 1.0) between learner image and reference
              ← Returns { score, feedback }
         → EvaluationRecord saved to PostgreSQL
         ← Score + feedback returned to UI
```

---

### 3. Adaptive Feedback

```
React UI → GET /api/skills/evaluation/{id}/adaptive-feedback
         → FeedbackAgentService
              → Loads EvaluationRecord from DB (score + evaluator feedback)
              → Builds prompt:
                   "Learner score: {score}. Feedback: {feedback}.
                    Provide: encouragement, a practice exercise,
                    and difficulty adjustment (stay/simplify/advance)."
              → LangChain4j → Groq/HF/OpenAI
                   ↳ Rule-based fallback if no API key configured
         ← Adaptive feedback text returned to UI
```

---

## AI Provider Configuration

Set `AI_PROVIDER` to switch inference backend. Defaults to **Groq**.

| Provider | Env vars required |
|---|---|
| `groq` (default) | `GROQ_API_KEY`, optionally `GROQ_MODEL` (default: `llama-3.3-70b-versatile`) |
| `huggingface` | `HUGGINGFACE_API_KEY`, optionally `HUGGINGFACE_MODEL` (default: `meta-llama/Llama-3.2-3B-Instruct`) |
| `openai` | `OPENAI_API_KEY` |

Both Groq and HuggingFace expose OpenAI-compatible REST APIs, so no extra dependencies are needed to switch between them. If no API key is configured, the app runs in mock mode and returns placeholder responses.

---

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
# → http://localhost:5173
```

---

## API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/skills` | List all skills |
| `POST` | `/api/skills` | Create a skill `{"name","description"}` |
| `GET` | `/api/skills/{name}/lesson?level=beginner` | Generate AI lesson |
| `POST` | `/api/skills/{name}/evaluate` | Evaluate `{"learnerId","imageUrl","referenceUrl"}` |
| `GET` | `/api/skills/evaluation/{id}/adaptive-feedback` | Adaptive feedback for an evaluation |

---

## Running Tests

```bash
cd backend && mvn test
```

12 unit tests covering provider initialisation, JSON parsing, lesson fallback chain, lesson persistence, and adaptive feedback fallback.
