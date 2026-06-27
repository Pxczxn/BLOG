/**
 * Vite 构建配置
 * <p>
 * 配置 React、Tailwind CSS 和开发服务器
 */
import tailwindcss from '@tailwindcss/vite';
import react from '@vitejs/plugin-react';
import path from 'path';
import {defineConfig} from 'vite';

export default defineConfig(() => {
  return {
    plugins: [react(), tailwindcss()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, '.'),
      },
    },
    server: {
      port: 4000,
      host: '0.0.0.0',
      // HMR 在 AI Studio 中通过 DISABLE_HMR 环境变量禁用。
      // 不要修改——文件监视已禁用以防止代理编辑期间闪烁。
      hmr: process.env.DISABLE_HMR !== 'true',
      proxy: {
        '/api': {
          target: 'http://localhost:4002',
          changeOrigin: true,
        },
        '/uploads': {
          target: 'http://localhost:4002',
          changeOrigin: true,
        },
      },
    },
    build: {
      cssCodeSplit: true,
      rollupOptions: {
        output: {
          manualChunks: {
            react: ['react', 'react-dom', 'react-router-dom'],
            charts: ['recharts'],
            motion: ['motion'],
            markdown: ['react-markdown', 'remark-gfm'],
            ui: ['lucide-react', 'react-hot-toast', 'react-helmet-async'],
          },
        },
      },
    },
  };
});
