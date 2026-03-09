-- V3__add_initial_admin_user.sql
-- 添加初始管理员账号：pxczxn / pxczxn (密码已使用 BCrypt 加密)

INSERT INTO admin_user (username, email, password_hash, status, role, created_at, updated_at)
VALUES (
    'pxczxn',
    'pxczxn@163.com',
    '$2a$10$yo0diFtpfvg6szNljNnakOu3HmmLz5/uuLVV1orkODQVTmnU2sAce',  -- BCrypt 加密后的 'pxczxn'
    'ACTIVE',
    'ADMIN',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    updated_at = NOW();
