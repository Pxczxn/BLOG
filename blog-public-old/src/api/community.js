/*
 * 功能：前端模块逻辑。
 */
import request from './request';

export const COMMUNITY_TOKEN_KEY = 'community_token';

export const communityApi = {
    async register(payload) {
        const response = await request.post('/api/community/auth/register', payload);
        return response.data || response;
    },
    async login(payload) {
        const response = await request.post('/api/community/auth/login', payload);
        return response.data || response;
    },
    async logout() {
        const response = await request.post('/api/community/logout');
        return response.data || response;
    },
    async me() {
        const response = await request.get('/api/community/me');
        return response.data || response;
    },
    async updateProfile(payload) {
        const response = await request.patch('/api/community/me', payload);
        return response.data || response;
    },
    async getPublicProfile(username) {
        const response = await request.get(`/api/public/users/${username}`);
        return response.data || response;
    },
    async listNodes() {
        const response = await request.get('/api/public/community/nodes');
        return response.data || response || [];
    },
    async getNode(slug) {
        const response = await request.get(`/api/public/community/nodes/${slug}`);
        return response.data || response;
    },
    async listPosts(params = {}) {
        const response = await request.get('/api/public/community/posts', { params });
        return response.data || response;
    },
    async getPost(slug) {
        const response = await request.get(`/api/public/community/posts/${slug}`);
        return response.data || response;
    },
    async listPostComments(slug) {
        const response = await request.get(`/api/public/community/posts/${slug}/comments`);
        return response.data || response || [];
    },
    async createPostComment(postId, payload) {
        const response = await request.post(`/api/community/posts/${postId}/comments`, payload);
        return response.data || response;
    },
    async incrementPostView(slug) {
        const response = await request.post(`/api/public/community/posts/${slug}/view`);
        return response.data || response;
    },
    async createPost(payload) {
        const response = await request.post('/api/community/posts', payload);
        return response.data || response;
    },
    async updatePost(id, payload) {
        const response = await request.put(`/api/community/posts/${id}`, payload);
        return response.data || response;
    },
    async getPostEditor(id) {
        const response = await request.get(`/api/community/posts/${id}`);
        return response.data || response;
    },
    async getMyPosts() {
        const response = await request.get('/api/community/posts/mine');
        return response.data || response || [];
    },
    async likePost(postId) {
        const response = await request.post(`/api/community/posts/${postId}/likes`);
        return response.data || response;
    },
    async unlikePost(postId) {
        const response = await request.delete(`/api/community/posts/${postId}/likes`);
        return response.data || response;
    },
    async favoritePost(postId) {
        const response = await request.post(`/api/community/posts/${postId}/favorites`);
        return response.data || response;
    },
    async unfavoritePost(postId) {
        const response = await request.delete(`/api/community/posts/${postId}/favorites`);
        return response.data || response;
    },
    async getPostInteraction(postId) {
        const response = await request.get(`/api/community/posts/${postId}/interaction`);
        return response.data || response;
    },
    async getPostInteractionPublic(postId) {
        const response = await request.get(`/api/community/posts/${postId}/interaction`);
        return response.data || response;
    },
    async followUser(username) {
        const response = await request.post(`/api/community/users/${username}/follow`);
        return response.data || response;
    },
    async unfollowUser(username) {
        const response = await request.delete(`/api/community/users/${username}/follow`);
        return response.data || response;
    },
    async getMyFavorites(params = {}) {
        const response = await request.get('/api/community/me/favorites', { params });
        return response.data || response;
    },
    async getNotifications(params = {}) {
        const response = await request.get('/api/community/notifications', { params });
        return response.data || response;
    },
    async readNotification(id) {
        const response = await request.post(`/api/community/notifications/${id}/read`);
        return response.data || response;
    },
    async readAllNotifications() {
        const response = await request.post('/api/community/notifications/read-all');
        return response.data || response;
    },
    async createReport(payload) {
        const response = await request.post('/api/community/reports', payload);
        return response.data || response;
    },
    async uploadAvatar(file) {
        const formData = new FormData();
        formData.append('file', file);
        const response = await request.post('/api/community/upload/avatar', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });
        return response.data || response;
    },
};

