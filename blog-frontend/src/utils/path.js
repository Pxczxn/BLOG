/**
 * 路径工具
 * 提供管理后台的基础路径和资源路径拼接功能，适配不同的部署路径。
 */

/** 从环境变量获取基础路径 */
const rawBase = import.meta.env.BASE_URL || '/';

/**
 * 管理后台路由基础路径
 * 根路径时为空字符串，否则移除末尾斜杠
 */
export const adminBasePath = rawBase === '/' ? '' : rawBase.replace(/\/$/, '');

/**
 * 拼接管理后台资源路径
 * @param {string} path - 相对路径
 * @returns {string} 完整的资源路径
 */
export const adminAssetPath = (path) => {
  // 移除路径开头的斜杠
  const normalizedPath = path.replace(/^\/+/, '');
  // 确保前缀以斜杠结尾
  const prefix = rawBase.endsWith('/') ? rawBase : `${rawBase}/`;
  return `${prefix}${normalizedPath}`;
};

