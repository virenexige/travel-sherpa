import { FormEvent, useState } from 'react';
import { Bell, CalendarRange, Hotel, MapPin, MessageCircle, Minus, Plane, PlaneTakeoff, Plus, Save, SlidersHorizontal, Sparkles, Star, UsersRound, Wallet, X } from 'lucide-react';
import type { TravelWatchRequest } from '../api/types';

const today = new Date().toISOString().slice(0, 10);
const tripTypes = ['Family + Nature', 'Only Flight', 'Adventure', 'Relaxing', 'City Break', 'Beach', 'Nature'];
const productTypes = [
  ['PACKAGE', 'Packages'],
  ['FLIGHT_ONLY', 'Flights'],
  ['HOTEL_ONLY', 'Hotels']
];
const cabinClasses = [
  ['ECONOMY', 'Economy'],
  ['PREMIUM_ECONOMY', 'Premium economy'],
  ['BUSINESS', 'Business'],
  ['FIRST', 'First']
];

export default function TravelWatchForm({ onSubmit }: { onSubmit: (payload: TravelWatchRequest) => Promise<void> }) {
  const [submitting, setSubmitting] = useState(false);
  const [visibleRangeCount, setVisibleRangeCount] = useState(1);
  const [form, setForm] = useState<TravelWatchRequest>({
    departureLocation: 'London',
    destination: 'Zurich',
    startDate: '2026-08-10',
    endDate: '2026-08-17',
    range2StartDate: '',
    range2EndDate: '',
    range3StartDate: '',
    range3EndDate: '',
    travellers: 3,
    flexibilityDays: 3,
    startDaysEarly: 3,
    startDaysLate: 3,
    finishDaysEarly: 0,
    finishDaysLate: 0,
    durationIncreaseDays: 2,
    tripDurationDays: 7,
    maxBudget: 3000,
    tripType: 'Family + Nature',
    preferredHotelRating: 4,
    travelProductType: 'PACKAGE',
    cabinClass: 'ECONOMY',
    bucketList: false,
    bucketListName: 'Swiss Alps family nature trip',
    earliestStartDate: '2026-07-20',
    latestEndDate: '2026-09-10',
    notes: 'Watch for a good family-friendly hotel and convenient airport.'
  });

  async function submit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    try {
      const maxFlexibility = Math.max(
        form.startDaysEarly,
        form.startDaysLate,
        form.finishDaysEarly,
        form.finishDaysLate,
        form.durationIncreaseDays
      );
      await onSubmit({
        ...form,
        endDate: ensureWindowFits(form.startDate, form.endDate, form.tripDurationDays),
        range2EndDate: visibleRangeCount >= 2 && form.range2StartDate && form.range2EndDate ? ensureWindowFits(form.range2StartDate, form.range2EndDate, form.tripDurationDays) : null,
        range3EndDate: visibleRangeCount >= 3 && form.range3StartDate && form.range3EndDate ? ensureWindowFits(form.range3StartDate, form.range3EndDate, form.tripDurationDays) : null,
        flexibilityDays: maxFlexibility,
        range2StartDate: visibleRangeCount >= 2 && form.range2StartDate ? form.range2StartDate : null,
        range3StartDate: visibleRangeCount >= 3 && form.range3StartDate ? form.range3StartDate : null,
      });
    } finally {
      setSubmitting(false);
    }
  }

  function removeOptionalRange(rangeNumber: 2 | 3) {
    if (rangeNumber === 2) {
      setForm({ ...form, range2StartDate: '', range2EndDate: '', range3StartDate: '', range3EndDate: '' });
      setVisibleRangeCount(1);
      return;
    }
    setForm({ ...form, range3StartDate: '', range3EndDate: '' });
    setVisibleRangeCount(2);
  }

  function updateFlexibility(field: 'startDaysEarly' | 'startDaysLate' | 'finishDaysEarly' | 'finishDaysLate' | 'durationIncreaseDays', value: number) {
    const next = { ...form, [field]: value };
    setForm({
      ...next,
      flexibilityDays: Math.max(
        next.startDaysEarly,
        next.startDaysLate,
        next.finishDaysEarly,
        next.finishDaysLate,
        next.durationIncreaseDays
      )
    });
  }

  function setTripType(tripType: string) {
    setForm({
      ...form,
      tripType,
      bucketListName: `${form.destination} ${tripType.toLowerCase()} trip`
    });
  }

  function selectedValues(value: string) {
    return value.split(',').map(item => item.trim()).filter(Boolean);
  }

  function toggleSelection(field: 'travelProductType' | 'cabinClass', value: string) {
    const current = selectedValues(form[field]);
    const next = current.includes(value) ? current.filter(item => item !== value) : [...current, value];
    if (next.length === 0) {
      return;
    }
    const update: TravelWatchRequest = { ...form, [field]: next.join(',') };
    if (field === 'travelProductType' && value === 'FLIGHT_ONLY' && next.includes('FLIGHT_ONLY')) {
      update.tripType = 'Only Flight';
    }
    setForm(update);
  }

  function productSummary() {
    return selectedValues(form.travelProductType)
      .map(value => productTypes.find(([option]) => option === value)?.[1] ?? value)
      .join(' + ');
  }

  function cabinSummary() {
    return selectedValues(form.cabinClass)
      .map(value => cabinClasses.find(([option]) => option === value)?.[1] ?? value)
      .join(' + ');
  }

  function hasHotelProduct() {
    const products = selectedValues(form.travelProductType);
    return products.includes('PACKAGE') || products.includes('HOTEL_ONLY');
  }

  function dateSummary() {
    const ranges = [
      `${form.tripDurationDays} day trip inside Range 1: ${form.startDate} to ${form.endDate}`,
      visibleRangeCount >= 2 && form.range2StartDate && form.range2EndDate ? `Range 2: ${form.range2StartDate} to ${form.range2EndDate}` : '',
      visibleRangeCount >= 3 && form.range3StartDate && form.range3EndDate ? `Range 3: ${form.range3StartDate} to ${form.range3EndDate}` : ''
    ].filter(Boolean);
    return ranges.join(' · ');
  }

  function addDays(date: string, days: number) {
    const next = new Date(`${date}T00:00:00`);
    next.setDate(next.getDate() + days);
    return next.toISOString().slice(0, 10);
  }

  function ensureWindowFits(startDate: string, endDate: string, tripDurationDays: number) {
    const minimumEnd = addDays(startDate, tripDurationDays);
    return endDate < minimumEnd ? minimumEnd : endDate;
  }

  function updateTripDuration(days: number) {
    const tripDurationDays = Math.max(1, Math.min(60, days));
    setForm({
      ...form,
      tripDurationDays,
      endDate: ensureWindowFits(form.startDate, form.endDate, tripDurationDays),
      range2EndDate: form.range2StartDate && form.range2EndDate ? ensureWindowFits(form.range2StartDate, form.range2EndDate, tripDurationDays) : form.range2EndDate,
      range3EndDate: form.range3StartDate && form.range3EndDate ? ensureWindowFits(form.range3StartDate, form.range3EndDate, tripDurationDays) : form.range3EndDate
    });
  }

  return (
    <form className="planner-experience" onSubmit={submit}>
      <aside className="planner-companion" aria-label="Trip brief">
        <div className="companion-photo" />
        <div className="companion-chat">
          <span className="assistant-avatar"><Sparkles size={18} /></span>
          <div>
            <strong>{form.destination ? `${form.destination} is on the board.` : 'Where should we look?'}</strong>
            <p>I’ll compare route options from {form.departureLocation || 'your departure city'}, then watch for better timing and price drops.</p>
          </div>
        </div>
        <div className="brief-card">
          <span><MapPin size={16} />Route</span>
          <strong>{form.departureLocation || '-'} to {form.destination || '-'}</strong>
        </div>
        <div className="brief-card">
          <span><CalendarRange size={16} />Dates</span>
          <strong>{dateSummary()}</strong>
        </div>
        <div className="brief-grid">
          <div><span>Travellers</span><strong>{form.travellers}</strong></div>
          <div><span>Max budget</span><strong>£{form.maxBudget}</strong></div>
          <div><span>Style</span><strong>{form.tripType}</strong></div>
          <div><span>Flex</span><strong>±{form.flexibilityDays}d</strong></div>
        </div>
        <div className="assistant-prompts">
          <button type="button" onClick={() => setTripType('Family + Nature')}>Family nature escape</button>
          <button type="button" onClick={() => setTripType('City Break')}>Efficient city break</button>
          <button type="button" onClick={() => setForm({ ...form, travelProductType: 'FLIGHT_ONLY', tripType: 'Only Flight' })}>Flights only</button>
        </div>
      </aside>

      <div className="planner-workflow">
      <section className="planner-card">
        <div className="section-title">
          <MessageCircle size={18} />
          <div>
            <span>Trip brief</span>
            <h2>Where should we search?</h2>
          </div>
        </div>
        <div className="smart-grid">
          <label><span><MapPin size={16} />Departure</span><input value={form.departureLocation} onChange={e => setForm({ ...form, departureLocation: e.target.value })} required /></label>
          <label><span><PlaneTakeoff size={16} />Destination</span><input value={form.destination} onChange={e => setForm({ ...form, destination: e.target.value })} required /></label>
        </div>

        <div className="chip-group" aria-label="Trip type">
          {tripTypes.map(type => (
            <button type="button" key={type} className={form.tripType === type ? 'selected' : ''} onClick={() => setTripType(type)}>{type}</button>
          ))}
        </div>
      </section>

      <section className="planner-card">
        <div className="section-title">
          <CalendarRange size={18} />
          <div>
            <span>Date options</span>
            <h2>Pick windows we can compare</h2>
          </div>
          {visibleRangeCount < 3 && (
            <button className="compact-action" type="button" onClick={() => setVisibleRangeCount(visibleRangeCount + 1)}><Plus size={18} />Add range</button>
          )}
        </div>
        <label className="range-control slider-label"><span>Trip length <strong>{form.tripDurationDays} days</strong></span><input type="range" min={1} max={30} value={form.tripDurationDays} onChange={e => updateTripDuration(Number(e.target.value))} /></label>
        <div className="date-ranges">
          <div className="date-range-row">
            <strong>Range 1</strong>
            <label>Window start<input type="date" min={today} value={form.startDate} onChange={e => setForm({ ...form, startDate: e.target.value, endDate: ensureWindowFits(e.target.value, form.endDate, form.tripDurationDays) })} required /></label>
            <label>Window end<input type="date" min={addDays(form.startDate, form.tripDurationDays)} value={form.endDate} onChange={e => setForm({ ...form, endDate: e.target.value })} required /></label>
          </div>
          {visibleRangeCount >= 2 && (
            <div className="date-range-row">
              <strong>Range 2</strong>
              <label>Window start<input type="date" min={today} value={form.range2StartDate ?? ''} onChange={e => setForm({ ...form, range2StartDate: e.target.value, range2EndDate: form.range2EndDate ? ensureWindowFits(e.target.value, form.range2EndDate, form.tripDurationDays) : '' })} required /></label>
              <label>Window end<input type="date" min={form.range2StartDate ? addDays(form.range2StartDate, form.tripDurationDays) : today} value={form.range2EndDate ?? ''} onChange={e => setForm({ ...form, range2EndDate: e.target.value })} required /></label>
              <button className="icon-button" type="button" aria-label="Remove range 2" onClick={() => removeOptionalRange(2)}><X size={18} /></button>
            </div>
          )}
          {visibleRangeCount >= 3 && (
            <div className="date-range-row">
              <strong>Range 3</strong>
              <label>Window start<input type="date" min={today} value={form.range3StartDate ?? ''} onChange={e => setForm({ ...form, range3StartDate: e.target.value, range3EndDate: form.range3EndDate ? ensureWindowFits(e.target.value, form.range3EndDate, form.tripDurationDays) : '' })} required /></label>
              <label>Window end<input type="date" min={form.range3StartDate ? addDays(form.range3StartDate, form.tripDurationDays) : today} value={form.range3EndDate ?? ''} onChange={e => setForm({ ...form, range3EndDate: e.target.value })} required /></label>
              <button className="icon-button" type="button" aria-label="Remove range 3" onClick={() => removeOptionalRange(3)}><X size={18} /></button>
            </div>
          )}
        </div>
      </section>

      <section className="planner-card">
        <div className="section-title">
          <SlidersHorizontal size={18} />
          <div>
            <span>Flexibility</span>
            <h2>How much can plans move?</h2>
          </div>
        </div>
        <div className="flex-grid">
          <label className="range-control"><span>Can start days early <strong>{form.startDaysEarly}d</strong></span><input type="range" min={0} max={14} value={form.startDaysEarly} onChange={e => updateFlexibility('startDaysEarly', Number(e.target.value))} /></label>
          <label className="range-control"><span>Can start days later <strong>{form.startDaysLate}d</strong></span><input type="range" min={0} max={14} value={form.startDaysLate} onChange={e => updateFlexibility('startDaysLate', Number(e.target.value))} /></label>
          <label className="range-control"><span>Can finish days early <strong>{form.finishDaysEarly}d</strong></span><input type="range" min={0} max={14} value={form.finishDaysEarly} onChange={e => updateFlexibility('finishDaysEarly', Number(e.target.value))} /></label>
          <label className="range-control"><span>Can finish days later <strong>{form.finishDaysLate}d</strong></span><input type="range" min={0} max={14} value={form.finishDaysLate} onChange={e => updateFlexibility('finishDaysLate', Number(e.target.value))} /></label>
        </div>
        <label className="range-control slider-label"><span>Increase duration by up to <strong>{form.durationIncreaseDays}d</strong></span><input type="range" min={0} max={14} value={form.durationIncreaseDays} onChange={e => updateFlexibility('durationIncreaseDays', Number(e.target.value))} /></label>
      </section>

      <section className="planner-card">
        <div className="section-title">
          <UsersRound size={18} />
          <div>
            <span>Preferences</span>
            <h2>Personalize the search</h2>
          </div>
        </div>
        <div className="smart-grid">
          <div className="stepper-control">
            <span><UsersRound size={16} />Travellers</span>
            <div>
              <button type="button" aria-label="Decrease travellers" onClick={() => setForm({ ...form, travellers: Math.max(1, form.travellers - 1) })}><Minus size={16} /></button>
              <strong>{form.travellers}</strong>
              <button type="button" aria-label="Increase travellers" onClick={() => setForm({ ...form, travellers: Math.min(12, form.travellers + 1) })}><Plus size={16} /></button>
            </div>
          </div>
          <label className="range-control"><span><span><Wallet size={16} />Max budget</span><strong>£{form.maxBudget}</strong></span><input type="range" min={500} max={10000} step={100} value={form.maxBudget} onChange={e => setForm({ ...form, maxBudget: Number(e.target.value) })} /></label>
        </div>
        {hasHotelProduct() && (
          <div className="preference-block">
            <span><Hotel size={16} />Hotel rating</span>
            <div className="star-rating" aria-label="Hotel rating">
              {[1, 2, 3, 4, 5].map(rating => (
                <button type="button" key={rating} className={rating <= form.preferredHotelRating ? 'selected' : ''} aria-label={`${rating} star hotel rating`} onClick={() => setForm({ ...form, preferredHotelRating: rating })}><Star size={20} fill="currentColor" /></button>
              ))}
            </div>
          </div>
        )}
        <div className="preference-block">
          <span><Plane size={16} />Search products: {productSummary()}</span>
          <div className="segmented multi-select">
            {productTypes.map(([value, label]) => (
              <button type="button" key={value} className={selectedValues(form.travelProductType).includes(value) ? 'selected' : ''} onClick={() => toggleSelection('travelProductType', value)}>{label}</button>
            ))}
          </div>
        </div>
        <div className="preference-block">
          <span><Plane size={16} />Cabin classes: {cabinSummary()}</span>
          <div className="chip-group multi-select" aria-label="Flight class">
          {cabinClasses.map(([value, label]) => (
            <button type="button" key={value} className={selectedValues(form.cabinClass).includes(value) ? 'selected' : ''} onClick={() => toggleSelection('cabinClass', value)}>{label}</button>
          ))}
          </div>
        </div>
      </section>

      <section className="planner-card">
        <label className="checkbox"><input type="checkbox" checked={form.bucketList} onChange={e => setForm({ ...form, bucketList: e.target.checked })} /><span><Bell size={16} />Add to bucket list and monitor daily</span></label>
        {form.bucketList && (
          <div className="smart-grid">
            <label>Bucket list name<input value={form.bucketListName} onChange={e => setForm({ ...form, bucketListName: e.target.value })} /></label>
            <label>Earliest travel date<input type="date" min={today} value={form.earliestStartDate} onChange={e => setForm({ ...form, earliestStartDate: e.target.value })} /></label>
            <label>Latest return date<input type="date" min={form.earliestStartDate} value={form.latestEndDate} onChange={e => setForm({ ...form, latestEndDate: e.target.value })} /></label>
            <label>Bucket list notes<input value={form.notes} onChange={e => setForm({ ...form, notes: e.target.value })} /></label>
          </div>
        )}
      </section>

      <div className="planner-submit">
        <button className="primary" disabled={submitting}><Save size={18} />{submitting ? 'Creating...' : 'Create and search'}</button>
        <p className="form-note"><PlaneTakeoff size={16} />Uses permitted provider adapters only. The app records provider queries; no website scraping is performed.</p>
      </div>
      </div>
    </form>
  );
}
