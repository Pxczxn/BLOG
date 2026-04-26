/*
 * 功能：前端模块逻辑。
 */
import React, { useEffect, useState } from 'react';
import { Helmet } from 'react-helmet-async';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { communityApi } from '../../api/community';
import { useAuth } from '../../auth/AuthContext';
import { getStaticUrl } from '../../api/request';

const avatarFallback = (name) => `https://api.dicebear.com/7.x/identicon/svg?seed=${encodeURIComponent(name || 'user')}&backgroundColor=1e1e2e`;

const UserProfilePage = () => {
    const { username } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();
    const [profile, setProfile] = useState(null);
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [followingAction, setFollowingAction] = useState(false);

    useEffect(() => {
        const fetchProfile = async () => {
            setLoading(true);
            setError('');
            try {
                const [profileData, postData] = await Promise.all([
                    communityApi.getPublicProfile(username),
                    communityApi.listPosts({ page: 1, size: 8, username }),
                ]);
                setProfile(profileData);
                setPosts(postData?.items || []);
            } catch (err) {
                setError(err.message || '鍔犺浇鐢ㄦ埛涓婚〉澶辫触銆?);
                setProfile(null);
                setPosts([]);
            } finally {
                setLoading(false);
            }
        };

        if (username) {
            fetchProfile();
        }
    }, [username]);

    const handleFollow = async () => {
        if (!profile || followingAction) return;
        if (!user) {
            navigate('/login');
            return;
        }
        if (user.username === profile.username) return;
        setFollowingAction(true);
        try {
            const next = profile.followedByMe
                ? await communityApi.unfollowUser(profile.username)
                : await communityApi.followUser(profile.username);
            setProfile((prev) => ({
                ...prev,
                followedByMe: next.following,
                followerCount: next.followerCount,
                followingCount: prev.followingCount,
            }));
        } catch (err) {
            window.alert(err.message || '鏇存柊鍏虫敞鐘舵€佸け璐ャ€?);
        } finally {
            setFollowingAction(false);
        }
    };

    if (loading) {
        return <div className="py-24 text-center text-slate-500">鍔犺浇涓?..</div>;
    }

    if (!profile) {
        return <div className="py-24 text-center text-slate-500">{error || '鐢ㄦ埛涓嶅瓨鍦ㄣ€?}</div>;
    }

    const isSelf = user && user.username === profile.username;

    return (
        <section className="space-y-8">
            <Helmet>
                <title>{profile.displayName} - 绀惧尯涓婚〉</title>
            </Helmet>

            <div className="rounded-[2rem] border border-white/5 bg-white/[0.03] p-8 shadow-2xl sm:p-10">
                <div className="flex flex-col gap-6 sm:flex-row sm:items-start">
                    <img
                        src={getStaticUrl(profile.avatar) || avatarFallback(profile.username)}
                        alt={profile.displayName}
                        className="h-24 w-24 rounded-3xl border border-white/10 bg-slate-800 object-cover"
                    />
                    <div className="flex-1">
                        <div className="flex flex-wrap items-start justify-between gap-4">
                            <div>
                                <p className="text-xs uppercase tracking-[0.3em] text-sky-300/80">绀惧尯涓婚〉</p>
                                <h1 className="mt-3 text-3xl font-bold text-slate-100">{profile.displayName}</h1>
                                <p className="mt-2 text-sm text-slate-400">@{profile.username}</p>
                            </div>
                            {!isSelf && (
                                <button
                                    type="button"
                                    onClick={handleFollow}
                                    disabled={followingAction}
                                    className={`rounded-2xl px-4 py-2 text-sm font-semibold transition ${
                                        profile.followedByMe
                                            ? 'border border-white/10 text-slate-200 hover:bg-white/5'
                                            : 'bg-cyan-500 text-slate-950 hover:bg-cyan-400'
                                    }`}
                                >
                                    {profile.followedByMe ? '宸插叧娉? : '鍏虫敞'}
                                </button>
                            )}
                        </div>

                        <div className="mt-4 flex gap-6 text-xs uppercase tracking-[0.18em] text-slate-500">
                            <span>{profile.followerCount || 0} 浣嶅叧娉ㄨ€?/span>
                            <span>{profile.followingCount || 0} 涓叧娉ㄤ腑</span>
                        </div>

                        <p className="mt-5 whitespace-pre-wrap text-sm leading-7 text-slate-300">
                            {profile.bio || '杩欎釜浜鸿繕娌℃湁濉啓涓汉绠€浠嬨€?}
                        </p>
                        {profile.website && (
                            <a href={profile.website} target="_blank" rel="noreferrer" className="mt-5 inline-flex text-sm text-sky-300 hover:text-sky-200">
                                {profile.website}
                            </a>
                        )}
                    </div>
                </div>
            </div>

            <div className="rounded-[2rem] border border-white/5 bg-white/[0.03] p-8 shadow-2xl sm:p-10">
                <div className="flex items-center justify-between">
                    <h2 className="text-2xl font-semibold text-slate-100">甯栧瓙</h2>
                    <span className="text-sm text-slate-500">{posts.length} 鏉?/span>
                </div>
                <div className="mt-6 space-y-4">
                    {posts.length ? posts.map((post) => (
                        <article key={post.id} className="rounded-2xl border border-white/5 bg-black/20 p-5">
                            <div className="flex flex-wrap items-center gap-3 text-xs uppercase tracking-[0.18em] text-slate-500">
                                <Link to={`/community/node/${post.node.slug}`} className="text-cyan-300 hover:text-cyan-200">{post.node.name}</Link>
                                <span>{post.likeCount || 0} 娆＄偣璧?/span>
                                <span>{post.favoriteCount || 0} 娆℃敹钘?/span>
                            </div>
                            <Link to={`/community/post/${post.slug}`} className="mt-3 block text-xl font-semibold text-slate-100 hover:text-cyan-300">
                                {post.title}
                            </Link>
                            <p className="mt-3 text-sm leading-7 text-slate-400">{post.summary || '鏆傛棤鎽樿銆?}</p>
                        </article>
                    )) : (
                        <div className="rounded-2xl border border-dashed border-white/10 p-8 text-center text-slate-500">杩樻病鏈夊叕寮€甯栧瓙銆?/div>
                    )}
                </div>
                <div className="mt-8 border-t border-white/5 pt-6">
                    <Link to="/community" className="text-sm text-slate-400 hover:text-slate-200">鍥炲埌绀惧尯</Link>
                </div>
            </div>
        </section>
    );
};

export default UserProfilePage;

