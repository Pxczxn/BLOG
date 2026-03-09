import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, App } from 'antd';
import request from '../../utils/request';

const formatDate = (time) => {
    if (!time) return '-';
    const d = new Date(time);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
};

const Comment = () => {
    const { message } = App.useApp();
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });

    const fetchComments = async (page = 1, size = 10) => {
        setLoading(true);
        try {
            const res = await request.get('/api/admin/comments', {
                params: { page, size, status: 'PENDING' },
            });
            const resData = res.data || res;
            setData(resData.items || []);
            setPagination({ current: resData.page || 1, pageSize: resData.size || 10, total: resData.total || 0 });
        } catch (error) {
            message.error(error.response?.data?.message || '获取评论失败');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchComments(pagination.current, pagination.pageSize);
    }, []);

    const handleTableChange = (newPagination) => {
        fetchComments(newPagination.current, newPagination.pageSize);
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

    const columns = [
        { title: '文章 ID', dataIndex: 'articleId', key: 'articleId' },
        { title: '昵称', dataIndex: 'nickname', key: 'nickname' },
        { title: '内容', dataIndex: 'content', key: 'content' },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (status) => <Tag color="orange">{status === 'PENDING' ? '待审核' : status}</Tag>,
        },
        {
            title: '时间',
            dataIndex: 'createdAt',
            key: 'createdAt',
            render: (time) => formatDate(time),
        },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button type="primary" size="small" onClick={() => handleAction(record.id, 'approve')}>通过</Button>
                    <Button type="primary" danger size="small" onClick={() => handleAction(record.id, 'reject')}>拒绝</Button>
                </Space>
            ),
        },
    ];

    return (
        <div>
            <div style={{ marginBottom: 16 }}>
                <h2>待审核评论</h2>
            </div>
            <Table
                columns={columns}
                dataSource={data}
                rowKey="id"
                pagination={pagination}
                loading={loading}
                onChange={handleTableChange}
            />
        </div>
    );
};

export default Comment;
