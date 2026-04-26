



import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { getAdminToken } from '../lib/auth';

export default function RequireAdminAuth() {
  const location = useLocation();
  const token = getAdminToken();

  if (!token) {
    return <Navigate to="/admin-pxczxn/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
}
