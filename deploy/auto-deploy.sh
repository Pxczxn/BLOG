#!/usr/bin/env bash
set -euo pipefail

# Required
: "${SERVER_HOST:?Please set SERVER_HOST}"
: "${DOMAIN:?Please set DOMAIN}"
: "${DB_PASSWORD:?Please set DB_PASSWORD}"
: "${JWT_SECRET:?Please set JWT_SECRET}"

# Optional
SERVER_USER="${SERVER_USER:-root}"
SSH_KEY_PATH="${SSH_KEY_PATH:-}"
REMOTE_DIR="${REMOTE_DIR:-/app/blog}"
LOCAL_BACKEND_JAR="${LOCAL_BACKEND_JAR:-./blog-backend/target/back-blog-0.0.1-SNAPSHOT.jar}"
LOCAL_PUBLIC_DIST="${LOCAL_PUBLIC_DIST:-./blog-public/dist}"
LOCAL_ADMIN_DIST="${LOCAL_ADMIN_DIST:-./blog-frontend/dist}"

SSH_OPTS="-o StrictHostKeyChecking=accept-new"
if [[ -n "$SSH_KEY_PATH" ]]; then
  SSH_OPTS="$SSH_OPTS -i $SSH_KEY_PATH"
fi

REMOTE="$SERVER_USER@$SERVER_HOST"

echo "[1/6] Prepare remote directories"
ssh $SSH_OPTS "$REMOTE" "mkdir -p $REMOTE_DIR/backend/config $REMOTE_DIR/frontend/admin-pxczxn $REMOTE_DIR/upload/{avatars,covers,misc}"

echo "[2/6] Upload backend jar"
scp $SSH_OPTS "$LOCAL_BACKEND_JAR" "$REMOTE:$REMOTE_DIR/backend/back-blog-0.0.1-SNAPSHOT.jar"

echo "[3/6] Upload frontend dist"
ssh $SSH_OPTS "$REMOTE" "rm -rf $REMOTE_DIR/frontend/*"
scp $SSH_OPTS -r "$LOCAL_PUBLIC_DIST"/* "$REMOTE:$REMOTE_DIR/frontend/"
scp $SSH_OPTS -r "$LOCAL_ADMIN_DIST"/* "$REMOTE:$REMOTE_DIR/frontend/admin-pxczxn/"

echo "[4/6] Write backend config"
ssh $SSH_OPTS "$REMOTE" "cat > $REMOTE_DIR/backend/config/application-prod.yml <<EOF
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pxczxn-blog?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
    username: blog_user
    password: $DB_PASSWORD
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

jwt:
  secret: $JWT_SECRET
  expiration: 86400000

app:
  cors:
    allowed-origins: https://$DOMAIN,http://$DOMAIN

file:
  upload-dir: /app/blog/upload
EOF"

echo "[5/6] Install nginx config"
scp $SSH_OPTS ./deploy/nginx.conf "$REMOTE:/tmp/blog-nginx.conf"
ssh $SSH_OPTS "$REMOTE" "sed -e 's/your-domain.com/$DOMAIN/g' /tmp/blog-nginx.conf > /etc/nginx/sites-available/$DOMAIN && ln -sf /etc/nginx/sites-available/$DOMAIN /etc/nginx/sites-enabled/$DOMAIN && nginx -t && systemctl reload nginx"

echo "[6/6] Restart backend service"
ssh $SSH_OPTS "$REMOTE" "systemctl restart blog-backend && systemctl is-active blog-backend"

echo "Deployment finished"
