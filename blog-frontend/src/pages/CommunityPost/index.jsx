import React, { useEffect, useState } from 'react';
import { App, Button, Empty, Popconfirm, Select, Space, Table, Tag } from 'antd';
import { DeleteOutlined, EyeOutlined } from '@ant-design/icons';
import { MessageSquare } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import request from '../../utils/request';

const statusColors = {
  DRAFT: 'default',
  PUBLISHED: 'success',
  PENDING_REVIEW: 'processing',
  REJECTED: 'error',
  HIDDEN: 'warning',
};

const statusLabels = {
  DRAFT: '草稿',
  PUBLISHED: '已发布',
  PENDING_REVIEW: '待审核',
  REJECTED: '已拒绝',
  HIDDEN: '已隐藏',
};

const CommunityPostPage = () => {
  const { message } = App.useApp();
  const navigate = useNavigate();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [statusFilter, setStatusFilter] = useState('');
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });

  const fetchPosts = async (page = 1, size = 10, status = statusFilter) => {
    setLoading(true);
    try {
      const res = await request.get('/api/admin/community/posts', {
        params: {
          page,
          size,
          ...(status ? { status } : {}),
        },
      });
      const payload = res.data || res;
      setData(payload.items || []);
      setPagination({
        current: payload.page || page,
        pageSize: payload.size || size,
        total: payload.total || 0,
      });
    } catch (error) {
      message.error(error.response?.data?.message || '获取社区帖子失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts(1, pagination.pageSize, statusFilter);
  }, [statusFilter]);

  const updateStatus = async (id, status) => {
    try {
      await request.put(`/api/admin/community/posts/${id}/status`, { status });
      message.success(`帖子状态已更新为 ${statusLabels[status] || status}`);
      fetchPosts(pagination.current, pagination.pageSize, statusFilter);
    } catch (error) {
      message.error(error.response?.data?.message || '更新状态失败');
    }
  };

  const handleDelete = async (id) => {
    try {
      await request.delete(`/api/admin/community/posts/${id}`);
      message.success('帖子已删除');
      fetchPosts(pagination.current, pagination.pageSize, statusFilter);
    } catch (error) {
      message.error(error.response?.data?.message || '删除失败');
    }
  };

  const columns = [
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      render: (value, record) => (
        <div>
          <div className="admin-table__title">{value}</div>
          <div className="admin-table__muted">
            {record.authorName || '未知作者'} · {record.nodeName || '未分节点'}
          </div>
        </div>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 140,
      render: (status) => <Tag color={statusColors[status] || 'default'}>{statusLabels[status] || status}</Tag>,
    },
    {
      title: '浏览量',
      dataIndex: 'viewCount',
      key: 'viewCount',
      width: 120,
      render: (value) => <span className="admin-table__muted">{value || 0}</span>,
    },
    {
      title: '操作',
      key: 'action',
      width: 280,
      render: (_, record) => (
        <Space size="small">
          {record.status !== 'PUBLISHED' && (
            <Button type="text" onClick={() => updateStatus(record.id, 'PUBLISHED')} className="admin-icon-btn admin-icon-btn--green">
              发布
            </Button>
          )}
          {record.status !== 'HIDDEN' && (
            <Button type="text" onClick={() => updateStatus(record.id, 'HIDDEN')} className="admin-icon-btn admin-icon-btn--amber">
              隐藏
            </Button>
          )}
          {record.status !== 'REJECTED' && (
            <Button type="text" onClick={() => updateStatus(record.id, 'REJECTED')} className="admin-icon-btn admin-icon-btn--red">
              拒绝
            </Button>
          )}
          {record.slug && (
            <Button type="text" icon={<EyeOutlined />} href={`/community/post/${record.slug}`} target="_blank" className="admin-icon-btn admin-icon-btn--blue" />
          )}
          <Popconfirm title="确定要删除这篇帖子吗？" onConfirm={() => handleDelete(record.id)} okText="删除" cancelText="取消">
            <Button type="text" danger icon={<DeleteOutlined />} className="admin-icon-btn admin-icon-btn--red" />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="admin-page admin-page--table animate-in fade-in duration-500">
      <div className="admin-page__header">
        <div>
          <h1 className="admin-page__title">社区帖子管理</h1>
          <p className="admin-page__desc">管理社区帖子生命周期和可见性。</p>
        </div>
        <div className="admin-page__actions">
          <Select
            allowClear
            placeholder="按状态筛选"
            value={statusFilter || undefined}
            onChange={(value) => setStatusFilter(value || '')}
            style={{ width: 220 }}
            options={Object.keys(statusColors).map((key) => ({ value: key, label: statusLabels[key] }))}
          />
          <Button
            type="default"
            size="large"
            onClick={() => navigate('/community-posts/comments')}
            className="admin-secondary-btn"
          >
            <MessageSquare size={18} style={{ marginRight: 8 }} />
            评论管理
          </Button>
        </div>
      </div>

      <div className="admin-panel">
        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          loading={loading}
          className="admin-table"
          pagination={{ ...pagination, onChange: (page, size) => fetchPosts(page, size, statusFilter) }}
          locale={{
            emptyText: <Empty description={<span style={{ color: '#71717a' }}>暂无社区帖子</span>} />,
          }}
        />
      </div>
    </div>
  );
};

export default CommunityPostPage;
