export default function DealScoreBadge({ score }: { score: number }) {
  const level = score >= 85 ? 'excellent' : score >= 70 ? 'good' : 'watch';
  return <span className={`deal-score ${level}`}>{score}/100</span>;
}
