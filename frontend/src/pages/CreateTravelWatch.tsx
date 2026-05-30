import { useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import type { TravelWatchRequest } from '../api/types';
import TravelWatchForm from '../components/TravelWatchForm';

export default function CreateTravelWatch() {
  const navigate = useNavigate();
  async function create(payload: TravelWatchRequest) {
    const watch = await api.createWatch(payload);
    navigate(`/watches/${watch.id}`);
  }

  return (
    <div className="page create-page">
      <header className="page-header create-header">
        <div>
          <span className="eyebrow">New search</span>
          <h1>Plan a smarter trip</h1>
          <p>Build a travel watch that searches flexible dates, nearby airports, and personalized alternatives.</p>
        </div>
      </header>
      <TravelWatchForm onSubmit={create} />
    </div>
  );
}
