/**
 * 路由配置
 * 定义后台管理系统的所有页面路由，包括登录页、文章管理、分类管理、
 * 标签管理、评论审核、社区帖子、互动数据、审核任务和举报处理等。
 */
import { createBrowserRouter, Navigate } from 'react-router-dom';
import BasicLayout from '../layouts/BasicLayout';
import Login from '../pages/Login';
import Dashboard from '../pages/Dashboard';
import ArticleList from '../pages/Article/List';
import ArticleEdit from '../pages/Article/Edit';
import Category from '../pages/Category';
import Comment from '../pages/Comment';
import CommunityInteraction from '../pages/CommunityInteraction';
import CommunityPost from '../pages/CommunityPost';
import ContentReport from '../pages/ContentReport';
import ModerationTask from '../pages/ModerationTask';
import Tag from '../pages/Tag';
import RequireAdminAuth from '../auth/RequireAdminAuth';
import { adminBasePath } from '../utils/path';

/**
 * 创建后台管理系统的路由实例
 * - /login: 登录页（无需鉴权）
 * - / 下所有子路由需经过 RequireAdminAuth 鉴权守卫
 * - 根路径默认重定向到文章列表页
 */
const router = createBrowserRouter(
    [
        {
            path: '/login',
            element: <Login />,
        },
        {
            element: <RequireAdminAuth />,
            children: [
                {
                    path: '/',
                    element: <BasicLayout />,
                    children: [
                        { index: true, element: <Dashboard /> },
                        { path: 'articles', element: <ArticleList /> },
                        { path: 'articles/new', element: <ArticleEdit /> },
                        { path: 'articles/edit/:id', element: <ArticleEdit /> },
                        { path: 'categories', element: <Category /> },
                        { path: 'tags', element: <Tag /> },
                        { path: 'comments', element: <Comment /> },
                        { path: 'community-posts', element: <CommunityPost /> },
                        { path: 'community-interactions', element: <CommunityInteraction /> },
                        { path: 'moderation-tasks', element: <ModerationTask /> },
                        { path: 'content-reports', element: <ContentReport /> },
                    ],
                },
            ],
        },
    ],
    { basename: adminBasePath }
);

export default router;
