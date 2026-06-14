import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

export interface Skill {
  id: number;
  name: string;
  description: string;
}

export interface EvaluationRecord {
  id: number;
  skillName: string;
  learnerId: string;
  score: number;
  feedback: string;
  evaluatedAt: string;
}

export const getSkills = () =>
  api.get<Skill[]>('/api/skills').then((r) => r.data);

export const createSkill = (name: string, description: string) =>
  api.post<Skill>('/api/skills', { name, description }).then((r) => r.data);

export const getLesson = (skillName: string, level: string) =>
  api.get<string>(`/api/skills/${encodeURIComponent(skillName)}/lesson`, {
    params: { level },
    transformResponse: [(data) => data],
  }).then((r) => r.data);

export const evaluateSkill = (
  skillName: string,
  learnerId: string,
  imageUrl: string,
  referenceUrl: string
) =>
  api
    .post<EvaluationRecord>(`/api/skills/${encodeURIComponent(skillName)}/evaluate`, {
      learnerId,
      imageUrl,
      referenceUrl,
    })
    .then((r) => r.data);

export const getAdaptiveFeedback = (id: number) =>
  api
    .get<{ adaptiveFeedback: string }>(`/api/skills/evaluation/${id}/adaptive-feedback`)
    .then((r) => r.data.adaptiveFeedback);
