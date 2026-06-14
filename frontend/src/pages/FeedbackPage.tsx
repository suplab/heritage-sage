import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getAdaptiveFeedback } from '../api/client';

export default function FeedbackPage() {
  const { id } = useParams<{ id: string }>();
  const [feedback, setFeedback] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!id) return;
    getAdaptiveFeedback(Number(id))
      .then(setFeedback)
      .catch(() => setError('Failed to load adaptive feedback.'))
      .finally(() => setLoading(false));
  }, [id]);

  return (
    <div className="max-w-2xl mx-auto p-6">
      <Link to="/" className="text-amber-600 hover:underline text-sm mb-4 inline-block">← All Skills</Link>
      <h1 className="text-2xl font-bold text-gray-800 mb-1">Adaptive Feedback</h1>
      <p className="text-gray-400 text-sm mb-6">Evaluation #{id}</p>

      {loading && <p className="text-gray-400 text-sm">Loading feedback…</p>}
      {error && <p className="text-red-500 text-sm">{error}</p>}

      {feedback && (
        <div className="bg-white rounded-xl shadow p-5">
          <pre className="whitespace-pre-wrap text-sm text-gray-700 font-sans leading-relaxed">{feedback}</pre>
        </div>
      )}
    </div>
  );
}
