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
        <p className="muted">MockTravel is the local development provider. It exists so searches, history, alerts, and recommendations work without using scraped data.</p>
        <div className="provider-notes">
          <div>
            <strong>SerpApi Google Flights</strong>
            <span>Enabled as an exact-route fare lookup provider when `SERPAPI_API_KEY` is set. The app caps it to one exact fare lookup per search run and a configurable monthly safety cap, defaulting to 200 under the 250/month allowance.</span>
          </div>
          <div>
            <strong>Aviationstack</strong>
            <span>Enabled as a schedule/status signal provider, not a fare-pricing source. The app limits it to one exact-route lookup per search run and caches route signals to protect the 100/month allowance.</span>
          </div>
          <div>
            <strong>Skyscanner</strong>
            <span>Use the official partner API with `SKYSCANNER_API_KEY` and `app.providers.skyscanner.enabled=true` after access is approved.</span>
          </div>
          <div>
            <strong>Google Flights</strong>
            <span>Not enabled for price search because there is no supported public Google Flights pricing API. Do not scrape or call reverse-engineered internal endpoints.</span>
          </div>
          <div>
            <strong>Other providers</strong>
            <span>Amadeus, Booking.com, Expedia, Kiwi, and similar sources should be integrated only through official, partner, affiliate, public, or permitted APIs.</span>
          </div>
        </div>
      </section>
    </div>
  );
}
