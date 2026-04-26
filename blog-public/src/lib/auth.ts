/**
 * 管理员 Token 存储工具
 */
export const ADMIN_TOKEN_KEY = 'admin_token';

// 获取管理员Token
export const getAdminToken = () => localStorage.getItem(ADMIN_TOKEN_KEY);

// 设置管理员Token
export const setAdminToken = (token: string | null) => {
  if (token) {
    localStorage.setItem(ADMIN_TOKEN_KEY, token);
    return;
  }
  localStorage.removeItem(ADMIN_TOKEN_KEY);
};

// 清除管理员Token
export const clearAdminToken = () => {
  localStorage.removeItem(ADMIN_TOKEN_KEY);
};
