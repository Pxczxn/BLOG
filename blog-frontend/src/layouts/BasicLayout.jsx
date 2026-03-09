import React from 'react';
import { Layout, Menu, Dropdown, Avatar } from 'antd';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
    FileTextOutlined,
    FolderOpenOutlined,
    TagsOutlined,
    CommentOutlined,
    LogoutOutlined,
} from '@ant-design/icons';
import { adminAssetPath } from '../utils/path';

const { Header, Content } = Layout;

const BasicLayout = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    const menuItems = [
        { key: '/articles', icon: <FileTextOutlined />, label: '文章' },
        { key: '/categories', icon: <FolderOpenOutlined />, label: '分类' },
        { key: '/tags', icon: <TagsOutlined />, label: '标签' },
        { key: '/comments', icon: <CommentOutlined />, label: '评论' },
    ];

    const selectedKey = location.pathname.startsWith('/articles') && location.pathname !== '/articles'
        ? '/articles'
        : location.pathname;

    const isEditorPage = location.pathname.includes('/articles/new') || location.pathname.includes('/articles/edit');

    return (
        <Layout
            className="min-h-screen bg-slate-50 dark:bg-[#030712]"
            style={{
                backgroundImage:
                    'radial-gradient(1200px 420px at 50% -120px, rgba(99,102,241,0.18), transparent 70%), radial-gradient(900px 320px at 10% 20%, rgba(14,165,233,0.12), transparent 72%)',
            }}
        >
            <Header className="sticky top-0 z-50 px-8 flex items-center justify-between h-16 bg-white/80 dark:bg-[#09090b]/80 backdrop-blur-md border-b border-slate-200 dark:border-[#27272a]">
                <div className="flex items-center gap-8 h-full">
                    <div className="flex items-center gap-3 cursor-pointer" onClick={() => navigate('/')}>
                        <div className="w-8 h-8 bg-slate-900 dark:bg-slate-100 rounded-lg flex items-center justify-center">
                            <span className="text-white dark:text-slate-900 font-bold text-base">PX</span>
                        </div>
                        <span className="text-slate-900 dark:text-slate-100 text-base font-semibold tracking-tight">破星辰 CMS</span>
                    </div>
                    <Menu
                        mode="horizontal"
                        selectedKeys={[selectedKey]}
                        onClick={({ key }) => navigate(key)}
                        items={menuItems}
                        className="bg-transparent border-b-0 leading-[62px] min-w-[400px]"
                    />
                </div>

                <div className="flex items-center gap-4">
                    <Dropdown
                        menu={{
                            items: [
                                {
                                    key: 'logout',
                                    icon: <LogoutOutlined />,
                                    label: '退出登录',
                                    onClick: handleLogout,
                                },
                            ],
                        }}
                        placement="bottomRight"
                    >
                        <div className="flex items-center gap-2 cursor-pointer p-1 rounded-full hover:bg-slate-100 dark:hover:bg-[#27272a]">
                            <Avatar
                                size={32}
                                src={adminAssetPath('assets/avatar.png')}
                                className="bg-slate-200 dark:bg-[#27272a] border border-slate-300 dark:border-[#3f3f46]"
                            />
                        </div>
                    </Dropdown>
                </div>
            </Header>

            <Content
                className={
                    isEditorPage
                        ? 'w-full max-w-[1280px] mx-auto my-4 md:my-6 p-0 rounded-2xl border border-slate-200/45 dark:border-white/15 bg-white/72 dark:bg-slate-950/45 backdrop-blur-2xl shadow-[0_20px_80px_rgba(2,6,23,.55)] overflow-hidden'
                        : 'w-full p-4 md:p-8 max-w-[1200px] mx-auto my-6 rounded-2xl border border-slate-200/50 dark:border-white/15 bg-white/78 dark:bg-slate-950/45 backdrop-blur-2xl shadow-[0_20px_80px_rgba(2,6,23,.55)]'
                }
            >
                <Outlet />
            </Content>
        </Layout>
    );
};

export default BasicLayout;
