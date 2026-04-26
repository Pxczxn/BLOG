# BLOG

Personal blog and community MVP for publishing articles, curating resources, and running a lightweight admin workflow.

[中文说明](./README.zh-CN.md)

## What Is Included

- Public site: home page, blog list/detail, resource pages, community pages, login and registration.
- Admin console: article management, category/tag management, moderation, dashboard, uploads, and account flows.
- Backend API: Spring Boot service with JWT authentication, Flyway migrations, MySQL persistence, upload handling, and tests.
- Deployment-ready defaults: no public default admin account is seeded by migrations; production admin bootstrap reads environment variables.

## Tech Stack

- Public frontend: React 19, TypeScript, Vite, Tailwind CSS, React Router, Axios.
- Admin frontend: React 19, Vite, Ant Design, Tailwind CSS, React Router, Axios.
- Backend: Java 21, Spring Boot 4, Spring Security, Spring Data JPA, Flyway, MySQL, JJWT.

## Project Structure

```text
blog-backend/    Spring Boot API service
blog-public/     Public website and built-in admin pages
blog-frontend/   Legacy/standalone admin frontend
```

## Local Development

### Backend

```bash
cd blog-backend
copy .env.example .env
```

Fill the empty values in `.env`, then run:

```bash
mvnw spring-boot:run
```

### Public Frontend

```bash
cd blog-public
npm install
npm run dev
```

The public frontend defaults to port `4000`.

### Admin Frontend

```bash
cd blog-frontend
npm install
npm run dev
```

## Production Notes

For production, run the backend with the `prod` profile and provide these variables:

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
INITIAL_ADMIN_USERNAME=
INITIAL_ADMIN_EMAIL=
INITIAL_ADMIN_PASSWORD=
```

`INITIAL_ADMIN_PASSWORD` must be at least 12 characters. The app will create or update the initial admin only when the `prod` profile is active and all three `INITIAL_ADMIN_*` values are present.

## Validation

The publish copy was checked with:

```bash
cd blog-backend
mvnw test
mvnw -DskipTests package
```

Frontend source was also syntax-parsed after comment cleanup.

## License

No license has been declared yet. Add one before accepting external contributions.
