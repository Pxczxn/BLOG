



import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { getAdminToken } from './storage';






const RequireAdminAuth = () => {
  const location = useLocation();

  
  if (!getAdminToken()) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
};

export default RequireAdminAuth;

