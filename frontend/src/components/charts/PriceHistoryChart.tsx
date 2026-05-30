import { Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import type { PriceHistoryPoint } from '../../api/types';

export default function PriceHistoryChart({ history }: { history: PriceHistoryPoint[] }) {
  const data = history.map(point => ({
    date: new Date(point.searchedAt).toLocaleDateString(),
    packagePrice: point.packagePrice
  }));
  return (
    <div className="chart">
      <ResponsiveContainer width="100%" height={260}>
        <LineChart data={data}>
          <XAxis dataKey="date" />
          <YAxis />
          <Tooltip />
          <Line type="monotone" dataKey="packagePrice" stroke="#2563eb" strokeWidth={3} dot={false} />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
