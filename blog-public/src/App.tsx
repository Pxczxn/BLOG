import { Suspense, lazy } from 'react';
import { BrowserRouter as Router, Navigate, Routes, Route } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import Layout from './components/Layout';
import { AuthProvider } from './lib/AuthContext';

// 管理员相关路由
import RequireAdminAuth from './components/RequireAdminAuth';

const Home = lazy(() => import('./pages/Home'));
const Blog = lazy(() => import('./pages/Blog'));
const BlogArticleEditor = lazy(() => import('./pages/BlogArticleEditor'));
const Category = lazy(() => import('./pages/Category'));
const Tags = lazy(() => import('./pages/Tags'));
const Community = lazy(() => import('./pages/Community'));
const CommunityPostDetail = lazy(() => import('./pages/CommunityPostDetail'));
const CommunityPostEditor = lazy(() => import('./pages/CommunityPostEditor'));
const Login = lazy(() => import('./pages/Login'));
const Register = lazy(() => import('./pages/Register'));
const About = lazy(() => import('./pages/About'));
const Resources = lazy(() => import('./pages/Resources'));
const ArticleDetail = lazy(() => import('./pages/ArticleDetail'));
const UserProfile = lazy(() => import('./pages/UserProfile'));

const AdminLayout = lazy(() => import('./layouts/AdminLayout'));
const AdminLogin = lazy(() => import('./pages/admin-pxczxn/Login'));
const Dashboard = lazy(() => import('./pages/admin-pxczxn/Dashboard'));
const ArticleManage = lazy(() => import('./pages/admin-pxczxn/ArticleManage'));
const ArticleEditor = lazy(() => import('./pages/admin-pxczxn/ArticleEditor'));
const CategoryManage = lazy(() => import('./pages/admin-pxczxn/CategoryManage'));
const TagManage = lazy(() => import('./pages/admin-pxczxn/TagManage'));
const CommunityManage = lazy(() => import('./pages/admin-pxczxn/CommunityManage'));
const ArticleCommentManage = lazy(() => import('./pages/admin-pxczxn/ArticleCommentManage.tsx')); // Force Vite reload
const CommunityCommentManage = lazy(() => import('./pages/admin-pxczxn/CommunityCommentManage.tsx')); // Force Vite reload
const ModerationManage = lazy(() => import('./pages/admin-pxczxn/ModerationManage'));
const ReportManage = lazy(() => import('./pages/admin-pxczxn/ReportManage'));
const SettingsManage = lazy(() => import('./pages/admin-pxczxn/SettingsManage'));

function RouteFallback() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-[#030014] text-sm text-slate-400">
      页面加载中...
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <Toaster
          position="top-center"
          toastOptions={{
            duration: 3000,
            style: {
              background: '#1e293b',
              color: '#f1f5f9',
              border: '1px solid #334155',
            },
            success: {
              iconTheme: {
                primary: '#10b981',
                secondary: '#f1f5f9',
              },
            },
            error: {
              iconTheme: {
                primary: '#ef4444',
                secondary: '#f1f5f9',
              },
            },
          }}
        />
        <Suspense fallback={<RouteFallback />}>
          <Routes>
            {/* 公共页面 */}
            <Route path="/" element={<Layout />}>
              <Route index element={<Home />} />
              <Route path="blog" element={<Blog />} />
              <Route path="blog/new" element={<BlogArticleEditor />} />
              <Route path="category" element={<Category />} />
              <Route path="tags" element={<Tags />} />
              <Route path="post/:slug" element={<ArticleDetail />} />
              <Route path="resources" element={<Resources />} />
              <Route path="resources/:type" element={<Resources />} />
              <Route path="community" element={<Community />} />
              <Route path="community/post/:slug" element={<CommunityPostDetail />} />
              <Route path="community/new" element={<CommunityPostEditor />} />
              <Route path="about" element={<About />} />
            </Route>

            {/* 社区认证 */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/me" element={<Layout />}><Route index element={<UserProfile />} /></Route>

            {/* 管理员登录 */}
            <Route path="/admin-pxczxn/login" element={<AdminLogin />} />

            {/* 管理后台 */}
            <Route path="/admin-pxczxn" element={<RequireAdminAuth />}>
              <Route element={<AdminLayout />}>
                <Route index element={<Dashboard />} />
                <Route path="articles" element={<ArticleManage />} />
                <Route path="articles/new" element={<ArticleEditor />} />
                <Route path="articles/edit/:id" element={<ArticleEditor />} />
                <Route path="articles/comments" element={<ArticleCommentManage />} />
                <Route path="categories" element={<CategoryManage />} />
                <Route path="tags" element={<TagManage />} />
                <Route path="community" element={<CommunityManage />} />
                <Route path="community/comments" element={<CommunityCommentManage />} />
                <Route path="moderation" element={<ModerationManage />} />
                <Route path="reports" element={<ReportManage />} />
                <Route path="settings" element={<SettingsManage />} />
                <Route path="comments" element={<Navigate to="/admin-pxczxn/articles/comments" replace />} />
              </Route>
            </Route>
          </Routes>
        </Suspense>
      </Router>
    </AuthProvider>
  );
}
