export const toRelativeMediaUrl = (url) => {
    if (!url || typeof url !== 'string') return '';
    if (url.startsWith('data:')) return url;
    if (/^https?:\/\//i.test(url)) {
        try {
            const parsed = new URL(url);
            return `${parsed.pathname}${parsed.search}${parsed.hash}`;
        } catch {
            return '';
        }
    }
    return url.startsWith('/') ? url : `/${url}`;
};
