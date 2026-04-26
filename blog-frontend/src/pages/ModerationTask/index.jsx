import React, { useEffect, useState } from 'react';
import { App, Button, Empty, Popconfirm, Select, Space, Table, Tag } from 'antd';
import request from '../../utils/request';

const statusColors = {
  PENDING: 'processing',
  APPROVED: 'success',
  REJECTED: 'error',
  CANCELED: 'default',
};

const riskColors = {
  LOW: 'default',
  MEDIUM: 'warning',
  HIGH: 'error',
};

const formatDate = (value) => {
  if (!value) return '-';
  return new Date(value).toLocaleString();
};

const ModerationTaskPage = () => {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [statusFilter, setStatusFilter] = useState('');
  const [typeFilter, setTypeFilter] = useState('');
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });

  const fetchData = async (page = 1, size = 10, status = statusFilter, type = typeFilter) => {
    setLoading(true);
    try {
      const res = await request.get('/api/admin/moderation/tasks', {
        params: {
          page,
          size,
          ...(status ? { status } : {}),
          ...(type ? { type } : {}),
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
      message.error(error.response?.data?.message || '获取审核任务失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(1, pagination.pageSize, statusFilter, typeFilter);
  }, [statusFilter, typeFilter]);

  const handleDecision = async (id, decision) => {
    try {
      await request.put(`/api/admin/moderation/tasks/${id}/decision`, { decision });
      message.success(`任务已标记为${decision === 'APPROVED' ? '已通过' : '已拒绝'}`);
      fetchData(pagination.current, pagination.pageSize, statusFilter, typeFilter);
    } catch (error) {
      message.error(error.response?.data?.message || '提交审核决定失败');
    }
  };

  const statusLabels = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已拒绝',
    CANCELED: '已取消',
  };

  const riskLabels = {
    LOW: '低',
    MEDIUM: '中',
    HIGH: '高',
  };

  const typeLabels = {
    POST: '帖子',
    COMMENT: '评论',
    POST_COMMENT: '帖子评论',
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80, render: (value) => <span className="admin-table__muted">{value}</span> },
    {
      title: '目标内容',
      key: 'target',
      render: (_, record) => (
        <div>
          <div className="admin-table__title">{record.titleSnapshot || '-'}</div>
          <div className="admin-table__muted">
            {(typeLabels[record.contentType] || record.contentType)} #{record.contentId}
          </div>
        </div>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status) => <Tag color={statusColors[status] || 'default'}>{statusLabels[status] || status}</Tag>,
    },
    {
      title: '风险/命中',
      key: 'risk',
      width: 140,
      render: (_, record) => (
        <Space>
          <Tag color={riskColors[record.riskLevel] || 'default'}>{riskLabels[record.riskLevel] || record.riskLevel}</Tag>
          <span className="admin-table__muted">{record.hitCount || 0}</span>
        </Space>
      ),
    },
    { title: '提交者', dataIndex: 'submittedBy', key: 'submittedBy', width: 140, render: (value) => <span className="admin-table__muted">{value}</span> },
    { title: '提交时间', dataIndex: 'submittedAt', key: 'submittedAt', render: (value) => <span className="admin-table__muted">{formatDate(value)}</span>, width: 180 },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record) => (
        <Space size="small">
          <Popconfirm title="确定通过此任务吗？" onConfirm={() => handleDecision(record.id, 'APPROVED')} okText="通过" cancelText="取消" disabled={record.status !== 'PENDING'}>
            <Button type="text" className="admin-icon-btn admin-icon-btn--green" disabled={record.status !== 'PENDING'}>
              通过
            </Button>
          </Popconfirm>
          <Popconfirm title="确定拒绝此任务吗？" onConfirm={() => handleDecision(record.id, 'REJECTED')} okText="拒绝" cancelText="取消" disabled={record.status !== 'PENDING'}>
            <Button type="text" className="admin-icon-btn admin-icon-btn--red" disabled={record.status !== 'PENDING'}>
              拒绝
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="admin-page admin-page--table animate-in fade-in duration-500">
      <div className="admin-page__header">
        <div>
          <h1 className="admin-page__title">审核任务</h1>
          <p className="admin-page__desc">在统一队列中审核社区帖子和评论。</p>
        </div>
        <div className="admin-page__actions">
          <Select
            allowClear
            placeholder="按状态筛选"
            value={statusFilter || undefined}
            onChange={(value) => setStatusFilter(value || '')}
            className="admin-select"
            options={Object.keys(statusLabels).map((key) => ({ value: key, label: statusLabels[key] }))}
          />
          <Select
            allowClear
            placeholder="按类型筛选"
            value={typeFilter || undefined}
            onChange={(value) => setTypeFilter(value || '')}
            className="admin-select"
            options={Object.keys(typeLabels).map((key) => ({ value: key, label: typeLabels[key] }))}
          />
        </div>
      </div>

      <div className="admin-panel">
        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          loading={loading}
          className="admin-table"
          pagination={{ ...pagination, onChange: (page, size) => fetchData(page, size, statusFilter, typeFilter) }}
          locale={{
            emptyText: <Empty description={<span style={{ color: '#71717a' }}>暂无审核任务</span>} />,
          }}
        />
      </div>
    </div>
  );
};

export default ModerationTaskPage;
