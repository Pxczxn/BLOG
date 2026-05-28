import { useEffect, useMemo, useState } from 'react';
import { Helmet } from 'react-helmet-async';
import { useLocation } from 'react-router-dom';
import {
  buildMetaDescription,
  DEFAULT_SITE_SETTINGS,
  normalizeKeywords,
  readSiteSettings,
  toAbsoluteUrl,
} from '../lib/siteSettings';

type JsonLdValue = Record<string, unknown> | Array<Record<string, unknown>>;

type SeoProps = {
  title?: string;
  description?: string;
  keywords?: string | string[];
  image?: string | null;
  type?: 'website' | 'article' | 'profile';
  path?: string;
  noindex?: boolean;
  author?: string;
  publishedTime?: string;
  modifiedTime?: string;
  tags?: string[];
  jsonLd?: JsonLdValue;
};

export default function Seo({
  title,
  description,
  keywords,
  image,
  type = 'website',
  path,
  noindex = false,
  author,
  publishedTime,
  modifiedTime,
  tags = [],
  jsonLd,
}: SeoProps) {
  const location = useLocation();
  const [settings, setSettings] = useState(DEFAULT_SITE_SETTINGS);

  useEffect(() => {
    setSettings(readSiteSettings());
  }, []);

  const pagePath = path ?? location.pathname;
  const canonicalUrl = useMemo(() => toAbsoluteUrl(pagePath), [pagePath]);
  const metaTitle = title ? `${title} | ${settings.siteName}` : settings.siteName;
  const metaDescription = buildMetaDescription(description, settings.description);
  const metaKeywords = normalizeKeywords(keywords || settings.keywords);
  const metaImage = image ? toAbsoluteUrl(image) : undefined;
  const robots = noindex
    ? 'noindex, nofollow, noarchive'
    : 'index, follow, max-snippet:-1, max-image-preview:large, max-video-preview:-1';
  const twitterCard = metaImage ? 'summary_large_image' : 'summary';
  const jsonLdItems = Array.isArray(jsonLd) ? jsonLd : jsonLd ? [jsonLd] : [];

  return (
    <Helmet>
      <title>{metaTitle}</title>
      <link rel="canonical" href={canonicalUrl} />

      <meta name="description" content={metaDescription} />
      <meta name="keywords" content={metaKeywords} />
      <meta name="robots" content={robots} />
      <meta name="author" content={author || settings.adminNick} />
      <meta name="theme-color" content="#030014" />

      <meta property="og:locale" content="zh_CN" />
      <meta property="og:site_name" content={settings.siteName} />
      <meta property="og:type" content={type} />
      <meta property="og:title" content={metaTitle} />
      <meta property="og:description" content={metaDescription} />
      <meta property="og:url" content={canonicalUrl} />
      {metaImage ? <meta property="og:image" content={metaImage} /> : null}

      <meta name="twitter:card" content={twitterCard} />
      <meta name="twitter:title" content={metaTitle} />
      <meta name="twitter:description" content={metaDescription} />
      {metaImage ? <meta name="twitter:image" content={metaImage} /> : null}

      {publishedTime ? <meta property="article:published_time" content={publishedTime} /> : null}
      {modifiedTime ? <meta property="article:modified_time" content={modifiedTime} /> : null}
      {author ? <meta property="article:author" content={author} /> : null}
      {tags.map((tag) => (
        <meta key={tag} property="article:tag" content={tag} />
      ))}

      {jsonLdItems.map((item, index) => (
        <script key={`jsonld-${index}`} type="application/ld+json">
          {JSON.stringify(item)}
        </script>
      ))}
    </Helmet>
  );
}
