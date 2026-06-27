ALTER TABLE device_session
    ADD COLUMN refresh_token_hash VARCHAR(128) NULL AFTER is_active,
    ADD COLUMN refresh_token_expires_at DATETIME NULL AFTER refresh_token_hash,
    ADD INDEX idx_device_session_refresh_token_hash (refresh_token_hash),
    ADD INDEX idx_device_session_active_refresh_expires (is_active, refresh_token_expires_at);
