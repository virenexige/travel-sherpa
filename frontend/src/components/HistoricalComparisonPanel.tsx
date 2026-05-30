import type { PriceHistoryPoint, SearchResult } from '../api/types';
import { EmptyState } from './StateBlock';

export default function HistoricalComparisonPanel({ history, best }: { history: PriceHistoryPoint[]; best: SearchResult | undefined }) {
  if (!history.length || !best) {
    return (
      <section className="panel">
        <h2>Historical Comparison</h2>
        <EmptyState title="No history yet" detail="Search results will build a price history for trend comparison." />
      </section>
    );
  }
  const latest = history[history.length - 1];
  const lowest = history.reduce((min, point) => point.packagePrice < min.packagePrice ? point : min, history[0]);
  const average = history.reduce((sum, point) => sum + point.packagePrice, 0) / history.length;
  return (
    <section className="panel">
      <h2>Historical Comparison</h2>
      <div className="comparison-grid">
        <div><span>Current best</span><strong>{best.currency} {best.packagePrice}</strong></div>
        <div><span>Latest checked</span><strong>{latest.currency} {latest.packagePrice}</strong></div>
        <div><span>Historical low</span><strong>{lowest.currency} {lowest.packagePrice}</strong></div>
        <div><span>Average observed</span><strong>{latest.currency} {Math.round(average)}</strong></div>
      </div>
    </section>
  );
}
