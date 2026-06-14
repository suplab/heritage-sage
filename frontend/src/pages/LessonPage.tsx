import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getLesson } from '../api/client';

type Level = 'beginner' | 'intermediate' | 'advanced';

export default function LessonPage() {
  const { name } = useParams<{ name: string }>();
  const skillName = decodeURIComponent(name ?? '');
  const [level, setLevel] = useState<Level>('beginner');
  const [lesson, setLesson] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleGenerate = async () => {
    setLoading(true);
    setError('');
    setLesson('');
    try {
      const content = await getLesson(skillName, level);
      setLesson(content);
    } catch {
      setError('Failed to generate lesson. Ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-6">
      <Link to="/" className="text-amber-600 hover:underline text-sm mb-4 inline-block">← All Skills</Link>
      <h1 className="text-2xl font-bold text-gray-800 mb-1">{skillName}</h1>
      <p className="text-gray-400 text-sm mb-6">AI-generated lesson</p>

      <div className="flex gap-2 mb-4">
        {(['beginner', 'intermediate', 'advanced'] as Level[]).map((l) => (
          <button
            key={l}
            onClick={() => setLevel(l)}
            className={`capitalize text-sm rounded-full px-4 py-1.5 border transition ${
              level === l
                ? 'bg-amber-600 text-white border-amber-600'
                : 'border-gray-300 text-gray-600 hover:border-amber-400'
            }`}
          >
            {l}
          </button>
        ))}
      </div>

      <button
        onClick={handleGenerate}
        disabled={loading}
        className="bg-amber-600 hover:bg-amber-700 text-white rounded-lg px-5 py-2 text-sm font-medium disabled:opacity-50 mb-6"
      >
        {loading ? 'Generating…' : 'Generate Lesson'}
      </button>

      {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

      {lesson && (
        <div className="bg-white rounded-xl shadow p-5">
          <h2 className="font-semibold text-gray-700 mb-3 capitalize">{level} Lesson</h2>
          <pre className="whitespace-pre-wrap text-sm text-gray-700 font-sans leading-relaxed">{lesson}</pre>
        </div>
      )}
    </div>
  );
}
