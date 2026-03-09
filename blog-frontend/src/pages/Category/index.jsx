import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, App, Popconfirm } from 'antd';
import request from '../../utils/request';

const Category = () => {
    const { message } = App.useApp();
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingCategory, setEditingCategory] = useState(null);
    const [form] = Form.useForm();

    const fetchCategories = async () => {
        setLoading(true);
        try {
            const res = await request.get('/api/public/categories');
            const resData = res.data || res;
            setData(Array.isArray(resData) ? resData : resData.items || []);
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

    const columns = [
        { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
        { title: '名称', dataIndex: 'name', key: 'name' },
        { title: 'Slug', dataIndex: 'slug', key: 'slug' },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button type="link" onClick={() => handleEdit(record)}>编辑</Button>
                    <Popconfirm title="确定要删除这个分类吗？" onConfirm={() => handleDelete(record.id)}>
                        <Button type="link" danger>删除</Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div>
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
                <h2>分类管理</h2>
                <Button type="primary" onClick={handleAdd}>
                    创建分类
                </Button>
            </div>
            <Table
                columns={columns}
                dataSource={data}
                rowKey="id"
                loading={loading}
                pagination={false}
            />

            <Modal
                title={editingCategory ? '编辑分类' : '创建分类'}
                open={isModalVisible}
                onOk={handleModalOk}
                onCancel={() => setIsModalVisible(false)}
            >
                <Form form={form} layout="vertical">
                    <Form.Item
                        name="name"
                        label="分类名称"
                        rules={[{ required: true, message: '请输入分类名称' }]}
                    >
                        <Input />
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default Category;
