import { createBrowserRouter } from 'react-router-dom';
import Layout from '../components/Layout';
import ListPage from '../pages/List';
import DetailPage from '../pages/Detail';
import AboutPage from '../pages/About';
import CategoryPage from '../pages/Category';
import TagPage from '../pages/Tag';
import NotFoundPage from '../pages/NotFound';

const router = createBrowserRouter([
    {
        path: '/',
        element: <Layout />,
        children: [
            {
                index: true,
                element: <ListPage />,
            },
            {
                path: 'category/:slug',
                element: <CategoryPage />,
            },
            {
                path: 'tag/:slug',
                element: <TagPage />,
            },
            {
                path: 'post/:slug',
                element: <DetailPage />,
            },
            {
                path: 'about',
                element: <AboutPage />,
            },
            {
                path: '*',
                element: <NotFoundPage />,
            }
        ],
    },
]);

export default router;
