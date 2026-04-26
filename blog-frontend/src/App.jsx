/**
 * 应用根组件
 * 配置 Ant Design 暗色主题和中文国际化，挂载路由提供器。
 */
import { RouterProvider } from 'react-router-dom';
import { App as AntdApp, ConfigProvider, theme as antTheme } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import router from './router';

/**
 * 应用内容组件
 * 配置 Ant Design 的主题和国际化，包裹路由提供器
 * @returns {JSX.Element} 配置后的应用组件
 */
function AppContent() {
  return (
    <ConfigProvider
      locale={zhCN}
      theme={{
        algorithm: antTheme.darkAlgorithm,
        token: {
          colorPrimary: '#818cf8',
          colorInfo: '#818cf8',
          colorBgBase: '#09090b',
          colorBgElevated: '#18181b',
          borderRadius: 8,
          wireframe: false,
        },
      }}
    >
      <AntdApp>
        <RouterProvider router={router} />
      </AntdApp>
    </ConfigProvider>
  );
}

/**
 * 应用根组件
 * @returns {JSX.Element} 应用内容组件
 */
function App() {
  return <AppContent />;
}

export default App;

