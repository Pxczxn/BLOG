


import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { motion } from 'motion/react';
import { Eye, Clock, MessageSquare, ThumbsUp, ArrowLeft, Users } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import request, { getStaticUrl } from '../lib/request';
import { format } from 'date-fns';
import { useAuth } from '../lib/AuthContext';
import RoleBadge from '../components/RoleBadge';

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

        request.post(`/api/public/community/posts/${slug}/view`).catch(console.error);

        const commentsRes: any = await request.get(`/api/public/community/posts/${slug}/comments`);
        setComments(commentsRes?.data ?? commentsRes ?? []);
      } catch (error) {
        console.error('Failed to fetch community post:', error);
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
      if (isLiked) {
        await request.delete(`/api/community/posts/${post.id}/like`);
        setIsLiked(false);
      } else {
        await request.post(`/api/community/posts/${post.id}/like`);
        setIsLiked(true);
      }
    } catch (error) {
      console.error('Failed to toggle like:', error);
    }
  };

  const handleSubmitComment = async () => {
    if (!post || !user || !commentContent.trim() || submitting) return;
    setSubmitting(true);
    try {
      const res: any = await request.post(`/api/community/posts/${post.slug}/comments`, {
        content: commentContent.trim()
      });
      const newComment = res?.data ?? res;
      setComments(prev => [...prev, newComment]);
      setCommentContent('');
    } catch (error) {
      console.error('Failed to submit comment:', error);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-[50vh] flex items-center justify-center">
        <div className="animate-pulse text-purple-400">正在加载讨论内容...</div>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="min-h-[50vh] flex flex-col items-center justify-center text-slate-400 gap-4">
        <p>帖子不存在或已被删除</p>
        <Link to="/community" className="text-purple-400 hover:underline">返回社区</Link>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <Link to="/community" className="inline-flex items-center gap-2 text-slate-400 hover:text-purple-400 mb-8 transition-colors">
        <ArrowLeft className="w-4 h-4" /> 返回社区
      </Link>

      <motion.article
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <div className="bg-white/[0.02] border border-white/5 rounded-3xl p-6 md:p-10 backdrop-blur-sm mb-8">
          
          <header className="mb-8 border-b border-white/5 pb-8">
            <div className="flex flex-wrap gap-2 mb-4">
              {post.tags?.map((tag: any, i: number) => (
                <span key={i} className="px-2.5 py-1 rounded bg-purple-500/20 border border-purple-500/30 text-xs text-purple-300">
                  {tag.name || tag}
                </span>
              ))}
            </div>

            <h1 className="text-2xl md:text-3xl font-black text-white mb-6 leading-tight">
              {post.title}
            </h1>

            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-slate-800 border-2 border-white/10 flex items-center justify-center overflow-hidden shrink-0">
                {post.author?.avatar ? (
                  <img src={getStaticUrl(post.author.avatar)} alt={post.author.username} className="w-full h-full object-cover" />
                ) : (
                  <Users className="w-6 h-6 text-slate-500" />
                )}
              </div>
              <div>
                <div className="mb-1 flex items-center gap-2">
                  <div className="text-slate-200 font-medium">{post.author?.displayName || post.author?.username || '匿名用户'}</div>
                  <RoleBadge role={post.author?.role} compact />
                </div>
                <div className="flex items-center gap-4 text-xs text-slate-500">
                  <span className="flex items-center gap-1"><Clock className="w-3 h-3" /> {format(new Date(post.createdAt || Date.now()), 'yyyy-MM-dd HH:mm')}</span>
                  <span className="flex items-center gap-1"><Eye className="w-3 h-3" /> {post.viewCount || 0}</span>
                </div>
              </div>
            </div>
          </header>

          
          <div className="prose prose-invert prose-purple max-w-none
                          prose-p:text-slate-300 prose-p:leading-relaxed
                          prose-a:text-purple-400 hover:prose-a:text-purple-300
                          prose-pre:bg-slate-900/80 prose-pre:border prose-pre:border-white/10
                          prose-img:rounded-2xl prose-img:border prose-img:border-white/10">
            <ReactMarkdown remarkPlugins={[remarkGfm]}>
              {post.content || '暂无内容'}
            </ReactMarkdown>
          </div>

          
          <div className="mt-10 pt-6 border-t border-white/5 flex items-center justify-end gap-4">
             <button
               onClick={handleLike}
               disabled={!user}
               className={`flex items-center gap-2 px-4 py-2 rounded-xl transition-colors ${isLiked ? 'bg-purple-500/20 text-purple-400' : 'bg-white/5 hover:bg-white/10 text-slate-300'} disabled:opacity-50`}
             >
               <ThumbsUp className="w-4 h-4" /> {isLiked ? '已赞' : '赞'} ({post.likeCount || 0})
             </button>
          </div>
        </div>

        
        <div className="bg-white/[0.02] border border-white/5 rounded-3xl p-6 md:p-10 backdrop-blur-sm">
          <h3 className="text-xl font-bold text-white mb-8 flex items-center gap-2">
            <MessageSquare className="w-5 h-5 text-purple-400" />
            讨论 ({post.commentCount || comments.length})
          </h3>

          
          <div className="mb-10 flex gap-4">
             <div className="w-10 h-10 rounded-full bg-slate-800 border border-white/10 shrink-0 overflow-hidden flex items-center justify-center">
               {user?.avatar ? <img src={getStaticUrl(user.avatar)} className="w-full h-full object-cover" /> : <Users className="w-5 h-5 text-slate-500" />}
             </div>
             <div className="flex-1 relative">
                <textarea
                  value={commentContent}
                  onChange={e => setCommentContent(e.target.value)}
                  placeholder={user ? "分享你的看法..." : "登录后参与讨论..."}
                  className="w-full bg-black/20 border border-white/10 rounded-xl p-4 text-sm text-white placeholder-slate-500 focus:outline-none focus:border-purple-500/50 min-h-[100px] resize-y disabled:opacity-50"
                  disabled={!user || submitting}
                ></textarea>
                <div className="mt-3 flex justify-end">
                   <button
                     onClick={handleSubmitComment}
                     disabled={!user || !commentContent.trim() || submitting}
                     className="px-6 py-2 bg-purple-600 hover:bg-purple-500 text-white rounded-lg text-sm font-medium transition-all disabled:cursor-not-allowed disabled:opacity-50"
                   >
                     {submitting ? '发送中...' : '发表回复'}
                   </button>
                </div>
             </div>
          </div>

          
          <div className="space-y-6">
            {comments.length > 0 ? comments.map((comment: any) => (
              <div key={comment.id} className="flex gap-4">
                <div className="w-10 h-10 rounded-full bg-slate-800 border border-white/10 shrink-0 overflow-hidden flex items-center justify-center">
                   {comment.author?.avatar ? <img src={getStaticUrl(comment.author.avatar)} className="w-full h-full object-cover" /> : <Users className="w-5 h-5 text-slate-500" />}
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="text-sm font-medium text-slate-200">{comment.author?.username || '用户'}</span>
                    <span className="text-xs text-slate-500">{format(new Date(comment.createdAt || Date.now()), 'MM-dd HH:mm')}</span>
                  </div>
                  <div className="text-sm text-slate-300 leading-relaxed bg-white/5 p-4 rounded-xl rounded-tl-none border border-white/5">
                    {comment.content}
                  </div>
                </div>
              </div>
            )) : (
              <div className="text-center py-10 text-slate-500">
                暂无讨论，快来抢沙发吧
              </div>
            )}
          </div>
        </div>
      </motion.article>
    </div>
  );
}
