import { Link } from 'react-router-dom';
import { CalendarDays, MapPin, Users } from 'lucide-react';
import type { TravelWatch } from '../api/types';

export default function TravelWatchCard({ watch }: { watch: TravelWatch }) {
  const ranges = [
    ['R1', watch.startDate, watch.endDate],
    ['R2', watch.range2StartDate, watch.range2EndDate],
    ['R3', watch.range3StartDate, watch.range3EndDate]
  ].filter(([, start, end]) => start && end);

  return (
    <Link className="watch-card" to={`/watches/${watch.id}`}>
      <div>
        <span className={`status ${watch.status.toLowerCase()}`}>{watch.status}</span>
        <h3>{watch.departureLocation} to {watch.destination}</h3>
      </div>
      <div className="watch-meta">
        <span><CalendarDays size={16} />{ranges.map(([label, start, end]) => `${label}: ${start} to ${end}`).join(' · ')}</span>
        <span><Users size={16} />{watch.travellers} travellers</span>
        <span><MapPin size={16} />{watch.tripType}</span>
      </div>
    </Link>
  );
}
