import React, { useEffect, useState } from 'react';
import { App, Empty, Table, Tag } from 'antd';
import request from '../../utils/request';

const CommunityInteractionPage = () => {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [overview, setOverview] = useState(null);

  const fetchOverview = async () => {
    setLoading(true);
    try {
      const res = await request.get('/api/admin/community/interactions/overview', {
        params: { topSize: 12 },
      });
      setOverview(res.data || res);
    } catch (error) {
      message.error(error.response?.data?.message || '获取互动数据失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOverview();
  }, []);

  const columns = [
    {
      title: '帖子ID',
      dataIndex: 'postId',
      key: 'postId',
      width: 90,
      render: (value) => <span className="admin-table__muted">{value}</span>,
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      render: (value) => <span className="admin-table__title">{value}</span>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 130,
      render: (status) => <Tag>{status}</Tag>,
    },
    { title: '点赞数', dataIndex: 'likeCount', key: 'likeCount', width: 100 },
    { title: '收藏数', dataIndex: 'favoriteCount', key: 'favoriteCount', width: 110 },
    { title: '热度', dataIndex: 'heatScore', key: 'heatScore', width: 100 },
  ];

  const tableData = overview?.topHotPosts || [];

  return (
    <div className="admin-page admin-page--table">
      <div className="admin-page__header admin-page__header--compact">
        <div>
          <h1 className="admin-page__title">互动数据</h1>
        </div>

        <div className="admin-page__actions admin-page__actions--flush">
          <div className={`admin-overview-strip admin-overview-strip--compact${loading ? ' admin-overview-strip--loading' : ''}`}>
            <div className="admin-overview-strip__item">
              <span className="admin-overview-strip__label">总点赞数</span>
              <strong className="admin-overview-strip__value">{overview?.totalLikes || 0}</strong>
            </div>
            <div className="admin-overview-strip__item">
              <span className="admin-overview-strip__label">总收藏数</span>
              <strong className="admin-overview-strip__value">{overview?.totalFavorites || 0}</strong>
            </div>
            <div className="admin-overview-strip__item">
              <span className="admin-overview-strip__label">总关注数</span>
              <strong className="admin-overview-strip__value">{overview?.totalFollows || 0}</strong>
            </div>
            <div className="admin-overview-strip__item">
              <span className="admin-overview-strip__label">总通知数</span>
              <strong className="admin-overview-strip__value">{overview?.totalNotifications || 0}</strong>
            </div>
            <div className="admin-overview-strip__item">
              <span className="admin-overview-strip__label">未读通知</span>
              <strong className="admin-overview-strip__value">{overview?.unreadNotifications || 0}</strong>
            </div>
          </div>
        </div>
      </div>

      <div className="admin-panel">
        <Table
          columns={columns}
          dataSource={tableData}
          rowKey="postId"
          pagination={false}
          className="admin-table"
          loading={loading}
          locale={{
            emptyText: <Empty description={<span style={{ color: '#71717a' }}>暂无互动数据</span>} />,
          }}
        />
      </div>
    </div>
  );
};

export default CommunityInteractionPage;
