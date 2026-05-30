import { useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Pause, Play, RefreshCw, Trash2 } from 'lucide-react';
import { api } from '../api/client';
import AlternativeDestinationCard from '../components/AlternativeDestinationCard';
import HistoricalComparisonPanel from '../components/HistoricalComparisonPanel';
import PriceComparisonTable from '../components/PriceComparisonTable';
import RecommendationCard from '../components/RecommendationCard';
import SearchActivityLogPanel from '../components/SearchActivityLogPanel';
import SearchStatusTimeline from '../components/SearchStatusTimeline';
import { EmptyState, ErrorState, LoadingState } from '../components/StateBlock';
import DestinationComparisonChart from '../components/charts/DestinationComparisonChart';
import DealScoreRadarChart from '../components/charts/DealScoreRadarChart';
import FlightHotelStackedChart from '../components/charts/FlightHotelStackedChart';
import PriceHistoryChart from '../components/charts/PriceHistoryChart';
import { useAsync } from '../hooks/useAsync';

export default function TravelWatchDetails() {
  const { id } = useParams<{ id: string }>();
  const [busy, setBusy] = useState(false);
  const watch = useAsync(() => api.getWatch(id!), [id]);
  const results = useAsync(() => api.results(id!), [id]);
  const history = useAsync(() => api.priceHistory(id!), [id]);
  const recommendations = useAsync(() => api.recommendations(id!), [id]);
  const searchLogs = useAsync(() => api.searchLogs(id!), [id]);
  const mcpContext = useAsync(() => api.mcpContext(id!), [id]);
  const best = useMemo(() => [...(results.data ?? [])].sort((a, b) => a.packagePrice - b.packagePrice)[0], [results.data]);
  const alternatives = useMemo(() => (results.data ?? []).filter(result => result.destination !== watch.data?.destination).slice(0, 4), [results.data, watch.data]);
  const dateRanges = useMemo(() => {
    if (!watch.data) return [];
    return [
      ['Range 1', watch.data.startDate, watch.data.endDate],
      ['Range 2', watch.data.range2StartDate, watch.data.range2EndDate],
      ['Range 3', watch.data.range3StartDate, watch.data.range3EndDate]
    ].filter(([, start, end]) => start && end);
  }, [watch.data]);

  if (watch.loading) return <LoadingState />;
  if (watch.error) return <ErrorState message={watch.error} />;
  if (!watch.data) return <EmptyState title="Watch not found" detail="This travel watch may have been deleted." />;

  function formatCsv(value: string) {
    return value.split(',')
      .map(item => item.trim().replace('_', ' ').toLowerCase())
      .filter(Boolean)
      .map(item => item.charAt(0).toUpperCase() + item.slice(1))
      .join(' + ');
  }

  async function runSearch() {
    setBusy(true);
    try {
      await api.searchNow(id!);
      await Promise.all([results.reload(), history.reload(), recommendations.reload()]);
    } finally {
      setBusy(false);
    }
  }

  async function toggleStatus() {
    setBusy(true);
    try {
      if (watch.data?.status === 'ACTIVE') await api.pauseWatch(id!);
      else await api.resumeWatch(id!);
      await watch.reload();
    } finally {
      setBusy(false);
    }
  }

  async function remove() {
    setBusy(true);
    await api.deleteWatch(id!);
    window.location.href = '/';
  }

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <span className="eyebrow">{watch.data.status}</span>
          <h1>{watch.data.departureLocation} to {watch.data.destination}</h1>
          <p>{dateRanges.map(([label, start, end]) => `${label}: ${start} window ${end}`).join(' · ')} · {watch.data.tripDurationDays} day trip · start {watch.data.startDaysEarly} days early / {watch.data.startDaysLate} days later · finish {watch.data.finishDaysEarly} days early / {watch.data.finishDaysLate} days later · duration +{watch.data.durationIncreaseDays} days · {watch.data.travellers} travellers · max budget £{watch.data.maxBudget} · {watch.data.tripType} · {formatCsv(watch.data.travelProductType)} · {formatCsv(watch.data.cabinClass)}{watch.data.bucketList ? ` · Bucket list: ${watch.data.bucketListName || watch.data.destination}` : ''}</p>
        </div>
        <div className="actions">
          <button onClick={runSearch} disabled={busy}><RefreshCw size={18} />Search Now</button>
          <button onClick={toggleStatus} disabled={busy}>{watch.data.status === 'ACTIVE' ? <Pause size={18} /> : <Play size={18} />}{watch.data.status === 'ACTIVE' ? 'Pause' : 'Resume'}</button>
          <button className="danger" onClick={remove} disabled={busy}><Trash2 size={18} />Delete</button>
        </div>
      </header>

      <section className="metric-grid">
        <div className="metric"><span>Current best price</span><strong>{best ? `${best.currency} ${best.packagePrice}` : '-'}</strong></div>
        <div className="metric"><span>Lowest historical price</span><strong>{history.data?.length ? `£${Math.min(...history.data.map(item => item.packagePrice))}` : '-'}</strong></div>
        <div className="metric"><span>Best deal score</span><strong>{best ? `${best.dealScore}/100` : '-'}</strong></div>
        <div className="metric"><span>Selected classes</span><strong>{formatCsv(watch.data.cabinClass)}</strong></div>
      </section>

      {watch.data.bucketList && (
        <section className="panel accent-panel">
          <h2>Bucket List Monitoring</h2>
          <p>Daily search is enabled for this planned trip. The app keeps comparing flights and hotels against historical prices and will surface stronger booking windows when deals improve.</p>
          <p className="muted">{watch.data.notes}</p>
        </section>
      )}

      <SearchStatusTimeline />

      <section className="panel">
        <h2>MCP Destination Details</h2>
        {mcpContext.loading && <LoadingState label="Checking MCP context" />}
        {mcpContext.data?.available ? (
          <p className="muted">{mcpContext.data.context}</p>
        ) : (
          <EmptyState title="No MCP context configured" detail="Enable Spring AI MCP and connect a travel/place MCP server to enrich this trip with live destination details." />
        )}
      </section>

      <section className="panel">
        <h2>AI Recommendations</h2>
        {recommendations.loading && <LoadingState label="Loading recommendations" />}
        {recommendations.data?.length ? recommendations.data.map(item => <RecommendationCard key={item.id} recommendation={item} />) : <EmptyState title="No recommendation yet" detail="Run a search to generate an AI-style recommendation." />}
      </section>

      <section className="split">
        <div className="panel"><h2>Price History</h2><PriceHistoryChart history={history.data ?? []} /></div>
        <div className="panel"><h2>Destination Comparison</h2><DestinationComparisonChart results={results.data ?? []} /></div>
        <div className="panel"><h2>Flight vs Hotel</h2><FlightHotelStackedChart results={results.data ?? []} /></div>
        <div className="panel"><h2>Deal Score</h2><DealScoreRadarChart result={best} /></div>
      </section>

      <HistoricalComparisonPanel history={history.data ?? []} best={best} />
      <SearchActivityLogPanel logs={searchLogs.data ?? []} />

      <section className="panel">
        <h2>Alternative Destinations</h2>
        <div className="alternative-grid">
          {alternatives.map(result => <AlternativeDestinationCard key={result.id} result={result} />)}
        </div>
      </section>

      <section className="panel">
        <h2>Search Results</h2>
        {results.loading && <LoadingState label="Loading results" />}
        {results.error && <ErrorState message={results.error} />}
        {results.data?.length ? <PriceComparisonTable results={results.data} /> : <EmptyState title="No results yet" detail="Run an immediate search to populate comparison data." />}
      </section>
    </div>
  );
}
