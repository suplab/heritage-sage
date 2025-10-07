FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN apt-get update && apt-get install -y build-essential libgl1-mesa-glx libglib2.0-0 && \
    pip install --no-cache-dir -r requirements.txt && rm -rf /var/lib/apt/lists/*
COPY . .
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8001"]