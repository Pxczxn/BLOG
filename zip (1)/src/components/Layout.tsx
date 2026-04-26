import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';

export default function Layout() {
  return (
    <div className="relative min-h-screen font-sans text-slate-200 bg-[#030014] overflow-x-hidden selection:bg-purple-500/30">
      {/* Starfield Effect Background */}
      <div className="fixed inset-0 pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] w-[120%] h-[120%] bg-[radial-gradient(circle_at_50%_50%,rgba(76,29,149,0.15),transparent_50%)]"></div>
        <div className="absolute top-[20%] right-[10%] w-[300px] h-[300px] bg-purple-600/10 blur-[100px] rounded-full"></div>
        <div className="absolute bottom-[10%] left-[5%] w-[400px] h-[400px] bg-blue-900/20 blur-[120px] rounded-full"></div>
        {/* Static Stars */}
        <div className="absolute inset-0" style={{ backgroundImage: "radial-gradient(white 1px, transparent 0)", backgroundSize: "50px 50px", opacity: 0.1 }}></div>
        <div className="absolute inset-0" style={{ backgroundImage: "radial-gradient(white 1px, transparent 0)", backgroundSize: "130px 130px", opacity: 0.05}}></div>
      </div>
      
      <Navbar />
      
      <main className="relative z-10 pt-16 min-h-[calc(100vh-80px)]">
        <Outlet />
      </main>
      
      <footer className="relative z-10 py-8 text-center mt-12 w-full">
        <p className="text-[10px] text-slate-600 uppercase tracking-[0.2em]">© 2026 开发者博客 • Powered by Starlight Engine</p>
      </footer>
    </div>
  );
}
