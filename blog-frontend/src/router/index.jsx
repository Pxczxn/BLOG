




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
