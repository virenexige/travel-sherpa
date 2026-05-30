import { MapPinned } from 'lucide-react';
import type { SearchResult } from '../api/types';
import DealScoreBadge from './DealScoreBadge';

export default function AlternativeDestinationCard({ result }: { result: SearchResult }) {
  return (
    <article className="alternative-card">
      <MapPinned size={20} />
      <div>
        <h3>{result.destination}</h3>
        <p>{result.departureAirport} to {result.arrivalAirport} · {result.currency} {result.packagePrice}</p>
      </div>
      <DealScoreBadge score={result.dealScore} />
    </article>
  );
}
