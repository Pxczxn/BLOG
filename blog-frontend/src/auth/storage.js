/**
 * 管理员 Token 存储工具
 * 封装 localStorage 对管理员 JWT Token 的读取、设置和清除操作。
 */

/** Token 在 localStorage 中的键名 */
export const ADMIN_TOKEN_KEY = 'token';

/**
 * 获取管理员 Token
 * @returns {string|null} 返回存储的 Token，不存在则返回 null
 */
export const getAdminToken = () => localStorage.getItem(ADMIN_TOKEN_KEY);

/**
 * 设置管理员 Token
 * @param {string} token - 要存储的 Token 值，若为空则移除 Token
 */
export const setAdminToken = (token) => {
  if (token) {
    localStorage.setItem(ADMIN_TOKEN_KEY, token);
    return;
  }

  localStorage.removeItem(ADMIN_TOKEN_KEY);
};

/**
 * 清除管理员 Token
 * 从 localStorage 中移除存储的 Token
 */
export const clearAdminToken = () => {
  localStorage.removeItem(ADMIN_TOKEN_KEY);
};

