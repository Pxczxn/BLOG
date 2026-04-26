/**
 * Vite 构建配置
 * <p>
 * 配置 React 插件、基础路径和开发服务器端口
 */
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  // 生产环境使用 /admin-pxczxn/ 作为基础路径
  base: process.env.NODE_ENV === 'production' ? '/admin-pxczxn/' : '/',
  plugins: [react()],
  server: {
    port: 4001,
    strictPort: true,
  },
})
