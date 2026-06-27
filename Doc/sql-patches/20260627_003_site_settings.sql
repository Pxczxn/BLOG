CREATE TABLE IF NOT EXISTS site_setting (
    setting_key VARCHAR(80) PRIMARY KEY COMMENT 'Setting key',
    setting_value TEXT NOT NULL COMMENT 'Setting value',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Site settings';

INSERT INTO site_setting (setting_key, setting_value)
VALUES
    ('siteName', '破星辰只寻你'),
    ('siteSub', '在代码的星河中，寻找技术与自由'),
    ('adminNick', '破星辰只寻你'),
    ('keywords', '博客,前端,React,Java,Spring Boot,AI Coding,个人网站'),
    ('description', '一个记录真实项目、踩坑复盘、工程实践与持续构建过程的个人博客。'),
    ('allowGuest', 'false'),
    ('homeTitle', '探索技术边界'),
    ('homeIntro', '分享前端开发、系统设计与工程化实践，也记录一个开发者不断打磨作品的过程。'),
    ('aboutBio', '一个正在学习与探索 AI Coding 的全栈开发者。这里会记录技术、设计和持续构建过程中的思考与实践。'),
    ('aboutBioSub', '这个博客会更偏向真实项目、踩坑记录、审美迭代，以及把想法真正落成作品的过程。'),
    ('frontend', 'React / Vue / TypeScript / Tailwind CSS'),
    ('backend', 'Java / Spring Boot / Node.js'),
    ('engineering', 'Vite / Webpack / Git'),
    ('other', 'MySQL / Redis / Linux')
ON DUPLICATE KEY UPDATE setting_value = setting_value;
