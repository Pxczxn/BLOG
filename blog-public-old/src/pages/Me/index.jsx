/*
 * 功能：前端模块逻辑。
 */
import React, { useEffect, useRef, useState } from 'react';
import { Helmet } from 'react-helmet-async';
import { Link, Navigate } from 'react-router-dom';
import { communityApi } from '../../api/community';
import { useAuth } from '../../auth/AuthContext';
import { getStaticUrl } from '../../api/request';
import AvatarCropper from '../../components/AvatarCropper';

const formatPostStatus = (status) => {
    switch (status) {
        case 'PUBLISHED':
            return '宸插彂甯?;
        case 'DRAFT':
            return '鑽夌';
        case 'PENDING':
            return '寰呭鏍?;
        case 'REJECTED':
            return '鏈€氳繃';
        default:
            return status || '鏈煡鐘舵€?;
    }
};

const MePage = () => {
    const { user, initializing, refreshUser } = useAuth();
    const [posts, setPosts] = useState([]);
    const [favorites, setFavorites] = useState([]);
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [form, setForm] = useState({
        displayName: '',
        bio: '',
        avatar: '',
        website: '',
    });
    const [saving, setSaving] = useState(false);
    const [uploading, setUploading] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [cropImage, setCropImage] = useState(null);
    const fileInputRef = useRef(null);

    useEffect(() => {
        if (user) {
            setForm({
                displayName: user.displayName || '',
                bio: user.bio || '',
                avatar: user.avatar || '',
                website: user.website || '',
            });
        }
    }, [user]);

    useEffect(() => {
        const bootstrap = async () => {
            if (!user) return;
            try {
                const [myPosts, favoriteData, notificationData] = await Promise.all([
                    communityApi.getMyPosts(),
                    communityApi.getMyFavorites({ page: 1, size: 6 }),
                    communityApi.getNotifications({ page: 1, size: 8 }),
                ]);
                setPosts(Array.isArray(myPosts) ? myPosts : []);
                setFavorites(favoriteData?.items || favoriteData?.page?.items || []);
                const notificationItems = notificationData?.page?.items || [];
                setNotifications(notificationItems);
                setUnreadCount(notificationData?.unreadCount || 0);
            } catch (err) {
                console.error('鍔犺浇涓汉涓績澶辫触', err);
            }
        };
        bootstrap();
    }, [user]);

    if (!initializing && !user) {
        return <Navigate to="/login" replace />;
    }

    const handleSubmit = async (event) => {
        event.preventDefault();
        setSaving(true);
        setMessage('');
        setError('');
        try {
            await communityApi.updateProfile(form);
            await refreshUser();
            setMessage('涓汉璧勬枡宸叉洿鏂般€?);
        } catch (err) {
            setError(err.message || '鏇存柊涓汉璧勬枡澶辫触銆?);
        } finally {
            setSaving(false);
        }
    };

    const markAllNotificationsRead = async () => {
        try {
            await communityApi.readAllNotifications();
            setNotifications((current) => current.map((item) => ({ ...item, read: true })));
            setUnreadCount(0);
        } catch (err) {
            setError(err.message || '閫氱煡宸茶澶辫触銆?);
        }
    };

    const handleAvatarUpload = async (event) => {
        const file = event.target.files?.[0];
        if (!file) return;

        if (file.size > 2 * 1024 * 1024) {
            setError('鍥剧墖澶у皬涓嶈兘瓒呰繃 2MB');
            return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
            setCropImage(e.target.result);
        };
        reader.readAsDataURL(file);

        if (fileInputRef.current) {
            fileInputRef.current.value = '';
        }
    };

    const handleCropComplete = async (croppedBlob) => {
        setCropImage(null);
        setUploading(true);
        setError('');
        try {
            const file = new File([croppedBlob], 'avatar.png', { type: 'image/png' });
            const result = await communityApi.uploadAvatar(file);
            setForm((prev) => ({ ...prev, avatar: result.url }));
        } catch (err) {
            setError(err.message || '澶村儚涓婁紶澶辫触銆?);
        } finally {
            setUploading(false);
        }
    };

    const handleCropCancel = () => {
        setCropImage(null);
    };

    return (
        <section className="space-y-8">
            <Helmet>
                <title>鎴戠殑涓婚〉 - 绀惧尯</title>
            </Helmet>

            <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-8 shadow-2xl sm:p-10">
                <div className="flex flex-col gap-3 border-b border-white/5 pb-8 sm:flex-row sm:items-end sm:justify-between">
                    <div>
                        <p className="text-xs uppercase tracking-[0.3em] text-amber-300/80">韬唤淇℃伅</p>
                        <h1 className="mt-3 text-3xl font-bold text-slate-100">{user?.displayName || user?.username}</h1>
                        <p className="mt-2 text-sm text-slate-400">@{user?.username} | {user?.email}</p>
                    </div>
                    <div className="flex gap-3">
                        <Link to={`/u/${user?.username}`} className="text-sm text-amber-200 hover:text-amber-100">鍏紑涓婚〉</Link>
                        <Link to="/community/new" className="text-sm text-cyan-300 hover:text-cyan-200">鍐欏笘瀛?/Link>
                    </div>
                </div>

                <form onSubmit={handleSubmit} className="mt-8 space-y-5">
                    <div className="flex items-center gap-6">
                        <div className="relative flex h-20 w-20 items-center justify-center overflow-hidden rounded-full border-2 border-white/10 bg-slate-800 text-2xl font-bold text-white">
                            {form.avatar ? (
                                <>
                                    <span>{(user?.displayName || user?.username || '?').charAt(0).toUpperCase()}</span>
                                    <img
                                        src={getStaticUrl(form.avatar)}
                                        alt="澶村儚"
                                        className="absolute inset-0 h-full w-full object-cover transition-opacity"
                                        onLoad={(e) => { e.target.style.opacity = '1'; }}
                                        onError={(e) => { e.target.style.opacity = '0'; }}
                                    />
                                </>
                            ) : (
                                <span className="text-sm font-normal text-slate-500">鏃犲ご鍍?/span>
                            )}
                        </div>
                        <div>
                            <input
                                ref={fileInputRef}
                                type="file"
                                accept="image/png,image/jpeg,image/webp,image/gif"
                                onChange={handleAvatarUpload}
                                className="hidden"
                            />
                            <button
                                type="button"
                                onClick={() => fileInputRef.current?.click()}
                                disabled={uploading}
                                className="rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-300 transition hover:bg-white/10 disabled:opacity-60"
                            >
                                {uploading ? '涓婁紶涓?..' : '涓婁紶澶村儚'}
                            </button>
                            <p className="mt-2 text-xs text-slate-500">鏀寔 PNG銆丣PG銆乄ebP銆丟IF锛屾渶澶?2MB</p>
                        </div>
                    </div>
                    <input
                        type="text"
                        placeholder="鏄电О"
                        value={form.displayName}
                        onChange={(event) => setForm({ ...form, displayName: event.target.value })}
                        className="w-full rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-amber-400/50"
                    />
                    <input
                        type="url"
                        placeholder="涓汉缃戠珯锛堥€夊～锛?
                        value={form.website}
                        onChange={(event) => setForm({ ...form, website: event.target.value })}
                        className="w-full rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-amber-400/50"
                    />
                    <textarea
                        rows={6}
                        placeholder="涓汉绠€浠?
                        value={form.bio}
                        onChange={(event) => setForm({ ...form, bio: event.target.value })}
                        className="w-full rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-amber-400/50"
                    />
                    <button type="submit" disabled={saving} className="rounded-2xl border border-cyan-500/30 bg-cyan-500/10 px-6 py-3 text-sm font-semibold text-cyan-400 transition hover:bg-cyan-500/20 hover:border-cyan-500/50 hover:text-cyan-300 disabled:opacity-60">
                        {saving ? '淇濆瓨涓?..' : '淇濆瓨璧勬枡'}
                    </button>
                </form>

                {message && <p className="mt-4 text-sm text-emerald-300">{message}</p>}
                {error && <p className="mt-4 text-sm text-rose-300">{error}</p>}
            </div>

            <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-8 shadow-2xl sm:p-10">
                <div className="flex items-center justify-between">
                    <h2 className="text-2xl font-semibold text-slate-100">閫氱煡</h2>
                    <div className="flex items-center gap-3">
                        <span className="text-sm text-slate-500">{unreadCount} 鏉℃湭璇?/span>
                        <button type="button" onClick={markAllNotificationsRead} className="text-sm text-cyan-300 hover:text-cyan-200">鍏ㄩ儴鏍囪宸茶</button>
                    </div>
                </div>
                <div className="mt-6 space-y-3">
                    {notifications.length ? notifications.map((item) => (
                        <div key={item.id} className={`rounded-2xl border p-4 ${item.read ? 'border-white/5 bg-black/20' : 'border-cyan-400/30 bg-cyan-500/10'}`}>
                            <p className="text-sm font-semibold text-slate-100">{item.title}</p>
                            {item.content && <p className="mt-2 text-sm text-slate-300">{item.content}</p>}
                        </div>
                    )) : (
                        <div className="rounded-2xl border border-dashed border-white/10 p-8 text-center text-slate-500">杩樻病鏈夐€氱煡銆?/div>
                    )}
                </div>
            </div>

            <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-8 shadow-2xl sm:p-10">
                <div className="flex items-center justify-between">
                    <h2 className="text-2xl font-semibold text-slate-100">鎴戠殑鏀惰棌</h2>
                    <span className="text-sm text-slate-500">{favorites.length} 鏉?/span>
                </div>
                <div className="mt-6 space-y-4">
                    {favorites.length ? favorites.map((post) => (
                        <article key={post.id} className="rounded-2xl border border-white/5 bg-black/20 p-5">
                            <div className="flex flex-wrap items-center gap-3 text-xs uppercase tracking-[0.18em] text-slate-500">
                                <span>{post.node?.name}</span>
                                <span>{post.likeCount || 0} 娆＄偣璧?/span>
                                <span>{post.favoriteCount || 0} 娆℃敹钘?/span>
                            </div>
                            <Link to={`/community/post/${post.slug}`} className="mt-3 block text-lg font-semibold text-slate-100 hover:text-cyan-300">
                                {post.title}
                            </Link>
                            <p className="mt-3 text-sm leading-7 text-slate-400">{post.summary || '鏆傛棤鎽樿銆?}</p>
                        </article>
                    )) : (
                        <div className="rounded-2xl border border-dashed border-white/10 p-8 text-center text-slate-500">杩樻病鏈夋敹钘忕殑甯栧瓙銆?/div>
                    )}
                </div>
            </div>

            <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-8 shadow-2xl sm:p-10">
                <div className="flex items-center justify-between">
                    <h2 className="text-2xl font-semibold text-slate-100">鎴戠殑绀惧尯甯栧瓙</h2>
                    <span className="text-sm text-slate-500">{posts.length} 鏉?/span>
                </div>
                <div className="mt-6 space-y-4">
                    {posts.length ? posts.map((post) => (
                        <article key={post.id} className="rounded-2xl border border-white/5 bg-black/20 p-5">
                            <div className="flex flex-wrap items-center justify-between gap-3">
                                <div>
                                    <h3 className="text-lg font-semibold text-slate-100">{post.title}</h3>
                                    <p className="mt-2 text-xs uppercase tracking-[0.18em] text-slate-500">{post.node?.name} | {formatPostStatus(post.status)}</p>
                                </div>
                                <div className="flex gap-3 text-sm">
                                    {post.status === 'PUBLISHED' && (
                                        <Link to={`/community/post/${post.slug}`} className="text-cyan-300 hover:text-cyan-200">鏌ョ湅</Link>
                                    )}
                                    <Link to={`/community/edit/${post.id}`} className="text-amber-200 hover:text-amber-100">缂栬緫</Link>
                                </div>
                            </div>
                        </article>
                    )) : (
                        <div className="rounded-2xl border border-dashed border-white/10 p-8 text-center text-slate-500">杩樻病鏈夊彂甯冭繃甯栧瓙銆?/div>
                    )}
                </div>
            </div>

            {cropImage && (
                <AvatarCropper
                    image={cropImage}
                    onCrop={handleCropComplete}
                    onCancel={handleCropCancel}
                />
            )}
        </section>
    );
};

export default MePage;

