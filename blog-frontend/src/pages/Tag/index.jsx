import React, { useEffect, useState } from 'react';
import { App, Button, Checkbox, Form, Input, Modal, Popconfirm, Space, Table } from 'antd';
import { CheckSquare, Plus, Tags as TagsIcon, Trash2, X } from 'lucide-react';
import request from '../../utils/request';

const Tag = () => {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [batchMode, setBatchMode] = useState(false);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [form] = Form.useForm();

  const fetchTags = async () => {
    setLoading(true);
    try {
      const res = await request.get('/api/public/tags');
      const payload = res.data || res;
      const list = Array.isArray(payload) ? payload : payload.items || [];
      setData([...list].sort((a, b) => Number(a.id) - Number(b.id)));
    } catch (error) {
      message.error(error.response?.data?.message || '获取标签失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTags();
  }, []);

  const handleAdd = () => {
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleDelete = async (id) => {
    try {
      await request.delete(`/api/admin/tags/${id}`);
      message.success('删除成功');
      fetchTags();
    } catch (error) {
      if (error?.response?.status === 404) {
        message.warning('标签不存在或已被删除，列表已刷新');
        fetchTags();
        return;
      }
      message.error(error.response?.data?.message || '删除失败');
    }
  };

  const handleBatchDelete = async () => {
    if (!selectedRowKeys.length) {
      message.info('请先选择要处理的标签');
      return;
    }

    const results = await Promise.allSettled(selectedRowKeys.map((id) => request.delete(`/api/admin/tags/${id}`)));
    const successCount = results.filter((item) => item.status === 'fulfilled').length;
    const failedCount = results.length - successCount;

    if (successCount) {
      message.success(`已删除 ${successCount} 个标签`);
    }
    if (failedCount) {
      message.warning(`${failedCount} 个标签删除失败`);
    }

    setSelectedRowKeys([]);
    fetchTags();
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

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      await request.post('/api/admin/tags', values);
      message.success('创建成功');
      setIsModalVisible(false);
      fetchTags();
    } catch (error) {
      if (error.response) {
        message.error(error.response?.data?.message || '创建失败');
      }
    }
  };

  const baseColumns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      render: (value) => <span className="admin-table__muted">{value}</span>,
    },
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      render: (value) => <span className="admin-table__title">{value}</span>,
    },
    {
      title: '操作',
      key: 'action',
      width: 116,
      render: (_, record) => (
        <Space size="small">
          <Popconfirm title="确定要删除这个标签吗？" onConfirm={() => handleDelete(record.id)} okText="确定" cancelText="取消">
            <Button type="text" danger className="admin-icon-btn admin-icon-btn--red" icon={<Trash2 size={16} />} />
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
          <h1 className="admin-page__title">标签管理</h1>
          <p className="admin-page__desc">维护文章标签，用更灵活的方式组织内容。</p>
        </div>

        <div className="admin-page__actions">
          {batchMode && (
            <div className="admin-bulk-bar admin-bulk-bar--inline">
              <div className="admin-bulk-bar__meta">
                已选择 <strong>{selectedRowKeys.length}</strong> 个标签
              </div>
              <div className="admin-bulk-bar__actions">
                <Button type="default" className="admin-secondary-btn" onClick={() => setSelectedRowKeys([])}>
                  清空选择
                </Button>
                <Popconfirm title="确定删除已选标签吗？" onConfirm={handleBatchDelete} okText="删除" cancelText="取消">
                  <Button danger className="admin-secondary-btn">
                    批量删除
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
            {batchMode ? '退出批量管理' : '批量管理标签'}
          </Button>

          <Button type="primary" onClick={handleAdd} className="admin-primary-btn">
            <Plus size={16} style={{ marginRight: 8 }} />
            创建标签
          </Button>
        </div>
      </div>

      <div className="admin-panel">
        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          loading={loading}
          pagination={false}
          className="admin-table"
          locale={{
            emptyText: (
              <div className="admin-empty">
                <TagsIcon size={48} className="admin-empty__icon" />
                <div className="admin-empty__title">暂无标签</div>
                <div className="admin-empty__desc">创建标签后，文章内容会更容易检索和聚合。</div>
              </div>
            ),
          }}
        />
      </div>

      <Modal
        title="创建标签"
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        okText="保存"
        cancelText="取消"
        className="admin-modal"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="标签名称" rules={[{ required: true, message: '请输入标签名称' }]}>
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Tag;
