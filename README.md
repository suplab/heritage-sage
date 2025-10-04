# 🧵 “Lost Skills” Revival Agent

**Objective:** Autonomous learning agent that teaches rare skills, evaluates performance, and adapts lessons.

## MVP Features:

User selects a skill (e.g., calligraphy, traditional weaving, old language phrases).

Agent provides interactive lessons with examples.

User submits progress (image, text, or video).

Agent evaluates and gives feedback.

Personalized next-step recommendations.

## Agentic Workflow:

User selects skill → Agent generates lesson plan (text + visual aids).

User practices and submits output.

Agent evaluates using CV/NLP depending on skill type.

Agent generates feedback and adjusts next lesson.

Optional: Agent tracks long-term mastery and adapts difficulty.

## Tech Stack:

LLM: GPT-4 for lesson generation and feedback.

CV/NLP: OpenCV / Tesseract / custom lightweight evaluation models.

Database: SQLite for progress tracking.

Frontend: Streamlit / Gradio with image/video upload.

## Hackathon Roadmap:

Build skill selection interface and lesson generator.

Implement user submission system.

Integrate basic evaluation logic (CV/NLP).

Generate feedback and suggest next lesson.

Bonus: Add gamification or progress tracking.
