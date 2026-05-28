import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { motion } from 'motion/react';
import { Eye, Clock, MessageSquare, ThumbsUp, ArrowLeft, Users } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { format } from 'date-fns';
import toast from 'react-hot-toast';
import Seo from '../components/Seo';
import request, { getStaticUrl } from '../lib/request';
import { useAuth } from '../lib/AuthContext';
import RoleBadge from '../components/RoleBadge';
import { buildBreadcrumbJsonLd, buildMetaDescription, toAbsoluteUrl } from '../lib/siteSettings';

export default function CommunityPostDetail() {
  const { slug } = useParams();
  const { user } = useAuth();
  const [post, setPost] = useState<any>(null);
  const [comments, setComments] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [isLiked, setIsLiked] = useState(false);
  const [commentContent, setCommentContent] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const fetchPost = async () => {
      try {
        setLoading(true);
        const res: any = await request.get(`/api/public/community/posts/${slug}`);
        const data = res?.data ?? res;
        setPost(data);
        setIsLiked(Boolean(data?.likedByMe));

        request.post(`/api/public/community/posts/${slug}/view`).catch(console.error);

        const commentsRes: any = await request.get(`/api/public/community/posts/${slug}/comments`);
        setComments(commentsRes?.data ?? commentsRes ?? []);
      } catch (error) {
        console.error('Failed to fetch community post:', error);
        setPost(null);
      } finally {
        setLoading(false);
      }
    };

    if (slug) {
      fetchPost();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }, [slug]);

  const handleLike = async () => {
    if (!post || !user) return;
    try {
      let res: any;
      if (isLiked) {
        res = await request.delete(`/api/community/posts/${post.id}/likes`);
        setIsLiked(false);
      } else {
        res = await request.post(`/api/community/posts/${post.id}/likes`);
        setIsLiked(true);
      }
      const interaction = res?.data ?? res;
      if (interaction) {
        setPost((current: any) => current ? {
          ...current,
          likeCount: interaction.likeCount ?? current.likeCount,
          favoriteCount: interaction.favoriteCount ?? current.favoriteCount,
        } : current);
        setIsLiked(Boolean(interaction.likedByMe));
      }
    } catch (error) {
      console.error('Failed to toggle like:', error);
    }
  };

  const handleSubmitComment = async () => {
    if (!post || !user || !commentContent.trim() || submitting) return;
    setSubmitting(true);
    try {
      await request.post(`/api/community/posts/${post.id}/comments`, {
        content: commentContent.trim(),
      });
      setCommentContent('');
      toast.success('评论已提交，审核通过后会显示', { duration: 1800 });
    } catch (error) {
      console.error('Failed to submit comment:', error);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <div className="animate-pulse text-purple-400">正在加载讨论内容...</div>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="flex min-h-[50vh] flex-col items-center justify-center gap-4 text-slate-400">
        <Seo title="帖子不存在" description="请求的社区帖子不存在或已被删除。" path={slug ? `/community/post/${slug}` : '/community'} noindex />
        <p>帖子不存在或已被删除</p>
        <Link to="/community" className="text-purple-400 hover:underline">返回社区</Link>
      </div>
    );
  }

  const postPath = `/community/post/${post.slug || slug}`;
  const postDescription = buildMetaDescription(post.summary || post.content);
  const postTags = Array.isArray(post.tags)
    ? post.tags.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || '')).filter(Boolean)
    : [];
  const breadcrumbJsonLd = buildBreadcrumbJsonLd([
    { name: '首页', path: '/' },
    { name: '社区', path: '/community' },
    { name: post.title, path: postPath },
  ]);
  const discussionJsonLd = {
    '@context': 'https://schema.org',
    '@type': 'DiscussionForumPosting',
    headline: post.title,
    description: postDescription,
    datePublished: post.publishedAt || post.createdAt,
    dateModified: post.updatedAt || post.createdAt,
    mainEntityOfPage: toAbsoluteUrl(postPath),
    author: {
      '@type': 'Person',
      name: post.author?.displayName || post.author?.username || '社区用户',
    },
    keywords: postTags,
    interactionStatistic: [
      {
        '@type': 'InteractionCounter',
        interactionType: 'https://schema.org/LikeAction',
        userInteractionCount: post.likeCount || 0,
      },
      {
        '@type': 'InteractionCounter',
        interactionType: 'https://schema.org/CommentAction',
        userInteractionCount: post.commentCount || comments.length,
      },
    ],
  };

  return (
    <div className="mx-auto max-w-4xl px-4 py-10 sm:px-6 lg:px-8">
      <Seo
        title={post.title}
        description={postDescription}
        path={postPath}
        type="article"
        publishedTime={post.publishedAt || post.createdAt}
        modifiedTime={post.updatedAt || post.createdAt}
        author={post.author?.displayName || post.author?.username || '社区用户'}
        tags={postTags}
        jsonLd={[breadcrumbJsonLd, discussionJsonLd]}
      />

      <Link to="/community" className="mb-8 inline-flex items-center gap-2 text-slate-400 transition-colors hover:text-purple-400">
        <ArrowLeft className="h-4 w-4" /> 返回社区
      </Link>

      <motion.article initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
        <div className="mb-8 rounded-3xl border border-white/5 bg-white/[0.02] p-6 backdrop-blur-sm md:p-10">
          <header className="mb-8 border-b border-white/5 pb-8">
            <div className="mb-4 flex flex-wrap gap-2">
              {postTags.map((tag: string) => (
                <span key={tag} className="rounded border border-purple-500/30 bg-purple-500/20 px-2.5 py-1 text-xs text-purple-300">
                  {tag}
                </span>
              ))}
            </div>

            <h1 className="mb-6 text-2xl font-black leading-tight text-white md:text-3xl">
              {post.title}
            </h1>

            <div className="flex items-center gap-4">
              <div className="flex h-12 w-12 shrink-0 items-center justify-center overflow-hidden rounded-full border-2 border-white/10 bg-slate-800">
                {post.author?.avatar ? (
                  <img src={getStaticUrl(post.author.avatar)} alt={post.author.username} className="h-full w-full object-cover" />
                ) : (
                  <Users className="h-6 w-6 text-slate-500" />
                )}
              </div>
              <div>
                <div className="mb-1 flex items-center gap-2">
                  <div className="font-medium text-slate-200">{post.author?.displayName || post.author?.username || '匿名用户'}</div>
                  <RoleBadge role={post.author?.role} compact />
                </div>
                <div className="flex items-center gap-4 text-xs text-slate-500">
                  <span className="flex items-center gap-1"><Clock className="h-3 w-3" /> {format(new Date(post.createdAt || Date.now()), 'yyyy-MM-dd HH:mm')}</span>
                  <span className="flex items-center gap-1"><Eye className="h-3 w-3" /> {post.viewCount || 0}</span>
                </div>
              </div>
            </div>
          </header>

          <div className="prose prose-invert prose-purple max-w-none prose-p:text-slate-300 prose-p:leading-relaxed prose-a:text-purple-400 hover:prose-a:text-purple-300 prose-pre:bg-slate-900/80 prose-pre:border prose-pre:border-white/10 prose-img:rounded-2xl prose-img:border prose-img:border-white/10">
            <ReactMarkdown remarkPlugins={[remarkGfm]}>
              {post.content || '暂无内容'}
            </ReactMarkdown>
          </div>

          <div className="mt-10 flex items-center justify-end gap-4 border-t border-white/5 pt-6">
            <button
              onClick={handleLike}
              disabled={!user}
              className={`flex items-center gap-2 rounded-xl px-4 py-2 transition-colors ${
                isLiked ? 'bg-purple-500/20 text-purple-400' : 'bg-white/5 text-slate-300 hover:bg-white/10'
              } disabled:opacity-50`}
            >
              <ThumbsUp className="h-4 w-4" /> {isLiked ? '已赞' : '赞'} ({post.likeCount || 0})
            </button>
          </div>
        </div>

        <div className="rounded-3xl border border-white/5 bg-white/[0.02] p-6 backdrop-blur-sm md:p-10">
          <h3 className="mb-8 flex items-center gap-2 text-xl font-bold text-white">
            <MessageSquare className="h-5 w-5 text-purple-400" />
            讨论 ({post.commentCount || comments.length})
          </h3>

          <div className="mb-10 flex gap-4">
            <div className="flex h-10 w-10 shrink-0 items-center justify-center overflow-hidden rounded-full border border-white/10 bg-slate-800">
              {user?.avatar ? <img src={getStaticUrl(user.avatar)} className="h-full w-full object-cover" /> : <Users className="h-5 w-5 text-slate-500" />}
            </div>
            <div className="flex-1">
              <textarea
                value={commentContent}
                onChange={(e) => setCommentContent(e.target.value)}
                placeholder={user ? '分享你的看法...' : '登录后参与讨论...'}
                className="min-h-[100px] w-full resize-y rounded-xl border border-white/10 bg-black/20 p-4 text-sm text-white placeholder-slate-500 focus:border-purple-500/50 focus:outline-none disabled:opacity-50"
                disabled={!user || submitting}
              />
              <div className="mt-3 flex justify-end">
                <button
                  onClick={handleSubmitComment}
                  disabled={!user || !commentContent.trim() || submitting}
                  className="rounded-lg bg-purple-600 px-6 py-2 text-sm font-medium text-white transition-all disabled:cursor-not-allowed disabled:opacity-50 hover:bg-purple-500"
                >
                  {submitting ? '发送中...' : '发表评论'}
                </button>
              </div>
            </div>
          </div>

          <div className="space-y-6">
            {comments.length > 0 ? comments.map((comment: any) => (
              <div key={comment.id} className="flex gap-4">
                <div className="flex h-10 w-10 shrink-0 items-center justify-center overflow-hidden rounded-full border border-white/10 bg-slate-800">
                  {comment.avatar ? <img src={getStaticUrl(comment.avatar)} className="h-full w-full object-cover" /> : <Users className="h-5 w-5 text-slate-500" />}
                </div>
                <div className="flex-1">
                  <div className="mb-1 flex items-center gap-2">
                    <span className="text-sm font-medium text-slate-200">{comment.profileUsername || comment.nickname || '用户'}</span>
                    <span className="text-xs text-slate-500">{format(new Date(comment.createdAt || Date.now()), 'MM-dd HH:mm')}</span>
                  </div>
                  <div className="rounded-xl rounded-tl-none border border-white/5 bg-white/5 p-4 text-sm leading-relaxed text-slate-300">
                    {comment.content}
                  </div>
                </div>
              </div>
            )) : (
              <div className="py-10 text-center text-slate-500">
                暂无讨论，快来抢沙发吧
              </div>
            )}
          </div>
        </div>
      </motion.article>
    </div>
  );
}
