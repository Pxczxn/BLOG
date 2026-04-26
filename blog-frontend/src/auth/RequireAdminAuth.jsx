/**
 * 管理员路由守卫组件
 * 检查用户是否已登录（token 是否存在），未登录则重定向到登录页。
 */
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { getAdminToken } from './storage';

/**
 * 路由守卫组件
 * 验证用户登录状态，未登录时重定向至登录页并记录来源路径
 * @returns {JSX.Element} 已登录返回子路由出口，未登录返回重定向组件
 */
const RequireAdminAuth = () => {
  const location = useLocation();

  // 检查 Token 是否存在，不存在则跳转登录页
  if (!getAdminToken()) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
};

export default RequireAdminAuth;

