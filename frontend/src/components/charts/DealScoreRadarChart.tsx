import { PolarAngleAxis, PolarGrid, Radar, RadarChart, ResponsiveContainer } from 'recharts';
import type { SearchResult } from '../../api/types';

export default function DealScoreRadarChart({ result }: { result: SearchResult | undefined }) {
  const score = result?.dealScore ?? 0;
  const data = [
    { metric: 'Price', value: Math.min(40, score * 0.4) },
    { metric: 'Dates', value: Math.min(15, score * 0.15) },
    { metric: 'Destination', value: Math.min(15, score * 0.15) },
    { metric: 'Convenience', value: Math.min(20, score * 0.2) },
    { metric: 'Hotel', value: Math.min(10, score * 0.1) }
  ];
  return (
    <div className="chart">
      <ResponsiveContainer width="100%" height={260}>
        <RadarChart data={data}>
          <PolarGrid />
          <PolarAngleAxis dataKey="metric" />
          <Radar dataKey="value" stroke="#2563eb" fill="#2563eb" fillOpacity={0.35} />
        </RadarChart>
      </ResponsiveContainer>
    </div>
  );
}
