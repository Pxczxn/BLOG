-- 功能：数据库迁移脚本。
-- 保证默认管理员账号为: 破星辰只寻你 / Pxczxnpxczxn

INSERT INTO admin_user (username, email, password_hash, status, role, created_at, updated_at)
SELECT
    '破星辰只寻你',
    'Pxczxn@163.com',
    '$2a$10$dE2S5XOgzqmpH2EJwn27CuNcnZTEwzyOUf7FOzJ1h1s1vvBHhhMvG',
    'ACTIVE',
    'ADMIN',
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM admin_user WHERE username = '破星辰只寻你'
);

UPDATE admin_user
SET
    email = 'Pxczxn@163.com',
    password_hash = '$2a$10$dE2S5XOgzqmpH2EJwn27CuNcnZTEwzyOUf7FOzJ1h1s1vvBHhhMvG',
    status = 'ACTIVE',
    role = 'ADMIN',
    updated_at = NOW()
WHERE username = '破星辰只寻你';
