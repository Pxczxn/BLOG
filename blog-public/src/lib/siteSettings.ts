import request from './request';

export type SiteSettings = {
  siteName: string;
  siteSub: string;
  adminNick: string;
  keywords: string;
  description: string;
  allowGuest: boolean;
  homeTitle: string;
  homeIntro: string;
  aboutBio: string;
  aboutBioSub: string;
  frontend: string;
  backend: string;
  engineering: string;
  other: string;
};

export type AboutSettings = {
  bio: string;
  bioSub: string;
  frontend: string;
  backend: string;
  engineering: string;
  other: string;
};

export const SITE_SETTINGS_STORAGE_KEY = 'pxczxn_site_settings';
export const ABOUT_SETTINGS_STORAGE_KEY = 'pxczxn_about_settings';

export const DEFAULT_SITE_SETTINGS: SiteSettings = {
  siteName: '破星辰只寻你',
  siteSub: '在代码的星河中，寻找技术与自由',
  adminNick: '破星辰只寻你',
  keywords: '博客,前端,React,Java,Spring Boot,AI Coding,个人网站',
  description: '一个记录真实项目、踩坑复盘、工程实践与持续构建过程的个人博客。',
  allowGuest: false,
  homeTitle: '探索技术边界',
  homeIntro: '分享前端开发、系统设计与工程化实践，也记录一个开发者不断打磨作品的过程。',
  aboutBio: '一个正在学习与探索 AI Coding 的全栈开发者。这里会记录技术、设计和持续构建过程中的思考与实践。',
  aboutBioSub: '这个博客会更偏向真实项目、踩坑记录、审美迭代，以及把想法真正落成作品的过程。',
  frontend: 'React / Vue / TypeScript / Tailwind CSS',
  backend: 'Java / Spring Boot / Node.js',
  engineering: 'Vite / Webpack / Git',
  other: 'MySQL / Redis / Linux',
};

export const DEFAULT_ABOUT_SETTINGS: AboutSettings = toAboutSettings(DEFAULT_SITE_SETTINGS);

const DEFAULT_SITE_ORIGIN = 'https://pxczxn.top';

function readStoredObject<T extends Record<string, unknown>>(key: string): Partial<T> | null {
  if (typeof window === 'undefined') {
    return null;
  }

  try {
    const raw = window.localStorage.getItem(key);
    if (!raw) {
      return null;
    }
    const parsed = JSON.parse(raw);
    return parsed && typeof parsed === 'object' ? parsed as Partial<T> : null;
  } catch {
    return null;
  }
}

function normalizeSettings(value?: Partial<SiteSettings> | null): SiteSettings {
  return {
    ...DEFAULT_SITE_SETTINGS,
    ...(value ?? {}),
  };
}

export function toAboutSettings(settings: SiteSettings): AboutSettings {
  return {
    bio: settings.aboutBio,
    bioSub: settings.aboutBioSub,
    frontend: settings.frontend,
    backend: settings.backend,
    engineering: settings.engineering,
    other: settings.other,
  };
}

export function mergeAboutSettings(settings: SiteSettings, about: AboutSettings): SiteSettings {
  return {
    ...settings,
    aboutBio: about.bio,
    aboutBioSub: about.bioSub,
    frontend: about.frontend,
    backend: about.backend,
    engineering: about.engineering,
    other: about.other,
  };
}

export function readSiteSettings(): SiteSettings {
  return normalizeSettings(readStoredObject<SiteSettings>(SITE_SETTINGS_STORAGE_KEY));
}

export function saveSiteSettings(settings: SiteSettings) {
  if (typeof window === 'undefined') {
    return;
  }
  window.localStorage.setItem(SITE_SETTINGS_STORAGE_KEY, JSON.stringify(settings));
  window.localStorage.setItem(ABOUT_SETTINGS_STORAGE_KEY, JSON.stringify(toAboutSettings(settings)));
}

export function readAboutSettings(): AboutSettings {
  const siteSettings = readSiteSettings();
  return {
    ...toAboutSettings(siteSettings),
    ...(readStoredObject<AboutSettings>(ABOUT_SETTINGS_STORAGE_KEY) ?? {}),
  };
}

export function saveAboutSettings(settings: AboutSettings) {
  if (typeof window === 'undefined') {
    return;
  }
  window.localStorage.setItem(ABOUT_SETTINGS_STORAGE_KEY, JSON.stringify(settings));
  saveSiteSettings(mergeAboutSettings(readSiteSettings(), settings));
}

export async function fetchSiteSettings(): Promise<SiteSettings> {
  const response = await request.get('/api/public/site-settings');
  const settings = normalizeSettings((response as any)?.data ?? response);
  saveSiteSettings(settings);
  return settings;
}

export async function fetchAdminSiteSettings(): Promise<SiteSettings> {
  const response = await request.get('/api/admin/site-settings');
  const settings = normalizeSettings((response as any)?.data ?? response);
  saveSiteSettings(settings);
  return settings;
}

export async function updateAdminSiteSettings(settings: SiteSettings): Promise<SiteSettings> {
  const response = await request.put('/api/admin/site-settings', settings);
  const saved = normalizeSettings((response as any)?.data ?? response);
  saveSiteSettings(saved);
  return saved;
}

export function getSiteOrigin() {
  const envOrigin = (import.meta.env.VITE_SITE_URL as string | undefined)?.trim()?.replace(/\/+$/, '');
  if (typeof window !== 'undefined' && window.location?.origin) {
    return window.location.origin.replace(/\/+$/, '');
  }
  return envOrigin || DEFAULT_SITE_ORIGIN;
}

export function toAbsoluteUrl(path?: string | null) {
  if (!path) {
    return getSiteOrigin();
  }
  if (/^https?:\/\//i.test(path)) {
    return path;
  }
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  return `${getSiteOrigin()}${normalizedPath}`;
}

export function toPlainText(value?: string | null) {
  if (!value) {
    return '';
  }

  return value
    .replace(/```[\s\S]*?```/g, ' ')
    .replace(/`([^`]+)`/g, '$1')
    .replace(/!\[.*?\]\(.*?\)/g, ' ')
    .replace(/\[(.*?)\]\(.*?\)/g, '$1')
    .replace(/[#>*_~\-]+/g, ' ')
    .replace(/<[^>]+>/g, ' ')
    .replace(/\s+/g, ' ')
    .trim();
}

export function buildMetaDescription(value?: string | null, fallback?: string, maxLength = 160) {
  const source = toPlainText(value) || fallback || DEFAULT_SITE_SETTINGS.description;
  if (source.length <= maxLength) {
    return source;
  }
  return `${source.slice(0, maxLength - 1).trim()}…`;
}

export function normalizeKeywords(keywords?: string | string[] | null) {
  if (!keywords) {
    return DEFAULT_SITE_SETTINGS.keywords;
  }
  if (Array.isArray(keywords)) {
    return keywords.filter(Boolean).join(',');
  }
  return keywords
    .split(/[,，]/)
    .map((item) => item.trim())
    .filter(Boolean)
    .join(',');
}

export function buildBreadcrumbJsonLd(items: Array<{ name: string; path: string }>) {
  return {
    '@context': 'https://schema.org',
    '@type': 'BreadcrumbList',
    itemListElement: items.map((item, index) => ({
      '@type': 'ListItem',
      position: index + 1,
      name: item.name,
      item: toAbsoluteUrl(item.path),
    })),
  };
}
