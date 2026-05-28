import React, { useEffect, useRef, useState } from 'react';
import { Dropdown, Avatar } from 'antd';
import { Outlet, useNavigate, useLocation, Link } from 'react-router-dom';
import {
  LayoutDashboard,
  FileText,
  FolderTree,
  Tags,
  MessageSquare,
  Flame,
  ShieldAlert,
  Flag,
  Search,
  Bell,
  LogOut,
} from 'lucide-react';
import { clearAdminToken } from '../auth/storage';
import request from '../utils/request';
import { adminAssetPath } from '../utils/path';

const MENU_ITEMS = [
  { path: '/articles', icon: FileText, label: '文章管理' },
  { path: '/categories', icon: FolderTree, label: '分类管理' },
  { path: '/tags', icon: Tags, label: '标签管理' },
  { path: '/community-posts', icon: MessageSquare, label: '社区帖子' },
  { path: '/community-interactions', icon: Flame, label: '互动数据' },
  { path: '/moderation-tasks', icon: ShieldAlert, label: '审核任务' },
  { path: '/content-reports', icon: Flag, label: '举报处理' },
];

const BasicLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const contentRef = useRef(null);
  const [unreadNotifications, setUnreadNotifications] = useState(0);

  const handleLogout = async () => {
    try {
      await request.post('/api/admin/logout');
    } catch (error) {
      console.error('Admin logout failed', error);
    } finally {
      clearAdminToken();
      navigate('/login', { replace: true });
    }
  };

  const isEditorPage =
    location.pathname.includes('/articles/new') ||
    location.pathname.includes('/articles/edit');

  useEffect(() => {
    if (contentRef.current) {
      contentRef.current.scrollTo({ top: 0, left: 0, behavior: 'auto' });
    }
  }, [location.pathname]);

  useEffect(() => {
    let isMounted = true;

    const fetchUnreadNotifications = async () => {
      try {
        const res = await request.get('/api/admin/community/interactions/overview');
        const payload = res.data || res;

        if (isMounted) {
          setUnreadNotifications(payload?.unreadNotifications ?? 0);
        }
      } catch (error) {
        if (isMounted) {
          setUnreadNotifications(0);
        }
      }
    };

    fetchUnreadNotifications();

    return () => {
      isMounted = false;
    };
  }, [location.pathname]);

  return (
    <div className="admin-shell">
      <div className="admin-shell__bg" />

      <aside className="admin-sidebar">
        <div className="admin-sidebar__brand">
          <button type="button" className="admin-sidebar__logo" onClick={() => navigate('/articles')}>
            <span className="admin-sidebar__logo-text">PX</span>
          </button>
          <div>
            <div className="admin-sidebar__title">破星辰只寻你</div>
            <div className="admin-sidebar__subtitle">后台管理中心</div>
          </div>
        </div>

        <nav className="admin-sidebar__nav">
          <SidebarLink
            to="/"
            label="仪表盘"
            icon={LayoutDashboard}
            active={location.pathname === '/'}
          />
          {MENU_ITEMS.map((item) => {
            const active = location.pathname === item.path || location.pathname.startsWith(`${item.path}/`);
            return (
              <SidebarLink
                key={item.path}
                to={item.path}
                label={item.label}
                icon={item.icon}
                active={active}
              />
            );
          })}
        </nav>
      </aside>

      <div className="admin-main">
        <header className="admin-header">
          <div className="admin-search">
            <Search size={18} className="admin-search__icon" />
            <input type="text" placeholder="搜索..." className="admin-search__input" />
          </div>

          <div className="admin-header__actions">
            <button className="admin-bell" type="button" aria-label="通知">
              <Bell size={20} />
              {unreadNotifications > 0 && <span className="admin-bell__dot" />}
            </button>

            <div className="admin-user">
              <div className="admin-user__meta">
                <div className="admin-user__name">Admin User</div>
                <div className="admin-user__role">Super Administrator</div>
              </div>
              <Dropdown
                menu={{
                  items: [
                    {
                      key: 'logout',
                      icon: <LogOut size={14} />,
                      label: '退出登录',
                      onClick: handleLogout,
                    },
                  ],
                }}
                placement="bottomRight"
              >
                <div className="admin-user__avatar-wrap">
                  <Avatar
                    size={44}
                    src={adminAssetPath('assets/avatar.png')}
                    className="admin-user__avatar"
                  >
                    AD
                  </Avatar>
                </div>
              </Dropdown>
            </div>
          </div>
        </header>

        <main ref={contentRef} className={`admin-content ${isEditorPage ? 'admin-content--editor' : ''}`}>
          <Outlet />
        </main>
      </div>
    </div>
  );
};

function SidebarLink({ to, label, icon, active, onClick, standalone = true }) {
  const className = `admin-nav-item${active ? ' admin-nav-item--active' : ''}`;
  const IconComponent = icon;

  if (!standalone && onClick) {
    return (
      <button type="button" className={className} onClick={onClick}>
        <IconComponent size={18} />
        <span>{label}</span>
      </button>
    );
  }

  return (
    <Link to={to} className={className}>
      <IconComponent size={18} />
      <span>{label}</span>
    </Link>
  );
}

export default BasicLayout;
