-- 功能：数据库迁移脚本。
-- 更新管理员账号为: 破星辰只寻你 / Pxczxnpxczxn

-- 先更新文章的 author_id 指向新用户（如果旧用户存在）
UPDATE article
SET author_id = (
    SELECT id FROM admin_user WHERE username = '破星辰只寻你'
)
WHERE author_id = (
    SELECT id FROM admin_user WHERE username = 'pxczxn'
) AND EXISTS (
    SELECT 1 FROM admin_user WHERE username = '破星辰只寻你'
);

-- 删除旧的管理员账号（如果存在）
DELETE FROM admin_user WHERE username = 'pxczxn';

-- 插入或更新新的管理员账号
INSERT INTO admin_user (username, email, password_hash, status, role, created_at, updated_at)
VALUES (
    '破星辰只寻你',
    'Pxczxn@163.com',
    '$2a$10$dE2S5XOgzqmpH2EJwn27CuNcnZTEwzyOUf7FOzJ1h1s1vvBHhhMvG',
    'ACTIVE',
    'ADMIN',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    password_hash = VALUES(password_hash),
    status = VALUES(status),
    role = VALUES(role),
    updated_at = NOW();
