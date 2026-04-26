




import React, { useState } from 'react';
import { Form, Input, Button, Card, App } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { setAdminToken } from '../../auth/storage';
import request from '../../utils/request';






const Login = () => {
    const { message } = App.useApp();
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const location = useLocation();

    




    const onFinish = async (values) => {
        setLoading(true);
        try {
            const res = await request.post('/api/admin/login', values);
            const token = res?.data?.token;

            if (!token) {
                message.error('登录失败：未获取到 Token');
                return;
            }

            setAdminToken(token);
            message.success('登录成功');
            navigate(location.state?.from?.pathname || '/');
        } catch (error) {
            console.error(error);
            message.error(error?.response?.data?.message || '登录失败');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div
            className="animate-in fade-in duration-700"
            style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100vh',
                width: '100vw',
                background: '#030712',
            }}
        >
            <Card
                style={{
                    width: 400,
                    padding: '16px 8px',
                    textAlign: 'center',
                    background: '#09090b',
                    border: '1px solid #27272a',
                    borderRadius: 16,
                    boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)',
                }}
                variant="borderless"
            >
                <div style={{ marginBottom: 36 }}>
                    <div
                        style={{
                            width: 48,
                            height: 48,
                            margin: '0 auto 24px',
                            background: '#fafafa',
                            borderRadius: 12,
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                        }}
                    >
                        <span style={{ color: '#09090b', fontWeight: 'bold', fontSize: 20 }}>PX</span>
                    </div>
                    <h2 style={{ color: '#fafafa', margin: 0, fontSize: 24, fontWeight: 600, letterSpacing: '-0.5px' }}>欢迎回来</h2>
                    <p style={{ color: '#a1a1aa', margin: '8px 0 0', fontSize: 14 }}>请登录以管理你的博客内容</p>
                </div>
                <Form name="login" onFinish={onFinish} size="large">
                    <Form.Item
                        name="username"
                        rules={[{ required: true, message: '请输入账号' }]}
                    >
                        <Input
                            prefix={<UserOutlined style={{ color: '#71717a' }} />}
                            placeholder="管理员账号"
                            style={{ background: '#09090b', color: '#fafafa', borderColor: '#27272a', padding: '10px 14px' }}
                        />
                    </Form.Item>
                    <Form.Item
                        name="password"
                        rules={[{ required: true, message: '请输入密码' }]}
                    >
                        <Input.Password
                            prefix={<LockOutlined style={{ color: '#71717a' }} />}
                            placeholder="管理员密码"
                            style={{ background: '#09090b', color: '#fafafa', borderColor: '#27272a', padding: '10px 14px' }}
                        />
                    </Form.Item>
                    <Form.Item style={{ marginTop: 32, marginBottom: 0 }}>
                        <Button
                            type="primary"
                            htmlType="submit"
                            block
                            loading={loading}
                            style={{
                                background: '#fafafa',
                                color: '#09090b',
                                borderColor: '#fafafa',
                                height: 44,
                                fontSize: 15,
                                fontWeight: 600,
                                borderRadius: 8,
                            }}
                        >
                            登录系统
                        </Button>
                    </Form.Item>
                </Form>
            </Card>
        </div>
    );
};

export default Login;
