/**
 * 应用入口
 * 初始化 React 应用，启用暗色模式并挂载根组件。
 */
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

// 启用全局暗色模式样式
document.documentElement.classList.add('dark')
document.body.classList.add('dark')
document.body.setAttribute('data-color-mode', 'dark')

// 创建 React 根节点并渲染应用
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)

