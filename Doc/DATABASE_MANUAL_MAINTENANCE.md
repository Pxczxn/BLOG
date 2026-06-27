# 数据库手动维护规范

项目已经移除 Flyway，应用启动时不会再自动校验或迁移数据库。后续数据库变更统一走手动 SQL。

## 目录约定

- `blog-backend/src/main/resources/db/init_full.sql`
  - 新环境初始化脚本。
  - 只用于空库初始化，不在已有线上库重复执行。
- `Doc/sql-patches/`
  - 后续升级脚本归档目录。
  - 每个脚本必须带日期、序号和简短说明。

推荐命名：

```text
Doc/sql-patches/20260627_001_add_article_seo_fields.sql
Doc/sql-patches/20260627_002_add_audit_log.sql
```

## 编写规则

1. 脚本必须尽量幂等。
2. 修改表结构前先确认字段或索引是否存在。
3. 危险操作必须拆成两步，例如先新增字段、回填数据，再另一次发布删除旧字段。
4. 不把真实密码、密钥、Token 写进 SQL。
5. 每个脚本顶部写清楚用途、执行环境和回滚思路。

模板：

```sql
-- Purpose: add SEO fields to article.
-- Target: pxczxn-blog
-- Safe to rerun: yes
-- Rollback: drop the added nullable columns after confirming code no longer reads them.

SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'article'
      AND COLUMN_NAME = 'seo_title'
);

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE article ADD COLUMN seo_title VARCHAR(255) NULL AFTER summary',
    'SELECT "Column article.seo_title already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
```

## 执行流程

1. 先备份：

```bash
/opt/blog/scripts/backup-live.sh
```

2. 上传 SQL 到服务器，例如：

```bash
scp Doc/sql-patches/20260627_001_add_article_seo_fields.sql root@115.191.48.35:/opt/blog/releases/
```

3. 在服务器执行：

```bash
mysql --database=pxczxn-blog < /opt/blog/releases/20260627_001_add_article_seo_fields.sql
```

4. 验证表结构和应用健康：

```bash
mysql --database=pxczxn-blog -e 'DESCRIBE article;'
/opt/blog/scripts/health-check.sh
```

## 注意

- 不再依赖 `flyway_schema_history` 判断数据库状态。
- 线上库里已有的 `flyway_schema_history` 表可以保留，不影响应用。
- 删除表、删除字段、批量更新数据前必须先做数据库备份。
