import { Sparkles } from 'lucide-react';
import type { Recommendation } from '../api/types';

export default function RecommendationCard({ recommendation }: { recommendation: Recommendation }) {
  return (
    <article className="recommendation-card">
      <div className="recommendation-title">
        <Sparkles size={20} />
        <h3>{recommendation.title}</h3>
      </div>
      <p>{recommendation.explanation}</p>
      <div className="recommendation-metrics">
        <span>{Math.round(recommendation.confidenceScore * 100)}% confidence</span>
        <span>Saving £{recommendation.estimatedSaving}</span>
      </div>
    </article>
  );
}
