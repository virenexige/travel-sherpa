import { useState } from 'react';
import { NavLink, Route, Routes } from 'react-router-dom';
import { Bell, ChevronLeft, ChevronRight, Compass, LayoutDashboard, PlusCircle, Settings } from 'lucide-react';
import Dashboard from './pages/Dashboard';
import CreateTravelWatch from './pages/CreateTravelWatch';
import TravelWatchDetails from './pages/TravelWatchDetails';
import SettingsPage from './pages/SettingsPage';

export default function App() {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

  return (
    <div className={`app-shell ${sidebarCollapsed ? 'sidebar-collapsed' : ''}`}>
      <aside className="sidebar">
        <div className="sidebar-top">
          <div className="brand">
            <Compass size={28} />
            <div>
              <strong>AI Travel</strong>
              <span>Smart Planner</span>
            </div>
          </div>
          <button className="collapse-button" type="button" aria-label={sidebarCollapsed ? 'Expand menu' : 'Collapse menu'} onClick={() => setSidebarCollapsed(!sidebarCollapsed)}>
            {sidebarCollapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
          </button>
        </div>
        <nav>
          <NavLink to="/" title="Dashboard"><LayoutDashboard size={18} /><span>Dashboard</span></NavLink>
          <NavLink to="/create" title="Create Watch"><PlusCircle size={18} /><span>Create Watch</span></NavLink>
          <NavLink to="/settings" title="Settings"><Settings size={18} /><span>Settings</span></NavLink>
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
