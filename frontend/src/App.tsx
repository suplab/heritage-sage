import { BrowserRouter, Routes, Route } from 'react-router-dom';
import SkillsPage from './pages/SkillsPage';
import LessonPage from './pages/LessonPage';
import EvaluatePage from './pages/EvaluatePage';
import FeedbackPage from './pages/FeedbackPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<SkillsPage />} />
        <Route path="/skills/:name/lesson" element={<LessonPage />} />
        <Route path="/skills/:name/evaluate" element={<EvaluatePage />} />
        <Route path="/evaluation/:id/feedback" element={<FeedbackPage />} />
      </Routes>
    </BrowserRouter>
  );
}
