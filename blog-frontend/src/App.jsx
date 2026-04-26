



import { RouterProvider } from 'react-router-dom';
import { App as AntdApp, ConfigProvider, theme as antTheme } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import router from './router';






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





function App() {
  return <AppContent />;
}

export default App;

