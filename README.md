# 🧵 ReviveCraft - “Lost Skills” Revival Agent

**Objective:** Autonomous learning agent that teaches rare skills, evaluates performance, and adapts lessons.

## MVP Features:

- User selects a skill (e.g., calligraphy, traditional weaving, old language phrases).
- Agent provides interactive lessons with examples.
- User submits progress (image, text, or video).
- Agent evaluates and gives feedback.
- Personalized next-step recommendations.

## System Architecture (Spring + AI Integration)

```
+------------------------------------------------------+
|                      User Interface                  |
| (React / Angular / Thymeleaf / Mobile App)           |
+------------------------------------------------------+
                | REST API (JSON)
                v
+------------------------------------------------------+
|                 Spring Boot Backend                  |
|------------------------------------------------------|
|  1. SkillController (API Layer)                      |
|  2. LessonService (Business Logic)                   |
|  3. EvaluationService (Feedback Engine)              |
|  4. ProgressService (Tracks User Learning)           |
|  5. AIIntegrationService (Calls OpenAI / Ollama)     |
+------------------------------------------------------+
                |                     |
                v                     v
+-------------------------+   +-------------------------+
|  PostgreSQL / MySQL DB  |   |   Python microservice   |
|  (skills, lessons, etc) |   |   (CV/NLP evaluation)   |
+-------------------------+   +-------------------------+
```

## Tech Stack:

| Layer               | Technology                                                        |
| ------------------- | ----------------------------------------------------------------- |
| Backend             | Spring Boot 3.x (Java 17+)                                        |
| Database            | PostgreSQL (Docker)                                               |
| AI Interface        | LangChain4j + OpenAI / Ollama / Hugging Face                      |
| Evaluation Service  | FastAPI + OpenCV + scikit-image system                            |
| Optional            | Neo4j (skill relationships), ElevenLabs TTS for spoken lessons |

## 🧠 Core Agent Flow Architecture

### 1️⃣ Lesson Generation Flow (LangChain4j Java Agent)

**Goal:** Generate structured lessons to teach forgotten or traditional skills (e.g., pottery, weaving, calligraphy).

**Flow:**
```
User → SkillController → LessonService → LangChainLessonService (LangChain4j Chain)
      ↳ ContextBuilder (adds learner level, region, available materials)
      ↳ PromptTemplate ("Teach {skill} in 3 levels with hands-on exercises and local context")
      ↳ LangChain4j ChatModel (OpenAI / Ollama)
      ↳ Lesson object → PostgreSQL
```

**LangChain4j Chain Example:**

```java
ChatModel chatModel = OpenAiChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .modelName("gpt-4o-mini")
    .build();

ChatChain chain = ChatChain.builder()
    .chatModel(chatModel)
    .build();

String output = chain.run("""
You are a skill revival mentor.
Generate a step-by-step learning path for the skill: %s.
Include beginner, intermediate, and expert modules.
""".formatted(skillName));
```

### 2️⃣ Image / Video Evaluation Flow (Python FastAPI Agent)

**Goal:** Evaluate a learner’s uploaded work sample (e.g., photo of pottery) for quality and authenticity.

**Flow:**
```
User uploads image → FastAPI Eval Service
   ↳ OpenCV / SSIM comparison with reference image
   ↳ Optional ML Model (ResNet50 / CLIP for semantic similarity)
   ↳ Feedback JSON → Java backend
   ↳ Stored as EvaluationRecord (skill_id, learner_id, score, comments)
```

**FastAPI sample (simplified):**
```python
@app.post("/evaluate-image")
async def evaluate_image(file: UploadFile, reference: UploadFile):
    img = cv2.imdecode(np.frombuffer(await file.read(), np.uint8), cv2.IMREAD_COLOR)
    ref = cv2.imdecode(np.frombuffer(await reference.read(), np.uint8), cv2.IMREAD_COLOR)
    score = compare_ssim(cv2.cvtColor(img, cv2.COLOR_BGR2GRAY),
                         cv2.cvtColor(ref, cv2.COLOR_BGR2GRAY))
    return {"score": score, "feedback": "Nice craftsmanship!" if score > 0.8 else "Needs refinement"}
```

### 3️⃣ Reflection & Adaptive Feedback Flow (Java Agent Loop)

**Goal:** Continuously adapt lessons based on learner performance and emotional tone.

**Flow:**
```
EvaluationRecord + EmotionContext → FeedbackAgent (LangChain4j)
   ↳ Synthesizes encouragement and next-step suggestions.
   ↳ Updates learner profile (difficulty level, motivation notes).
```

**LangChain4j Agent Sketch:**
```java
String feedbackPrompt = """
Analyze this learner's performance and tone:
- Score: %s
- Comments: %s
- Emotional tone: %s
Give encouraging, adaptive feedback.
""".formatted(score, comments, emotionTone);

String feedback = chain.run(feedbackPrompt);
```

### 4️⃣ Knowledge Graph Integration (Future Extension)

Store relationships between skills, tools, and regional origins in Neo4j or AWS Neptune for contextual recommendations:

> “Since you learned Weaving, you might enjoy Natural Dyeing or Embroidery from Odisha.”

## How to run locally

### Start DB:
```bash
docker-compose up -d
```

### Start eval service:
```bash
cd eval-service
pip install -r requirements.txt
uvicorn app:app --reload --host 0.0.0.0 --port 8001
```

### Start Java backend:
```bash
mvn spring-boot:run
```

### Example workflow:

- Create skill:
```bash
POST http://localhost:8080/api/skills
{ "name": "Calligraphy", "description": "Traditional pen calligraphy" }
```

- Generate lesson:
```bash
GET http://localhost:8080/api/skills/Calligraphy/lesson
```

- Evaluate (image URLs must be publicly accessible):
```bash
POST http://localhost:8080/api/skills/Calligraphy/evaluate
{ "learnerId":"user1", "imageUrl":"https://example.com/user.jpg", "referenceUrl":"https://example.com/ref.jpg" }
```

- Get adaptive feedback:
```bash
GET http://localhost:8080/api/skills/evaluation/1/adaptive-feedback
```

## Roadmap:

- LangChain4j parts are skeleton/pseudocode and need implementation.
- Build skill selection interface and lesson generator.
- Implement user submission system.
- Integrate basic evaluation logic (CV/NLP).
- Generate feedback and suggest next lesson.
- Bonus: Add gamification or progress tracking.
