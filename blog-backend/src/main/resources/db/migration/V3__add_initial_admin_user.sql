-- 功能：数据库迁移脚本。
-- 添加初始管理员账号：破星辰只寻你 / Pxczxnpxczxn (密码已使用 BCrypt 加密)

INSERT INTO admin_user (username, email, password_hash, status, role, created_at, updated_at)
VALUES (
    '破星辰只寻你',
    'Pxczxn@163.com',
    '$2a$10$dE2S5XOgzqmpH2EJwn27CuNcnZTEwzyOUf7FOzJ1h1s1vvBHhhMvG',  -- BCrypt 加密后的 'Pxczxnpxczxn'
    'ACTIVE',
    'ADMIN',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    updated_at = NOW();
