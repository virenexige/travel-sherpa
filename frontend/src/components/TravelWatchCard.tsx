import { Link } from 'react-router-dom';
import { CalendarDays, MapPin, Users } from 'lucide-react';
import type { TravelWatch } from '../api/types';

export default function TravelWatchCard({ watch }: { watch: TravelWatch }) {
  return (
    <Link className="watch-card" to={`/watches/${watch.id}`}>
      <div>
        <span className={`status ${watch.status.toLowerCase()}`}>{watch.status}</span>
        <h3>{watch.departureLocation} to {watch.destination}</h3>
      </div>
      <div className="watch-meta">
        <span><CalendarDays size={16} />{watch.startDate} to {watch.endDate}</span>
        <span><Users size={16} />{watch.travellers} travellers</span>
        <span><MapPin size={16} />{watch.tripType}</span>
      </div>
    </Link>
  );
}
