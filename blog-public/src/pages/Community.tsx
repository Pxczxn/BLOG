import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'motion/react';
import { MessageSquare, Users, ThumbsUp, Eye } from 'lucide-react';
import request from '../lib/request';
import RoleBadge from '../components/RoleBadge';
import EmptyState from '../components/EmptyState';
import { useAuth } from '../lib/AuthContext';

type CommunityNode = {
  id: number;
  name: string;
  slug: string;
  description?: string;
  postCount?: number;
};

type Topic = {
  id: string;
  slug: string;
  title: string;
  node?: {
    name: string;
    slug: string;
  };
  tags: string[];
  author: string;
  authorRole?: string;
  time: string;
  replies: number;
  likes: number;
  views: number;
};

export default function Community() {
  const [topics, setTopics] = useState<Topic[]>([]);
  const [nodes, setNodes] = useState<CommunityNode[]>([]);
  const [selectedNode, setSelectedNode] = useState('all');
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();
  const publishPath = user ? '/community/new' : '/login?redirect=/community/new';

  useEffect(() => {
    const fetchNodes = async () => {
      try {
        const resp = await request.get('/api/public/community/nodes');
        const data = resp?.data ?? resp ?? [];
        setNodes(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error('Failed to fetch community nodes:', error);
        setNodes([]);
      }
    };

    fetchNodes();
  }, []);

  useEffect(() => {
    const fetchTopics = async () => {
      try {
        setLoading(true);
        const params: Record<string, string | number> = { page: 1, size: 10 };
        if (selectedNode !== 'all') {
          params.node = selectedNode;
        }

        const resp = await request.get('/api/public/community/posts', { params });
        const page = resp?.data ?? resp ?? { items: [] };
        const items = Array.isArray(page.items) ? page.items : [];

        const normalized: Topic[] = items.map((item: any) => ({
          id: item.id || item.slug,
          slug: item.slug || String(item.id),
          title: item.title,
          node: item.node
            ? {
                name: item.node.name || item.node.slug,
                slug: item.node.slug,
              }
            : undefined,
          tags: Array.isArray(item.tags)
            ? item.tags.map((t: any) => t.name || t).filter(Boolean)
            : [],
          author: item.author?.displayName || item.author?.username || '匿名',
          authorRole: item.author?.role,
          time: item.createdAt
            ? new Date(item.createdAt).toLocaleDateString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
              })
            : '未知时间',
          replies: item.commentCount || 0,
          likes: item.likeCount || 0,
          views: item.viewCount || 0,
        }));

        setTopics(normalized);
      } catch (error) {
        console.error('Failed to fetch topics:', error);
        setTopics([]);
      } finally {
        setLoading(false);
      }
    };

    fetchTopics();
  }, [selectedNode]);

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:px-8">
      <div className="mb-8 flex items-center justify-between gap-4">
        <div>
          <motion.h1
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className="text-3xl font-black text-white drop-shadow-[0_0_15px_rgba(168,85,247,0.4)]"
          >
            社区
          </motion.h1>
          <motion.p
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.1 }}
            className="mt-2 text-sm text-slate-400"
          >
            把问题、灵感和项目进展放在这里，慢慢沉淀成可回看的讨论记录。
          </motion.p>
        </div>

        <Link to={publishPath}>
          <motion.button
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            className="flex items-center gap-2 rounded-full border border-purple-400/30 bg-purple-600 px-6 py-2 font-medium text-white shadow-[0_0_20px_rgba(168,85,247,0.4)] transition-all hover:bg-purple-500"
          >
            <MessageSquare className="h-4 w-4" />
            发布帖子
          </motion.button>
        </Link>
      </div>

      <div className="mb-6 flex flex-wrap gap-2">
        <FilterChip
          active={selectedNode === 'all'}
          onClick={() => setSelectedNode('all')}
          label="全部节点"
        />
        {nodes.map((node) => (
          <FilterChip
            key={node.slug}
            active={selectedNode === node.slug}
            onClick={() => setSelectedNode(node.slug)}
            label={node.name}
            count={node.postCount && node.postCount > 0 ? node.postCount : undefined}
          />
        ))}
      </div>

      <div className="relative z-10 overflow-hidden rounded-2xl border border-white/10 bg-white/5 backdrop-blur-xl">
        <div className="border-b border-white/10 px-6 py-4 text-sm font-medium">
          <span className="text-purple-300">最新发布</span>
        </div>

        <div className="flex min-h-[300px] flex-col">
          {loading ? (
            <div className="flex flex-1 items-center justify-center">
              <p className="text-slate-500">加载中...</p>
            </div>
          ) : topics.length > 0 ? (
            topics.map((topic, index) => (
              <Link to={`/community/post/${topic.slug}`} key={topic.id}>
                <motion.div
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.05 }}
                  className="flex cursor-pointer flex-col justify-between gap-4 border-b border-white/5 px-6 py-5 transition-colors hover:bg-white/5 md:flex-row md:items-center"
                >
                  <div className="flex flex-1 gap-4 items-start md:items-center">
                    <div className="flex h-10 w-10 shrink-0 items-center justify-center overflow-hidden rounded-full border border-white/10 bg-slate-800">
                      <Users className="h-5 w-5 text-slate-500" />
                    </div>
                    <div>
                      <div className="mb-1 flex flex-wrap items-center gap-2">
                        {topic.node?.name && (
                          <span className="rounded bg-white/10 px-2 py-0.5 text-[10px] text-slate-300 border border-white/10">
                            {topic.node.name}
                          </span>
                        )}
                        {topic.tags.map((tag: string) => (
                          <span
                            key={tag}
                            className="rounded border border-purple-500/30 bg-purple-500/20 px-2 py-0.5 text-[10px] text-purple-300"
                          >
                            {tag}
                          </span>
                        ))}
                        <span className="ml-2 hidden text-xs text-slate-500 md:inline-flex">
                          {topic.author} · {topic.time}
                        </span>
                        <RoleBadge role={topic.authorRole} compact />
                      </div>
                      <h3 className="text-base font-bold text-slate-200 transition-colors group-hover:text-purple-300">
                        {topic.title}
                      </h3>
                      <span className="mt-2 inline-block text-xs text-slate-500 md:hidden">
                        来自 {topic.author}
                      </span>
                    </div>
                  </div>

                  <div className="ml-14 flex items-center gap-6 text-sm text-slate-500 md:ml-0">
                    <div className="flex flex-col items-center gap-1 md:flex-row md:gap-1.5">
                      <span className="flex items-center gap-1">
                        <MessageSquare className="hidden w-4 h-4 md:block" />
                        {topic.replies}
                      </span>
                      <span className="text-[10px] md:hidden">回复</span>
                    </div>
                    <div className="flex flex-col items-center gap-1 md:flex-row md:gap-1.5">
                      <span className="flex items-center gap-1">
                        <ThumbsUp className="hidden w-4 h-4 md:block" />
                        {topic.likes}
                      </span>
                      <span className="text-[10px] md:hidden">点赞</span>
                    </div>
                    <div className="hidden flex-col items-center gap-1 md:flex md:flex-row md:gap-1.5">
                      <span className="flex items-center gap-1">
                        <Eye className="hidden w-4 h-4 md:block" />
                        {topic.views}
                      </span>
                      <span className="text-[10px] md:hidden">浏览</span>
                    </div>
                  </div>
                </motion.div>
              </Link>
            ))
          ) : (
            <EmptyState
              icon={MessageSquare}
              title="还没有帖子，先开个头吧"
              description="可以抛一个问题、记一段踩坑经历，或者分享最近正在打磨的小功能。"
              actions={[{ label: user ? '发布帖子' : '登录后发布', to: publishPath }]}
            />
          )}
        </div>
      </div>
    </div>
  );
}

function FilterChip({
  active,
  onClick,
  label,
  count,
}: {
  active: boolean;
  onClick: () => void;
  label: string;
  count?: number;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={[
        'inline-flex items-center gap-2 rounded-full border px-4 py-2 text-sm transition-all',
        active
          ? 'border-purple-400/50 bg-purple-500/20 text-purple-200 shadow-[0_0_20px_rgba(168,85,247,0.15)]'
          : 'border-white/10 bg-white/5 text-slate-400 hover:border-white/20 hover:text-white',
      ].join(' ')}
    >
      <span>{label}</span>
      {typeof count === 'number' && (
        <span className="rounded-full bg-black/20 px-2 py-0.5 text-[10px] text-slate-300">
          {count}
        </span>
      )}
    </button>
  );
}
