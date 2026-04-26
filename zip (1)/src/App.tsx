/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Home from './pages/Home';
import Category from './pages/Category';
import Tags from './pages/Tags';
import Community from './pages/Community';
import About from './pages/About';

// Admin imports
import AdminLayout from './layouts/AdminLayout';
import Dashboard from './pages/admin/Dashboard';
import ArticleManage from './pages/admin/ArticleManage';
import CategoryManage from './pages/admin/CategoryManage';
import TagManage from './pages/admin/TagManage';
import CommentManage from './pages/admin/CommentManage';
import CommunityManage from './pages/admin/CommunityManage';
import ModerationManage from './pages/admin/ModerationManage';
import ReportManage from './pages/admin/ReportManage';
import SettingsManage from './pages/admin/SettingsManage';

export default function App() {
  return (
    <Router>
      <Routes>
        {/* Public Application Routes */}
        <Route path="/" element={<Layout />}>
          <Route index element={<Home />} />
          <Route path="category" element={<Category />} />
          <Route path="tags" element={<Tags />} />
          <Route path="community" element={<Community />} />
          <Route path="about" element={<About />} />
        </Route>

        {/* Admin Application Routes */}
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="articles" element={<ArticleManage />} />
          <Route path="categories" element={<CategoryManage />} />
          <Route path="tags" element={<TagManage />} />
          <Route path="comments" element={<CommentManage />} />
          <Route path="community" element={<CommunityManage />} />
          <Route path="moderation" element={<ModerationManage />} />
          <Route path="reports" element={<ReportManage />} />
          <Route path="settings" element={<SettingsManage />} />
        </Route>
      </Routes>
    </Router>
  );
}
