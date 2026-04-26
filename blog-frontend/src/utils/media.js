/**
 * 媒体 URL 处理工具
 * 将绝对 URL 转换为相对路径，用于处理图片等媒体资源的地址。
 */

/**
 * 将 URL 转换为相对路径
 * @param {string} url - 原始 URL
 * @returns {string} 相对路径 URL，无效输入返回空字符串
 * @description
 * - 空值或非字符串返回空字符串
 * - Data URL（base64）原样返回
 * - 绝对 URL 提取路径部分
 * - 相对路径确保以 / 开头
 */
export const toRelativeMediaUrl = (url) => {
  // 空值或非字符串处理
  if (!url || typeof url !== 'string') return '';
  // Data URL 原样返回
  if (url.startsWith('data:')) return url;
  // 绝对 URL 转换为相对路径
  if (/^https?:\/\//i.test(url)) {
    try {
      const parsed = new URL(url);
      return `${parsed.pathname}${parsed.search}${parsed.hash}`;
    } catch {
      return '';
    }
  }
  // 确保路径以 / 开头
  return url.startsWith('/') ? url : `/${url}`;
};

