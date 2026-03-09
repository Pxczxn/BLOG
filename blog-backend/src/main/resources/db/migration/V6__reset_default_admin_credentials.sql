-- V6__reset_default_admin_credentials.sql
-- 保证默认管理员账号为: pxczxn / pxczxn

INSERT INTO admin_user (username, email, password_hash, status, role, created_at, updated_at)
SELECT
    'pxczxn',
    'pxczxn@163.com',
    '$2a$10$yo0diFtpfvg6szNljNnakOu3HmmLz5/uuLVV1orkODQVTmnU2sAce',
    'ACTIVE',
    'ADMIN',
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM admin_user WHERE username = 'pxczxn'
);

UPDATE admin_user
SET
    email = 'pxczxn@163.com',
    password_hash = '$2a$10$yo0diFtpfvg6szNljNnakOu3HmmLz5/uuLVV1orkODQVTmnU2sAce',
    status = 'ACTIVE',
    role = 'ADMIN',
    updated_at = NOW()
WHERE username = 'pxczxn';
