/*
 * 功能：前端模块逻辑。
 */
import { createBrowserRouter } from 'react-router-dom';
import Layout from '../components/Layout';
import ListPage from '../pages/List';
import DetailPage from '../pages/Detail';
import AboutPage from '../pages/About';
import CategoryPage from '../pages/Category';
import TagPage from '../pages/Tag';
import LoginPage from '../pages/Login';
import RegisterPage from '../pages/Register';
import MePage from '../pages/Me';
import UserProfilePage from '../pages/UserProfile';
import CommunityHomePage from '../pages/CommunityHome';
import CommunityNodePage from '../pages/CommunityNode';
import CommunityPostDetailPage from '../pages/CommunityPostDetail';
import CommunityPostEditorPage from '../pages/CommunityPostEditor';
import NotFoundPage from '../pages/NotFound';

const router = createBrowserRouter([
    {
        path: '/',
        element: <Layout />,
        children: [
            { index: true, element: <ListPage /> },
            { path: 'category/:slug', element: <CategoryPage /> },
            { path: 'tag/:slug', element: <TagPage /> },
            { path: 'post/:slug', element: <DetailPage /> },
            { path: 'about', element: <AboutPage /> },
            { path: 'login', element: <LoginPage /> },
            { path: 'register', element: <RegisterPage /> },
            { path: 'me', element: <MePage /> },
            { path: 'u/:username', element: <UserProfilePage /> },
            { path: 'community', element: <CommunityHomePage /> },
            { path: 'community/node/:slug', element: <CommunityNodePage /> },
            { path: 'community/post/:slug', element: <CommunityPostDetailPage /> },
            { path: 'community/new', element: <CommunityPostEditorPage /> },
            { path: 'community/edit/:id', element: <CommunityPostEditorPage /> },
            { path: '*', element: <NotFoundPage /> },
        ],
    },
]);

export default router;

