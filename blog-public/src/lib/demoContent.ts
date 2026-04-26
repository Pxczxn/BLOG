import type { Article } from "../components/ArticleCard";

export const demoArticles: Article[] = [
    {
        id: "demo-react-architecture",
        title: "从零整理一个 React 博客前台：页面、数据流和组件边界",
        coverImage:
            "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=900&q=80",
        tags: ["React", "前端架构"],
        views: 1280,
        publishedAt: "2026/04/25",
        isPinned: true,
    },
    {
        id: "demo-spring-community",
        title: "博客系统升级社区：用户、帖子、审核流应该如何拆分",
        coverImage:
            "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=900&q=80",
        tags: ["Spring Boot", "社区"],
        views: 864,
        publishedAt: "2026/04/22",
    },
    {
        id: "demo-design-system",
        title: "给个人项目建立一套可持续迭代的暗色视觉系统",
        coverImage:
            "https://images.unsplash.com/photo-1558655146-9f40138edfeb?auto=format&fit=crop&w=900&q=80",
        tags: ["设计系统", "Tailwind"],
        views: 642,
        publishedAt: "2026/04/18",
    },
];

export const demoCategories = [
    { name: "前端开发", articleCount: 8 },
    { name: "后端工程", articleCount: 6 },
    { name: "项目复盘", articleCount: 5 },
    { name: "工具效率", articleCount: 3 },
];

export const demoTags = [
    "React",
    "TypeScript",
    "Spring Boot",
    "MySQL",
    "设计系统",
    "社区产品",
    "部署",
    "性能优化",
];

export const demoTopics = [
    {
        id: "demo-topic-frontend-start",
        title: "前端先从哪里改最舒服？先把首页、导航和空状态跑顺",
        tags: ["前端", "规划"],
        author: "Pxczxn",
        time: "今天",
        replies: 12,
        likes: 34,
        views: 268,
    },
    {
        id: "demo-topic-content-system",
        title: "博客文章和社区帖子要不要共用一张表？",
        tags: ["后端", "建模"],
        author: "developer",
        time: "昨天",
        replies: 8,
        likes: 21,
        views: 193,
    },
    {
        id: "demo-topic-review-flow",
        title: "开放社区之前，审核中心最少需要哪些能力？",
        tags: ["社区", "审核"],
        author: "moderator",
        time: "04/23",
        replies: 5,
        likes: 16,
        views: 156,
    },
];
