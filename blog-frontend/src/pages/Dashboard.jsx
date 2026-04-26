import React, { useEffect, useState } from 'react';
import { App, Empty } from 'antd';
import { FileText, FolderTree, Tags, Bell } from 'lucide-react';
import request from '../utils/request';

const emptyChartData = [
  { label: '周一', value: 0 },
  { label: '周二', value: 0 },
  { label: '周三', value: 0 },
  { label: '周四', value: 0 },
  { label: '周五', value: 0 },
  { label: '周六', value: 0 },
  { label: '周日', value: 0 },
];

const Dashboard = () => {
  const { message } = App.useApp();
  const [stats, setStats] = useState({
    articles: '--',
    categories: '--',
    tags: '--',
    unreadNotifications: '--',
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [articlesRes, categoriesRes, tagsRes, overviewRes] = await Promise.all([
          request.get('/api/admin/articles', { params: { page: 1, size: 1 } }),
          request.get('/api/public/categories'),
          request.get('/api/public/tags'),
          request.get('/api/admin/community/interactions/overview').catch(() => null),
        ]);

        const articlesData = articlesRes.data || articlesRes;
        const categoriesData = categoriesRes.data || categoriesRes;
        const tagsData = tagsRes.data || tagsRes;
        const overviewData = overviewRes ? overviewRes.data || overviewRes : null;

        setStats({
          articles: articlesData.total ?? (Array.isArray(articlesData) ? articlesData.length : 0),
          categories: Array.isArray(categoriesData) ? categoriesData.length : categoriesData.total ?? 0,
          tags: Array.isArray(tagsData) ? tagsData.length : tagsData.total ?? 0,
          unreadNotifications: overviewData?.unreadNotifications ?? 0,
        });
      } catch (error) {
        console.error('Failed to fetch dashboard data', error);
        message.warning('仪表盘部分数据暂时不可用');
      }
    };

    fetchData();
  }, [message]);

  return (
    <div className="admin-page">
      <div className="admin-page__header">
        <div>
          <h1 className="admin-page__title">仪表盘</h1>
          <p className="admin-page__desc">统一查看内容管理的基础状态和系统概览。</p>
        </div>
      </div>

      <div className="admin-dashboard-grid">
        <MetricCard title="文章总数" value={stats.articles} icon={FileText} tone="blue" />
        <MetricCard title="分类总数" value={stats.categories} icon={FolderTree} tone="green" />
        <MetricCard title="标签总数" value={stats.tags} icon={Tags} tone="amber" />
        <MetricCard title="未读通知" value={stats.unreadNotifications} icon={Bell} tone="pink" />
      </div>

      <div className="admin-dashboard-panels">
        <div className="admin-panel admin-dashboard-panel">
          <div className="admin-dashboard-panel__title">访问量趋势</div>
          <div className="admin-dashboard-chart">
            <div className="admin-dashboard-chart__grid">
              {emptyChartData.map((item) => (
                <div key={item.label} className="admin-dashboard-chart__col">
                  <div className="admin-dashboard-chart__bar" style={{ height: `${item.value}%` }} />
                  <span>{item.label}</span>
                </div>
              ))}
            </div>
            <div className="admin-dashboard-chart__empty">暂无数据</div>
          </div>
        </div>

        <div className="admin-panel admin-dashboard-panel">
          <div className="admin-dashboard-panel__title">内容状态</div>
          <div className="admin-dashboard-empty">
            <Empty description={<span style={{ color: '#71717a' }}>更多后台统计面板稍后补齐</span>} />
          </div>
        </div>
      </div>
    </div>
  );
};

function MetricCard({ title, value, icon: Icon, tone }) {
  const IconComponent = Icon;

  return (
    <div className={`admin-metric admin-metric--${tone}`}>
      <div className="admin-metric__icon">
        <IconComponent size={22} />
      </div>
      <div className="admin-metric__value">{value}</div>
      <div className="admin-metric__label">{title}</div>
    </div>
  );
}

export default Dashboard;
