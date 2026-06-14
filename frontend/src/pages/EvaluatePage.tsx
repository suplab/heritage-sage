import { useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { evaluateSkill, type EvaluationRecord } from '../api/client';

export default function EvaluatePage() {
  const { name } = useParams<{ name: string }>();
  const skillName = decodeURIComponent(name ?? '');
  const navigate = useNavigate();

  const [learnerId, setLearnerId] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [referenceUrl, setReferenceUrl] = useState('');
  const [result, setResult] = useState<EvaluationRecord | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setResult(null);
    try {
      const record = await evaluateSkill(skillName, learnerId || 'anonymous', imageUrl, referenceUrl);
      setResult(record);
    } catch {
      setError('Evaluation failed. Make sure both image URLs are publicly accessible and the eval service is running.');
    } finally {
      setLoading(false);
    }
  };

  const scoreColor = (score: number) =>
    score >= 0.75 ? 'text-green-600' : score >= 0.5 ? 'text-amber-600' : 'text-red-500';

  return (
    <div className="max-w-2xl mx-auto p-6">
      <Link to="/" className="text-amber-600 hover:underline text-sm mb-4 inline-block">← All Skills</Link>
      <h1 className="text-2xl font-bold text-gray-800 mb-1">{skillName}</h1>
      <p className="text-gray-400 text-sm mb-6">Submit your work for evaluation</p>

      <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow p-5 flex flex-col gap-3 mb-6">
        <input
          className="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-400"
          placeholder="Learner ID (optional)"
          value={learnerId}
          onChange={(e) => setLearnerId(e.target.value)}
        />
        <input
          className="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-400"
          placeholder="Your image URL (publicly accessible)"
          value={imageUrl}
          onChange={(e) => setImageUrl(e.target.value)}
          required
        />
        <input
          className="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-400"
          placeholder="Reference image URL"
          value={referenceUrl}
          onChange={(e) => setReferenceUrl(e.target.value)}
          required
        />
        <button
          type="submit"
          disabled={loading}
          className="bg-teal-600 hover:bg-teal-700 text-white rounded-lg px-4 py-2 text-sm font-medium disabled:opacity-50"
        >
          {loading ? 'Evaluating…' : 'Submit for Evaluation'}
        </button>
        {error && <p className="text-red-500 text-sm">{error}</p>}
      </form>

      {result && (
        <div className="bg-white rounded-xl shadow p-5">
          <h2 className="font-semibold text-gray-700 mb-3">Evaluation Result</h2>
          <p className="text-sm text-gray-500 mb-1">Score</p>
          <p className={`text-3xl font-bold mb-4 ${scoreColor(result.score)}`}>
            {(result.score * 100).toFixed(0)}%
          </p>
          <p className="text-sm text-gray-500 mb-1">Feedback</p>
          <p className="text-sm text-gray-700 mb-5">{result.feedback}</p>
          <button
            onClick={() => navigate(`/evaluation/${result.id}/feedback`)}
            className="bg-amber-600 hover:bg-amber-700 text-white rounded-lg px-4 py-2 text-sm font-medium"
          >
            Get Adaptive Feedback →
          </button>
        </div>
      )}
    </div>
  );
}
