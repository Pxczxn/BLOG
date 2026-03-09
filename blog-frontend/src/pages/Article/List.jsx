import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, App, Popconfirm, Empty } from 'antd';
import { useNavigate } from 'react-router-dom';
import { EditOutlined, DeleteOutlined, SendOutlined, UndoOutlined } from '@ant-design/icons';
import { NotebookPen } from 'lucide-react';
import request from '../../utils/request';

const formatDate = (time) => {
    if (!time) return '-';
    const d = new Date(time);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
};

const ArticleList = () => {
    const { message } = App.useApp();
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
    const navigate = useNavigate();

    const fetchArticles = async (page = 1, size = 10) => {
        setLoading(true);
        try {
            const res = await request.get('/api/admin/articles', {
                params: { page, size },
            });
            const resData = res.data || res;
            if (Array.isArray(resData)) {
                setData(resData);
                setPagination({ current: 1, pageSize: 10, total: resData.length });
            } else {
                setData(resData.items || []);
                setPagination({ current: resData.page || 1, pageSize: resData.size || 10, total: resData.total || 0 });
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

    const handleTableChange = (newPagination) => {
        fetchArticles(newPagination.current, newPagination.pageSize);
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

    const columns = [
        {
            title: '标题',
            dataIndex: 'title',
            key: 'title',
            render: (text) => <span style={{ fontWeight: 500, color: '#e4e4e7' }}>{text}</span>,
        },
        {
            title: '分类',
            dataIndex: ['category', 'name'],
            key: 'categoryName',
            render: (text, record) => (
                <Tag color="geekblue" style={{ border: '1px solid #1d4ed850', background: '#1d4ed820' }}>
                    {text || record.categoryName || '默认'}
                </Tag>
            ),
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (status) => (
                <Tag
                    color={status === 'PUBLISHED' ? 'success' : 'default'}
                    style={{
                        border: '1px solid rgba(255,255,255,0.1)',
                        background: status === 'PUBLISHED' ? '#064e3b30' : '#27272a',
                    }}
                >
                    {status === 'PUBLISHED' ? '已发布' : '草稿'}
                </Tag>
            ),
        },
        {
            title: '创建时间',
            dataIndex: 'createdAt',
            key: 'createdAt',
            render: (time) => <span style={{ color: '#a1a1aa' }}>{formatDate(time)}</span>,
        },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space size="small">
                    <Button type="text" icon={<EditOutlined />} onClick={() => navigate(`/articles/edit/${record.id}`)} style={{ color: '#60a5fa' }} />
                    <Button
                        type="text"
                        icon={record.status === 'PUBLISHED' ? <UndoOutlined /> : <SendOutlined />}
                        onClick={() => handleStatusChange(record.id, record.status)}
                        style={{ color: record.status === 'PUBLISHED' ? '#fbbf24' : '#34d399' }}
                    />
                    <Popconfirm title="确定要删除这篇文章吗？" onConfirm={() => handleDelete(record.id)} okText="确定" cancelText="取消">
                        <Button type="text" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div className="animate-in fade-in duration-500">
            <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                    <h2 style={{ margin: 0, fontSize: 24, fontWeight: 600, color: '#fafafa', letterSpacing: '-0.5px' }}>文章管理</h2>
                    <p style={{ margin: '4px 0 0 0', color: '#a1a1aa', fontSize: 13 }}>在这里管理和发布你的所有文章内容</p>
                </div>
                <Button
                    type="primary"
                    size="large"
                    onClick={() => navigate('/articles/new')}
                    style={{ borderRadius: 8, background: '#fafafa', color: '#09090b', fontWeight: 500, border: 'none', padding: '0 24px' }}
                >
                    <NotebookPen size={18} style={{ marginRight: 8 }} /> 写新文章
                </Button>
            </div>
            <div className="glass-content" style={{ padding: 20, background: 'rgba(255,255,255,0.02)', borderRadius: 16, border: '1px solid rgba(255,255,255,0.05)' }}>
                <Table
                    columns={columns}
                    dataSource={data}
                    rowKey="id"
                    pagination={{ ...pagination, style: { marginBottom: 0 } }}
                    loading={loading}
                    onChange={handleTableChange}
                    style={{ background: 'transparent' }}
                    rowClassName={() => 'custom-table-row'}
                    locale={{
                        emptyText: (
                            <Empty
                                image={<NotebookPen className="w-16 h-16 text-slate-600 opacity-20 mx-auto my-6" />}
                                description={
                                    <div className="text-slate-400 pb-4">
                                        <p className="text-lg mb-2 font-medium">暂无文章</p>
                                        <p className="text-sm opacity-60">点击右上方“写新文章”开始你的创作吧</p>
                                    </div>
                                }
                            />
                        ),
                    }}
                />
            </div>
            <style jsx="true">{`
                .custom-table-row:hover > td {
                    background: rgba(255, 255, 255, 0.04) !important;
                    transition: background 0.3s;
                }
                .ant-table-thead > tr > th {
                    background: rgba(0, 0, 0, 0.2) !important;
                    color: #a1a1aa !important;
                    font-weight: 500 !important;
                    border-bottom: 1px solid rgba(255, 255, 255, 0.05) !important;
                }
                .ant-table-tbody > tr > td {
                    border-bottom: 1px solid rgba(255, 255, 255, 0.03) !important;
                }
                .ant-table-wrapper .ant-table {
                    background: transparent !important;
                }
            `}</style>
        </div>
    );
};

export default ArticleList;
