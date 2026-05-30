import { NavLink, Route, Routes } from 'react-router-dom';
import { Bell, Compass, LayoutDashboard, PlusCircle, Settings } from 'lucide-react';
import Dashboard from './pages/Dashboard';
import CreateTravelWatch from './pages/CreateTravelWatch';
import TravelWatchDetails from './pages/TravelWatchDetails';
import SettingsPage from './pages/SettingsPage';

export default function App() {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <Compass size={28} />
          <div>
            <strong>AI Travel</strong>
            <span>Smart Planner</span>
          </div>
        </div>
        <nav>
          <NavLink to="/"><LayoutDashboard size={18} />Dashboard</NavLink>
          <NavLink to="/create"><PlusCircle size={18} />Create Watch</NavLink>
          <NavLink to="/settings"><Settings size={18} />Settings</NavLink>
        </nav>
        <div className="sidebar-alert">
          <Bell size={18} />
          <span>Price checks run every 6 hours.</span>
        </div>
      </aside>
      <main className="content">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/create" element={<CreateTravelWatch />} />
          <Route path="/watches/:id/*" element={<TravelWatchDetails />} />
          <Route path="/settings" element={<SettingsPage />} />
        </Routes>
      </main>
    </div>
  );
}
