import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getSkills, createSkill, type Skill } from '../api/client';

export default function SkillsPage() {
  const [skills, setSkills] = useState<Skill[]>([]);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchSkills = () => getSkills().then(setSkills).catch(() => setError('Failed to load skills.'));

  useEffect(() => { fetchSkills(); }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    setLoading(true);
    setError('');
    try {
      await createSkill(name.trim(), description.trim());
      setName('');
      setDescription('');
      fetchSkills();
    } catch {
      setError('Failed to create skill.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-6">
      <h1 className="text-3xl font-bold text-amber-700 mb-2">Heritage Sage</h1>
      <p className="text-gray-500 mb-8">Reviving lost traditional skills through AI-powered learning.</p>

      <h2 className="text-xl font-semibold mb-4">Add a Skill</h2>
      <form onSubmit={handleCreate} className="bg-white rounded-xl shadow p-4 mb-8 flex flex-col gap-3">
        <input
          className="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
          placeholder="Skill name (e.g. Calligraphy)"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <textarea
          className="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
          placeholder="Short description (optional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          rows={2}
        />
        <button
          type="submit"
          disabled={loading}
          className="bg-amber-600 hover:bg-amber-700 text-white rounded-lg px-4 py-2 text-sm font-medium disabled:opacity-50"
        >
          {loading ? 'Adding…' : 'Add Skill'}
        </button>
        {error && <p className="text-red-500 text-sm">{error}</p>}
      </form>

      <h2 className="text-xl font-semibold mb-4">Available Skills</h2>
      {skills.length === 0 ? (
        <p className="text-gray-400 text-sm">No skills yet. Add one above.</p>
      ) : (
        <ul className="flex flex-col gap-3">
          {skills.map((skill) => (
            <li key={skill.id} className="bg-white rounded-xl shadow p-4 flex items-center justify-between">
              <div>
                <p className="font-semibold text-gray-800">{skill.name}</p>
                {skill.description && <p className="text-gray-400 text-sm">{skill.description}</p>}
              </div>
              <div className="flex gap-2">
                <Link
                  to={`/skills/${encodeURIComponent(skill.name)}/lesson`}
                  className="bg-amber-100 hover:bg-amber-200 text-amber-800 text-sm rounded-lg px-3 py-1.5"
                >
                  Learn
                </Link>
                <Link
                  to={`/skills/${encodeURIComponent(skill.name)}/evaluate`}
                  className="bg-teal-100 hover:bg-teal-200 text-teal-800 text-sm rounded-lg px-3 py-1.5"
                >
                  Evaluate
                </Link>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
