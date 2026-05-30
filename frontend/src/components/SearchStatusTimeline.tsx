import { CheckCircle2, Clock, RefreshCw } from 'lucide-react';

export default function SearchStatusTimeline() {
  return (
    <div className="timeline">
      <span><CheckCircle2 size={16} />Exact dates searched</span>
      <span><CheckCircle2 size={16} />Flexible dates compared</span>
      <span><RefreshCw size={16} />Nearby airports monitored</span>
      <span><Clock size={16} />Next run in up to 6 hours</span>
    </div>
  );
}
