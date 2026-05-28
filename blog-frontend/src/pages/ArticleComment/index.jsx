import React, { useEffect, useState } from 'react';
import { App, Button, Checkbox, Empty, Popconfirm, Space, Table, Tag } from 'antd';
import { CheckSquare, X } from 'lucide-react';
import request from '../../utils/request';

const formatDate = (time) => {
  if (!time) return '-';
  const d = new Date(time);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(
    d.getHours(),
  ).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
};

const ArticleComment = () => {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [batchMode, setBatchMode] = useState(false);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  const fetchComments = async (page = 1, size = 10) => {
    setLoading(true);
    try {
      const res = await request.get('/api/admin/comments', {
        params: { page, size, status: 'PENDING' },
      });
      const payload = res.data || res;
      setData(payload.items || []);
      setPagination({
        current: payload.page || page,
        pageSize: payload.size || size,
        total: payload.total || 0,
      });
    } catch (error) {
      message.error(error.response?.data?.message || '获取评论列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchComments(pagination.current, pagination.pageSize);
  }, []);

  const handleTableChange = (nextPagination) => {
    fetchComments(nextPagination.current, nextPagination.pageSize);
  };

  const handleAction = async (id, action) => {
    try {
      await request.put(`/api/admin/comments/${id}/${action}`);
      message.success('操作成功');
      fetchComments(pagination.current, pagination.pageSize);
    } catch (error) {
      message.error(error.response?.data?.message || '操作失败');
    }
  };

  const handleBatchAction = async (action) => {
    if (!selectedRowKeys.length) {
      message.info('请先选择要处理的评论');
      return;
    }

    const results = await Promise.allSettled(selectedRowKeys.map((id) => request.put(`/api/admin/comments/${id}/${action}`)));
    const successCount = results.filter((item) => item.status === 'fulfilled').length;
    const failedCount = results.length - successCount;
    const actionLabel = action === 'approve' ? '通过' : '拒绝';

    if (successCount) {
      message.success(`已${actionLabel} ${successCount} 条评论`);
    }
    if (failedCount) {
      message.warning(`${failedCount} 条评论处理失败`);
    }

    setSelectedRowKeys([]);
    fetchComments(pagination.current, pagination.pageSize);
  };

  const allRowKeys = data.map((item) => item.id);
  const allSelected = allRowKeys.length > 0 && selectedRowKeys.length === allRowKeys.length;
  const partiallySelected = selectedRowKeys.length > 0 && selectedRowKeys.length < allRowKeys.length;

  const toggleRowSelection = (id, checked) => {
    setSelectedRowKeys((prev) => {
      if (checked) {
        return prev.includes(id) ? prev : [...prev, id];
      }
      return prev.filter((key) => key !== id);
    });
  };

  const toggleAllSelection = (checked) => {
    setSelectedRowKeys(checked ? allRowKeys : []);
  };

  const baseColumns = [
    {
      title: '文章ID',
      dataIndex: 'articleId',
      key: 'articleId',
      width: 110,
      render: (value) => <span className="admin-table__muted">{value}</span>,
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      key: 'nickname',
      width: 150,
      render: (value) => <span className="admin-table__title">{value || '-'}</span>,
    },
    {
      title: '内容',
      dataIndex: 'content',
      key: 'content',
      ellipsis: true,
      render: (value) => <span className="admin-table__muted">{value || '-'}</span>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status) => <Tag color="orange">{status === 'PENDING' ? '待审核' : status}</Tag>,
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 170,
      render: (value) => <span className="admin-table__muted">{formatDate(value)}</span>,
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record) => (
        <Space size="small" wrap>
          <Button type="text" className="admin-icon-btn admin-icon-btn--green" onClick={() => handleAction(record.id, 'approve')}>
            通过
          </Button>
          <Button type="text" className="admin-icon-btn admin-icon-btn--red" onClick={() => handleAction(record.id, 'reject')}>
            拒绝
          </Button>
        </Space>
      ),
    },
    {
      title: batchMode ? (
        <Checkbox
          checked={allSelected}
          indeterminate={partiallySelected}
          onChange={(event) => toggleAllSelection(event.target.checked)}
        />
      ) : '',
      key: 'selection',
      width: 72,
      align: 'center',
      render: (_, record) =>
        batchMode ? (
          <Checkbox
            checked={selectedRowKeys.includes(record.id)}
            onChange={(event) => toggleRowSelection(record.id, event.target.checked)}
          />
        ) : (
          <span style={{ display: 'inline-block', width: 16, height: 16 }} />
        ),
    },
  ];
  const columns = baseColumns;

  return (
    <div className="admin-page admin-page--table">
      <div className="admin-page__header">
        <div>
          <h1 className="admin-page__title">文章评论管理</h1>
          <p className="admin-page__desc">集中审核待处理评论，保持内容区干净有序。</p>
        </div>

        <div className="admin-page__actions">
          {batchMode && (
            <div className="admin-bulk-bar admin-bulk-bar--inline">
              <div className="admin-bulk-bar__meta">
                已选择 <strong>{selectedRowKeys.length}</strong> 条评论
              </div>
              <div className="admin-bulk-bar__actions">
                <Button type="default" className="admin-secondary-btn" onClick={() => setSelectedRowKeys([])}>
                  清空选择
                </Button>
                <Popconfirm title="确定通过已选评论吗？" onConfirm={() => handleBatchAction('approve')} okText="通过" cancelText="取消">
                  <Button className="admin-secondary-btn">批量通过</Button>
                </Popconfirm>
                <Popconfirm title="确定拒绝已选评论吗？" onConfirm={() => handleBatchAction('reject')} okText="拒绝" cancelText="取消">
                  <Button danger className="admin-secondary-btn">
                    批量拒绝
                  </Button>
                </Popconfirm>
              </div>
            </div>
          )}

          <Button
            type="default"
            onClick={() => {
              setBatchMode((prev) => !prev);
              setSelectedRowKeys([]);
            }}
            className="admin-secondary-btn"
          >
            {batchMode ? <X size={16} style={{ marginRight: 8 }} /> : <CheckSquare size={16} style={{ marginRight: 8 }} />}
            {batchMode ? '退出批量管理' : '批量管理评论'}
          </Button>
        </div>
      </div>

      <div className="admin-panel">
        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          pagination={pagination}
          loading={loading}
          onChange={handleTableChange}
          className="admin-table"
          locale={{
            emptyText: <Empty description={<span style={{ color: '#71717a' }}>暂无数据</span>} />,
          }}
        />
      </div>
    </div>
  );
};

export default ArticleComment;
