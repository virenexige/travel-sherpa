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
    <div className="page narrow">
      <header className="page-header">
        <div>
          <span className="eyebrow">New search</span>
          <h1>Create Travel Watch</h1>
        </div>
      </header>
      <section className="panel">
        <TravelWatchForm onSubmit={create} />
      </section>
    </div>
  );
}
