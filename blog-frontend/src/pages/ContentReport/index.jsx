import React, { useEffect, useState } from 'react';
import { App, Button, Empty, Popconfirm, Select, Space, Table, Tag } from 'antd';
import request from '../../utils/request';

const statusColors = {
  OPEN: 'processing',
  RESOLVED: 'success',
  DISMISSED: 'default',
};

const statusLabels = {
  OPEN: '待处理',
  RESOLVED: '已解决',
  DISMISSED: '已驳回',
};

const typeLabels = {
  POST: '帖子',
  COMMENT: '评论',
  POST_COMMENT: '帖子评论',
};

const reasonLabels = {
  SPAM: '垃圾信息',
  ABUSE: '辱骂攻击',
  COPYRIGHT: '侵权',
  ILLEGAL: '违法违规',
  OTHER: '其他',
};

const formatDate = (value) => {
  if (!value) return '-';
  return new Date(value).toLocaleString();
};

const ContentReportPage = () => {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [statusFilter, setStatusFilter] = useState('');
  const [typeFilter, setTypeFilter] = useState('');
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });

  const fetchData = async (page = 1, size = 10, status = statusFilter, type = typeFilter) => {
    setLoading(true);
    try {
      const res = await request.get('/api/admin/moderation/reports', {
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
      message.error(error.response?.data?.message || '获取举报列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(1, pagination.pageSize, statusFilter, typeFilter);
  }, [statusFilter, typeFilter]);

  const handleReport = async (record, status, handleAction) => {
    try {
      await request.put(`/api/admin/moderation/reports/${record.id}/handle`, {
        status,
        handleAction,
      });
      message.success('举报已处理');
      fetchData(pagination.current, pagination.pageSize, statusFilter, typeFilter);
    } catch (error) {
      message.error(error.response?.data?.message || '处理举报失败');
    }
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      render: (value) => <span className="admin-table__muted">{value}</span>,
    },
    {
      title: '目标内容',
      key: 'target',
      width: 260,
      render: (_, record) => (
        <div>
          <div className="admin-table__title">
            {typeLabels[record.contentType] || record.contentType} #{record.contentId}
          </div>
          <div className="admin-table__muted">原因：{reasonLabels[record.reason] || record.reason}</div>
        </div>
      ),
    },
    {
      title: '举报人',
      dataIndex: 'reporter',
      key: 'reporter',
      width: 140,
      render: (value) => <span className="admin-table__muted">{value || '-'}</span>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status) => <Tag color={statusColors[status] || 'default'}>{statusLabels[status] || status}</Tag>,
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: 280,
      ellipsis: true,
      render: (value) => <span className="admin-table__muted">{value || '-'}</span>,
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (value) => <span className="admin-table__muted">{formatDate(value)}</span>,
    },
    {
      title: '操作',
      key: 'action',
      width: 210,
      render: (_, record) => (
        <Space size="small" wrap>
          <Popconfirm
            title="确定驳回此举报吗？"
            onConfirm={() => handleReport(record, 'DISMISSED', 'NONE')}
            okText="驳回"
            cancelText="取消"
            disabled={record.status !== 'OPEN'}
          >
            <Button type="text" disabled={record.status !== 'OPEN'}>
              驳回
            </Button>
          </Popconfirm>

          {record.contentType === 'POST' && (
            <Popconfirm
              title="确定解决并隐藏此帖子吗？"
              onConfirm={() => handleReport(record, 'RESOLVED', 'HIDE_POST')}
              okText="解决"
              cancelText="取消"
              disabled={record.status !== 'OPEN'}
            >
              <Button type="text" className="admin-icon-btn admin-icon-btn--amber" disabled={record.status !== 'OPEN'}>
                隐藏帖子
              </Button>
            </Popconfirm>
          )}

          {(record.contentType === 'COMMENT' || record.contentType === 'POST_COMMENT') && (
            <Popconfirm
              title="确定解决并拒绝此评论吗？"
              onConfirm={() => handleReport(record, 'RESOLVED', 'REJECT_COMMENT')}
              okText="解决"
              cancelText="取消"
              disabled={record.status !== 'OPEN'}
            >
              <Button type="text" className="admin-icon-btn admin-icon-btn--amber" disabled={record.status !== 'OPEN'}>
                拒绝评论
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div className="admin-page admin-page--table animate-in fade-in duration-500">
      <div className="admin-page__header">
        <div>
          <h1 className="admin-page__title">举报处理</h1>
          <p className="admin-page__desc">处理用户举报并采取相应的审核措施。</p>
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
          scroll={{ x: 1180 }}
          pagination={{
            ...pagination,
            onChange: (page, size) => fetchData(page, size, statusFilter, typeFilter),
          }}
          locale={{
            emptyText: <Empty description={<span style={{ color: '#71717a' }}>暂无举报</span>} />,
          }}
        />
      </div>
    </div>
  );
};

export default ContentReportPage;
