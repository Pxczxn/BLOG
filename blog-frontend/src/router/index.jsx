import { createBrowserRouter, Navigate } from 'react-router-dom';
import BasicLayout from '../layouts/BasicLayout';
import Login from '../pages/Login';
import ArticleList from '../pages/Article/List';
import ArticleEdit from '../pages/Article/Edit';
import Category from '../pages/Category';
import Comment from '../pages/Comment';
import Tag from '../pages/Tag';
import { adminBasePath } from '../utils/path';

const router = createBrowserRouter(
    [
        {
            path: '/login',
            element: <Login />,
        },
        {
            path: '/',
            element: <BasicLayout />,
            children: [
                {
                    index: true,
                    element: <Navigate to="/articles" replace />,
                },
                {
                    path: 'articles',
                    element: <ArticleList />,
                },
                {
                    path: 'articles/new',
                    element: <ArticleEdit />,
                },
                {
                    path: 'articles/edit/:id',
                    element: <ArticleEdit />,
                },
                {
                    path: 'categories',
                    element: <Category />,
                },
                {
                    path: 'tags',
                    element: <Tag />,
                },
                {
                    path: 'comments',
                    element: <Comment />,
                },
            ],
        },
    ],
    { basename: adminBasePath }
);

export default router;
