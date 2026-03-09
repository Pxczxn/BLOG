import React, { useEffect, useState } from 'react';
import { App, Button, Form, Input, Modal, Popconfirm, Space, Table } from 'antd';
import request from '../../utils/request';

const Tag = () => {
    const { message } = App.useApp();
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = Form.useForm();

    const fetchTags = async () => {
        setLoading(true);
        try {
            const res = await request.get('/api/public/tags');
            const resData = res.data || res;
            const list = Array.isArray(resData) ? resData : resData.items || [];
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

    const columns = [
        { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
        { title: '名称', dataIndex: 'name', key: 'name' },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Popconfirm title="确定要删除这个标签吗？" onConfirm={() => handleDelete(record.id)}>
                        <Button type="link" danger>
                            删除
                        </Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div>
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
                <h2>标签管理</h2>
                <Button type="primary" onClick={handleAdd}>
                    创建标签
                </Button>
            </div>

            <Table columns={columns} dataSource={data} rowKey="id" loading={loading} pagination={false} />

            <Modal title="创建标签" open={isModalVisible} onOk={handleModalOk} onCancel={() => setIsModalVisible(false)}>
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
