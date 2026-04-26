





const rawBase = import.meta.env.BASE_URL || '/';





export const adminBasePath = rawBase === '/' ? '' : rawBase.replace(/\/$/, '');






export const adminAssetPath = (path) => {
  
  const normalizedPath = path.replace(/^\/+/, '');
  
  const prefix = rawBase.endsWith('/') ? rawBase : `${rawBase}/`;
  return `${prefix}${normalizedPath}`;
};

