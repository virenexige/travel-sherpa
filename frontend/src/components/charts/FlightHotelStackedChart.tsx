import { Bar, BarChart, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import type { SearchResult } from '../../api/types';

export default function FlightHotelStackedChart({ results }: { results: SearchResult[] }) {
  const data = results.slice(0, 8).map(result => ({
    route: `${result.destination} ${result.departureAirport}`,
    flight: result.flightPrice,
    hotel: result.hotelPrice
  }));
  return (
    <div className="chart">
      <ResponsiveContainer width="100%" height={260}>
        <BarChart data={data}>
          <XAxis dataKey="route" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Bar dataKey="flight" stackId="cost" fill="#2563eb" />
          <Bar dataKey="hotel" stackId="cost" fill="#f59e0b" radius={[5, 5, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
