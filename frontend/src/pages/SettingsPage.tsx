import AlertPreferencePanel from '../components/AlertPreferencePanel';

export default function SettingsPage() {
  return (
    <div className="page narrow">
      <header className="page-header">
        <div>
          <span className="eyebrow">Account</span>
          <h1>Settings</h1>
        </div>
      </header>
      <AlertPreferencePanel />
      <section className="panel">
        <h2>Provider configuration</h2>
        <p className="muted">Real provider adapters are enabled from backend environment variables after API access is approved.</p>
      </section>
    </div>
  );
}
