INSERT INTO category (name, slug, created_at, updated_at) VALUES
    ('技术开发', 'tech-dev', NOW(), NOW()),
    ('AI开发', 'ai-dev', NOW(), NOW()),
    ('项目记录', 'project-log', NOW(), NOW()),
    ('学习笔记', 'learning-notes', NOW(), NOW()),
    ('随笔', 'essay', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

INSERT INTO tag (name, slug, created_at, updated_at) VALUES
    ('SpringBoot', 'springboot', NOW(), NOW()),
    ('React', 'react', NOW(), NOW()),
    ('Java', 'java', NOW(), NOW()),
    ('JWT', 'jwt', NOW(), NOW()),
    ('MySQL', 'mysql', NOW(), NOW()),
    ('AI', 'ai', NOW(), NOW()),
    ('Claude', 'claude', NOW(), NOW()),
    ('Gemini', 'gemini', NOW(), NOW()),
    ('Vite', 'vite', NOW(), NOW()),
    ('Tailwind', 'tailwind', NOW(), NOW()),
    ('Linux', 'linux', NOW(), NOW()),
    ('Nginx', 'nginx', NOW(), NOW()),
    ('博客系统', 'blog-system', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

