import { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';
import { fetchSiteSettings, readSiteSettings } from '../lib/siteSettings';

export default function Layout() {
  const [settings, setSettings] = useState(readSiteSettings());

  useEffect(() => {
    fetchSiteSettings().then(setSettings).catch(() => {});
  }, []);

  return (
    <div className="relative min-h-screen overflow-x-hidden bg-[#030014] font-sans text-slate-200 selection:bg-purple-500/30">
      <div className="fixed inset-0 pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] h-[120%] w-[120%] bg-[radial-gradient(circle_at_50%_50%,rgba(76,29,149,0.15),transparent_50%)]" />
        <div className="absolute top-[18%] right-[8%] h-[320px] w-[320px] rounded-full bg-purple-600/10 blur-[120px]" />
        <div className="absolute bottom-[8%] left-[4%] h-[420px] w-[420px] rounded-full bg-blue-900/20 blur-[140px]" />
        <div
          className="absolute inset-0 opacity-10"
          style={{ backgroundImage: 'radial-gradient(white 1px, transparent 0)', backgroundSize: '50px 50px' }}
        />
        <div
          className="absolute inset-0 opacity-5"
          style={{ backgroundImage: 'radial-gradient(white 1px, transparent 0)', backgroundSize: '130px 130px' }}
        />
      </div>

      <Navbar />

      <main className="relative z-10 min-h-[calc(100vh-80px)] pt-16">
        <div className="mx-auto w-full max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
          <Outlet />
        </div>
      </main>

      <footer className="relative z-10 mt-12 py-8 text-center">
        <p className="text-[10px] uppercase tracking-[0.2em] text-slate-600">
          &copy; 2026 {settings.siteName} · Powered by Starlight Engine
        </p>
      </footer>
    </div>
  );
}
