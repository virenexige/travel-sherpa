import { Link } from 'react-router-dom';
import { PlusCircle } from 'lucide-react';
import { api } from '../api/client';
import TravelWatchCard from '../components/TravelWatchCard';
import { EmptyState, ErrorState, LoadingState } from '../components/StateBlock';
import { useAsync } from '../hooks/useAsync';

export default function Dashboard() {
  const { data: watches, loading, error } = useAsync(api.listWatches, []);
  if (loading) return <LoadingState />;
  if (error) return <ErrorState message={error} />;

  const active = watches?.filter(watch => watch.status === 'ACTIVE') ?? [];
  const best = watches?.[0];

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <span className="eyebrow">Travel intelligence</span>
          <h1>Dashboard</h1>
        </div>
        <Link className="primary" to="/create"><PlusCircle size={18} />New watch</Link>
      </header>
      <section className="metric-grid">
        <div className="metric"><span>Active watches</span><strong>{active.length}</strong></div>
        <div className="metric"><span>Best current deal</span><strong>{best ? best.destination : '-'}</strong></div>
        <div className="metric"><span>Latest price drop</span><strong>Monitoring</strong></div>
        <div className="metric"><span>Next scheduled search</span><strong>Within 6h</strong></div>
      </section>
      <section className="panel">
        <h2>Travel watches</h2>
        {!watches?.length ? (
          <EmptyState title="No travel watches yet" detail="Create a watch to start comparing dates, airports, and nearby destinations." />
        ) : (
          <div className="watch-grid">{watches.map(watch => <TravelWatchCard key={watch.id} watch={watch} />)}</div>
        )}
      </section>
    </div>
  );
}
