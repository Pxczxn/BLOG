




import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'


export default defineConfig({
  
  base: process.env.NODE_ENV === 'production' ? '/admin-pxczxn/' : '/',
  plugins: [react()],
  server: {
    port: 4001,
    strictPort: true,
  },
})
