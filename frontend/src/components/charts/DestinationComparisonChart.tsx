import { Bar, BarChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import type { SearchResult } from '../../api/types';

export default function DestinationComparisonChart({ results }: { results: SearchResult[] }) {
  const data = Object.values(results.reduce<Record<string, { destination: string; packagePrice: number }>>((acc, result) => {
    const existing = acc[result.destination];
    if (!existing || result.packagePrice < existing.packagePrice) {
      acc[result.destination] = { destination: result.destination, packagePrice: result.packagePrice };
    }
    return acc;
  }, {})).slice(0, 8);
  return (
    <div className="chart">
      <ResponsiveContainer width="100%" height={260}>
        <BarChart data={data}>
          <XAxis dataKey="destination" />
          <YAxis />
          <Tooltip />
          <Bar dataKey="packagePrice" fill="#2563eb" radius={[5, 5, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
