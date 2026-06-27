export const ADMIN_TOKEN_KEY = 'admin_token';

let adminAccessToken: string | null = null;

export const getAdminToken = () => adminAccessToken;

export const setAdminToken = (token: string | null) => {
  adminAccessToken = token;
  localStorage.removeItem(ADMIN_TOKEN_KEY);
};

export const clearAdminToken = () => {
  adminAccessToken = null;
  localStorage.removeItem(ADMIN_TOKEN_KEY);
};
