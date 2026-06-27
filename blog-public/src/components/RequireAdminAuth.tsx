import { useEffect, useState } from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { getAdminToken } from '../lib/auth';
import { refreshAdminSession } from '../lib/request';

export default function RequireAdminAuth() {
  const location = useLocation();
  const [checking, setChecking] = useState(!getAdminToken());
  const [authenticated, setAuthenticated] = useState(Boolean(getAdminToken()));

  useEffect(() => {
    let mounted = true;
    if (getAdminToken()) {
      setAuthenticated(true);
      setChecking(false);
      return;
    }

    refreshAdminSession().then((token) => {
      if (!mounted) return;
      setAuthenticated(Boolean(token));
      setChecking(false);
    });

    return () => {
      mounted = false;
    };
  }, []);

  if (checking) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-950 text-slate-300">
        <div className="h-8 w-8 animate-spin rounded-full border-2 border-slate-600 border-t-white" />
      </div>
    );
  }

  if (!authenticated) {
    return <Navigate to="/admin-pxczxn/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
}
