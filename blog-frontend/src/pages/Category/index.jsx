import React, { useEffect, useState } from 'react';
import { Table, Button, Checkbox, Space, Modal, Form, Input, App, Popconfirm } from 'antd';
import { CheckSquare, Edit3, FolderTree, Plus, Trash2, X } from 'lucide-react';
import request from '../../utils/request';

const Category = () => {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [batchMode, setBatchMode] = useState(false);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [form] = Form.useForm();

  const fetchCategories = async () => {
    setLoading(true);
    try {
      const res = await request.get('/api/public/categories');
      const payload = res.data || res;
      setData(Array.isArray(payload) ? payload : payload.items || []);
    } catch (error) {
      message.error(error.response?.data?.message || '获取分类失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleAdd = () => {
    setEditingCategory(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingCategory(record);
    form.setFieldsValue(record);
    setIsModalVisible(true);
  };

  const handleDelete = async (id) => {
    try {
      await request.delete(`/api/admin/categories/${id}`);
      message.success('删除成功');
      fetchCategories();
    } catch (error) {
      message.error(error.response?.data?.message || '删除失败');
    }
  };

  const handleBatchDelete = async () => {
    if (!selectedRowKeys.length) {
      message.info('请先选择要处理的分类');
      return;
    }

    const results = await Promise.allSettled(selectedRowKeys.map((id) => request.delete(`/api/admin/categories/${id}`)));
    const successCount = results.filter((item) => item.status === 'fulfilled').length;
    const failedCount = results.length - successCount;

    if (successCount) {
      message.success(`已删除 ${successCount} 个分类`);
    }
    if (failedCount) {
      message.warning(`${failedCount} 个分类删除失败`);
    }

    setSelectedRowKeys([]);
    fetchCategories();
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
      if (editingCategory) {
        await request.put(`/api/admin/categories/${editingCategory.id}`, values);
        message.success('更新成功');
      } else {
        await request.post('/api/admin/categories', values);
        message.success('创建成功');
      }
      setIsModalVisible(false);
      fetchCategories();
    } catch (error) {
      if (error.response) {
        message.error(error.response?.data?.message || '操作失败');
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
      title: 'Slug',
      dataIndex: 'slug',
      key: 'slug',
      render: (value) => <span className="admin-table__muted">{value || '-'}</span>,
    },
    {
      title: '操作',
      key: 'action',
      width: 140,
      render: (_, record) => (
        <Space size="small">
          <Button type="text" onClick={() => handleEdit(record)} className="admin-icon-btn admin-icon-btn--blue" icon={<Edit3 size={16} />} />
          <Popconfirm title="确定要删除这个分类吗？" onConfirm={() => handleDelete(record.id)} okText="确定" cancelText="取消">
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
          <h1 className="admin-page__title">分类管理</h1>
          <p className="admin-page__desc">集中维护博客内容的分类结构</p>
        </div>

        <div className="admin-page__actions">
          {batchMode && (
            <div className="admin-bulk-bar admin-bulk-bar--inline">
              <div className="admin-bulk-bar__meta">
                已选择 <strong>{selectedRowKeys.length}</strong> 个分类
              </div>
              <div className="admin-bulk-bar__actions">
                <Button type="default" className="admin-secondary-btn" onClick={() => setSelectedRowKeys([])}>
                  清空选择
                </Button>
                <Popconfirm title="确定删除已选分类吗？" onConfirm={handleBatchDelete} okText="删除" cancelText="取消">
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
            {batchMode ? '退出批量管理' : '批量管理分类'}
          </Button>

          <Button type="primary" onClick={handleAdd} className="admin-primary-btn">
            <Plus size={16} style={{ marginRight: 8 }} />
            创建分类
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
                <FolderTree size={48} className="admin-empty__icon" />
                <div className="admin-empty__title">暂无分类</div>
                <div className="admin-empty__desc">先创建一个分类，把文章体系搭起来。</div>
              </div>
            ),
          }}
        />
      </div>

      <Modal
        title={editingCategory ? '编辑分类' : '创建分类'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        okText="保存"
        cancelText="取消"
        className="admin-modal"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="分类名称" rules={[{ required: true, message: '请输入分类名称' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="slug" label="Slug">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Category;
