/**
 * 工具函数
 * <p>
 * 通用工具函数集合
 */
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

// 合并 Tailwind CSS 类名，避免冲突
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}
