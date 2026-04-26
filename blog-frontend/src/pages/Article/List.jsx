import React, { useEffect, useState } from 'react';
import { Table, Button, Checkbox, Space, Tag, App, Popconfirm, Empty } from 'antd';
import { useNavigate } from 'react-router-dom';
import { DeleteOutlined, EditOutlined, SendOutlined, UndoOutlined } from '@ant-design/icons';
import { CheckSquare, X } from 'lucide-react';
import { NotebookPen } from 'lucide-react';
import request from '../../utils/request';

const formatDate = (time) => {
  if (!time) return '-';
  const d = new Date(time);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(
    d.getHours(),
  ).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
};

const ArticleList = () => {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [batchMode, setBatchMode] = useState(false);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const navigate = useNavigate();

  const fetchArticles = async (page = 1, size = 10) => {
    setLoading(true);
    try {
      const res = await request.get('/api/admin/articles', {
        params: { page, size },
      });
      const payload = res.data || res;
      if (Array.isArray(payload)) {
        setData(payload);
        setPagination({ current: 1, pageSize: 10, total: payload.length });
      } else {
        setData(payload.items || []);
        setPagination({
          current: payload.page || 1,
          pageSize: payload.size || 10,
          total: payload.total || 0,
        });
      }
    } catch (error) {
      message.error(error.response?.data?.message || '获取文章列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchArticles(pagination.current, pagination.pageSize);
  }, []);

  const handleTableChange = (nextPagination) => {
    fetchArticles(nextPagination.current, nextPagination.pageSize);
  };

  const handleDelete = async (id) => {
    try {
      await request.delete(`/api/admin/articles/${id}`);
      message.success('删除成功');
      fetchArticles(pagination.current, pagination.pageSize);
    } catch (error) {
      message.error(error.response?.data?.message || '删除失败');
    }
  };

  const handleStatusChange = async (id, currentStatus) => {
    try {
      const action = currentStatus === 'PUBLISHED' ? 'draft' : 'publish';
      await request.put(`/api/admin/articles/${id}/${action}`);
      message.success('操作成功');
      fetchArticles(pagination.current, pagination.pageSize);
    } catch (error) {
      message.error(error.response?.data?.message || '操作失败');
    }
  };

  const handleBatchAction = async (action) => {
    if (!selectedRowKeys.length) {
      message.info('请先选择要处理的文章');
      return;
    }

    const requests = selectedRowKeys.map((id) => {
      if (action === 'delete') {
        return request.delete(`/api/admin/articles/${id}`);
      }
      return request.put(`/api/admin/articles/${id}/${action}`);
    });

    const results = await Promise.allSettled(requests);
    const successCount = results.filter((item) => item.status === 'fulfilled').length;
    const failedCount = results.length - successCount;
    const actionLabel = action === 'delete' ? '删除' : action === 'publish' ? '发布' : '转为草稿';

    if (successCount) {
      message.success(`已${actionLabel} ${successCount} 篇文章`);
    }
    if (failedCount) {
      message.warning(`${failedCount} 篇文章处理失败`);
    }

    setSelectedRowKeys([]);
    fetchArticles(pagination.current, pagination.pageSize);
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
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      render: (text) => <span className="admin-table__title">{text}</span>,
    },
    {
      title: '分类',
      dataIndex: ['category', 'name'],
      key: 'categoryName',
      render: (text, record) => (
        <Tag className="admin-tag admin-tag--blue">{text || record.categoryName || '默认'}</Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Tag className={`admin-tag ${status === 'PUBLISHED' ? 'admin-tag--green' : 'admin-tag--muted'}`}>
          {status === 'PUBLISHED' ? '已发布' : '草稿'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (time) => <span className="admin-table__muted">{formatDate(time)}</span>,
    },
    {
      title: '操作',
      key: 'action',
      width: 168,
      render: (_, record) => (
        <Space size="small">
          <Button type="text" icon={<EditOutlined />} onClick={() => navigate(`/articles/edit/${record.id}`)} className="admin-icon-btn admin-icon-btn--blue" />
          <Button
            type="text"
            icon={record.status === 'PUBLISHED' ? <UndoOutlined /> : <SendOutlined />}
            onClick={() => handleStatusChange(record.id, record.status)}
            className={`admin-icon-btn ${record.status === 'PUBLISHED' ? 'admin-icon-btn--amber' : 'admin-icon-btn--green'}`}
          />
          <Popconfirm title="确定要删除这篇文章吗？" onConfirm={() => handleDelete(record.id)} okText="确定" cancelText="取消">
            <Button type="text" danger icon={<DeleteOutlined />} className="admin-icon-btn admin-icon-btn--red" />
          </Popconfirm>
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
          <h1 className="admin-page__title">文章管理</h1>
          <p className="admin-page__desc">在这里管理和发布你的所有文章内容</p>
        </div>

        <div className="admin-page__actions">
          {batchMode && (
            <div className="admin-bulk-bar admin-bulk-bar--inline">
              <div className="admin-bulk-bar__meta">
                已选择 <strong>{selectedRowKeys.length}</strong> 篇文章
              </div>
              <div className="admin-bulk-bar__actions">
                <Button type="default" className="admin-secondary-btn" onClick={() => setSelectedRowKeys([])}>
                  清空选择
                </Button>
                <Popconfirm title="确定发布已选文章吗？" onConfirm={() => handleBatchAction('publish')} okText="发布" cancelText="取消">
                  <Button className="admin-secondary-btn">批量发布</Button>
                </Popconfirm>
                <Popconfirm title="确定将已选文章转为草稿吗？" onConfirm={() => handleBatchAction('draft')} okText="转为草稿" cancelText="取消">
                  <Button className="admin-secondary-btn">批量转草稿</Button>
                </Popconfirm>
                <Popconfirm title="确定删除已选文章吗？" onConfirm={() => handleBatchAction('delete')} okText="删除" cancelText="取消">
                  <Button danger className="admin-secondary-btn">批量删除</Button>
                </Popconfirm>
              </div>
            </div>
          )}

          <Button
            type="default"
            size="large"
            onClick={() => {
              setBatchMode((prev) => !prev);
              setSelectedRowKeys([]);
            }}
            className="admin-secondary-btn"
          >
            {batchMode ? <X size={18} style={{ marginRight: 8 }} /> : <CheckSquare size={18} style={{ marginRight: 8 }} />}
            {batchMode ? '退出批量管理' : '批量管理文章'}
          </Button>
        </div>
      </div>

      <div className="admin-panel">
        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          pagination={{ ...pagination, style: { marginBottom: 0 } }}
          loading={loading}
          onChange={handleTableChange}
          className="admin-table"
          locale={{
            emptyText: (
              <Empty
                image={<NotebookPen className="mx-auto my-6 h-16 w-16 text-slate-600 opacity-20" />}
                description={
                  <div className="pb-4 text-slate-400">
                    <p className="mb-2 text-lg font-medium">暂无文章</p>
                    <p className="text-sm opacity-60">打开批量管理后，可以更快处理整批文章。</p>
                  </div>
                }
              />
            ),
          }}
        />
      </div>
    </div>
  );
};

export default ArticleList;
