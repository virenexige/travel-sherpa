import { FormEvent, useState } from 'react';
import { PlaneTakeoff, Save } from 'lucide-react';
import type { TravelWatchRequest } from '../api/types';

const today = new Date().toISOString().slice(0, 10);

export default function TravelWatchForm({ onSubmit }: { onSubmit: (payload: TravelWatchRequest) => Promise<void> }) {
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState<TravelWatchRequest>({
    departureLocation: 'London',
    destination: 'Zurich',
    startDate: '2026-08-10',
    endDate: '2026-08-17',
    travellers: 3,
    flexibilityDays: 3,
    maxBudget: 3000,
    tripType: 'Family + Nature',
    preferredHotelRating: 4
  });

  async function submit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    try {
      await onSubmit(form);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="form-grid" onSubmit={submit}>
      <label>Departure city or airport<input value={form.departureLocation} onChange={e => setForm({ ...form, departureLocation: e.target.value })} required /></label>
      <label>Destination<input value={form.destination} onChange={e => setForm({ ...form, destination: e.target.value })} required /></label>
      <label>Start date<input type="date" min={today} value={form.startDate} onChange={e => setForm({ ...form, startDate: e.target.value })} required /></label>
      <label>End date<input type="date" min={form.startDate} value={form.endDate} onChange={e => setForm({ ...form, endDate: e.target.value })} required /></label>
      <label>Travellers<input type="number" min={1} max={12} value={form.travellers} onChange={e => setForm({ ...form, travellers: Number(e.target.value) })} /></label>
      <label>Flexibility<input type="number" min={0} max={14} value={form.flexibilityDays} onChange={e => setForm({ ...form, flexibilityDays: Number(e.target.value) })} /></label>
      <label>Budget<input type="number" min={0} value={form.maxBudget} onChange={e => setForm({ ...form, maxBudget: Number(e.target.value) })} /></label>
      <label>Hotel rating<input type="number" min={1} max={5} value={form.preferredHotelRating} onChange={e => setForm({ ...form, preferredHotelRating: Number(e.target.value) })} /></label>
      <label className="wide">Trip type<select value={form.tripType} onChange={e => setForm({ ...form, tripType: e.target.value })}>
        <option>Family + Nature</option>
        <option>Adventure</option>
        <option>Relaxing</option>
        <option>City Break</option>
        <option>Beach</option>
        <option>Nature</option>
      </select></label>
      <button className="primary wide" disabled={submitting}><Save size={18} />{submitting ? 'Creating...' : 'Create and search'}</button>
      <p className="form-note wide"><PlaneTakeoff size={16} />The first search runs immediately using permitted provider adapters.</p>
    </form>
  );
}
