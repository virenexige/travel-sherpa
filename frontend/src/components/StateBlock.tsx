export function LoadingState({ label = 'Loading travel data' }: { label?: string }) {
  return <div className="state-block">{label}...</div>;
}

export function ErrorState({ message }: { message: string }) {
  return <div className="state-block error">{message}</div>;
}

export function EmptyState({ title, detail }: { title: string; detail: string }) {
  return (
    <div className="state-block">
      <strong>{title}</strong>
      <span>{detail}</span>
    </div>
  );
}
