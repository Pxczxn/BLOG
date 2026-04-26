


export const ADMIN_TOKEN_KEY = 'admin_token';


export const getAdminToken = () => localStorage.getItem(ADMIN_TOKEN_KEY);


export const setAdminToken = (token: string | null) => {
  if (token) {
    localStorage.setItem(ADMIN_TOKEN_KEY, token);
    return;
  }
  localStorage.removeItem(ADMIN_TOKEN_KEY);
};


export const clearAdminToken = () => {
  localStorage.removeItem(ADMIN_TOKEN_KEY);
};
