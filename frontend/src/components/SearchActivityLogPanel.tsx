import { SearchCheck } from 'lucide-react';
import type { SearchActivityLog } from '../api/types';
import { EmptyState } from './StateBlock';

export default function SearchActivityLogPanel({ logs }: { logs: SearchActivityLog[] }) {
  const visibleLogs = [...logs].sort((a, b) => {
    if (a.searchType === 'PROVIDER_SIGNAL' && b.searchType !== 'PROVIDER_SIGNAL') return -1;
    if (a.searchType !== 'PROVIDER_SIGNAL' && b.searchType === 'PROVIDER_SIGNAL') return 1;
    return new Date(b.searchedAt).getTime() - new Date(a.searchedAt).getTime();
  }).slice(0, 12);

  return (
    <section className="panel">
      <h2><SearchCheck size={20} />Provider Search Log</h2>
      {!logs.length ? (
        <EmptyState title="No provider log yet" detail="Run a search to see provider adapters, criteria, and returned pricing." />
      ) : (
        <div className="log-list">
          {visibleLogs.map(log => (
            <article className="log-row" key={log.id}>
              <div>
                <strong>{log.providerName}</strong>
                <span>{log.searchType} · {log.departureAirport} to {log.arrivalAirport} · {log.startDate} to {log.endDate}</span>
              </div>
              <p>{log.message}</p>
              <span className="log-price">{log.cheapestPackagePrice ? `${log.currency} ${log.cheapestPackagePrice}` : 'No offers'}</span>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
