import { useEffect, useState } from 'react';
import { FileText, Users, Eye, MessageSquare, Flame } from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import request from '../../lib/request';
import AdminPageHeader from '../../components/admin/AdminPageHeader';

const emptyData = [
  { name: '周一', value: 0 },
  { name: '周二', value: 0 },
  { name: '周三', value: 0 },
  { name: '周四', value: 0 },
  { name: '周五', value: 0 },
  { name: '周六', value: 0 },
  { name: '周日', value: 0 },
];

export default function Dashboard() {
  const [overview, setOverview] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchOverview = async () => {
      try {
        const res: any = await request.get('/api/admin/community/interactions/overview');
        setOverview(res?.data ?? res);
      } catch (error) {
        
      } finally {
        setLoading(false);
      }
    };

    fetchOverview();
  }, []);

  return (
    <div className="space-y-6">
      <AdminPageHeader title="仪表盘" />

      <div className="mt-16 grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
        <MetricCard title="总点赞数" value={loading ? '--' : overview?.totalLikes || 0} icon={FileText} color="blue" />
        <MetricCard title="总收藏数" value={loading ? '--' : overview?.totalFavorites || 0} icon={Users} color="emerald" />
        <MetricCard title="总关注数" value={loading ? '--' : overview?.totalFollows || 0} icon={Eye} color="orange" />
        <MetricCard title="未读通知" value={loading ? '--' : overview?.unreadNotifications || 0} icon={MessageSquare} color="pink" />
      </div>

      <div className="mt-6 grid grid-cols-1 gap-6 lg:grid-cols-2">
        <div className="rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-md">
          <h3 className="relative mb-6 inline-block text-sm font-semibold uppercase tracking-wider text-white">
            热门帖子排行
            <span className="absolute -bottom-2 left-0 h-0.5 w-1/2 bg-blue-500" />
          </h3>
          <div className="flex max-h-72 flex-col gap-4 overflow-y-auto pr-2">
            {overview?.topHotPosts?.length > 0 ? (
              overview.topHotPosts.map((post: any) => (
                <div
                  key={post.postId}
                  className="flex items-center justify-between rounded-xl border border-white/5 bg-white/5 p-3 transition-colors hover:bg-white/10"
                >
                  <div className="min-w-0 flex-1 pr-4">
                    <h4 className="truncate text-sm font-medium text-white">{post.title}</h4>
                    <p className="mt-1 text-xs text-slate-400">ID: {post.postId} · 状态: {post.status}</p>
                  </div>
                  <div className="shrink-0 text-xs font-medium text-slate-300">
                    <span className="flex items-center gap-1.5">
                      <Flame className="h-3 w-3 text-orange-400" />
                      {post.heatScore}
                    </span>
                  </div>
                </div>
              ))
            ) : (
              <div className="rounded-xl border border-dashed border-white/5 py-20 text-center text-sm text-slate-500">
                {loading ? '加载中...' : '暂无热门帖子'}
              </div>
            )}
          </div>
        </div>

        <div className="rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-md">
          <h3 className="relative mb-6 inline-block text-sm font-semibold uppercase tracking-wider text-white">
            文章发布情况
            <span className="absolute -bottom-2 left-0 h-0.5 w-1/2 bg-purple-500" />
          </h3>
          <div className="relative h-72 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={emptyData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" vertical={false} />
                <XAxis dataKey="name" stroke="rgba(255,255,255,0.2)" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis stroke="rgba(255,255,255,0.2)" fontSize={12} tickLine={false} axisLine={false} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'rgba(15, 23, 42, 0.9)',
                    border: '1px solid rgba(255,255,255,0.1)',
                    borderRadius: '8px',
                  }}
                  cursor={{ fill: 'rgba(255,255,255,0.05)' }}
                />
                <Bar dataKey="value" fill="#a855f7" radius={[4, 4, 0, 0]} maxBarSize={40} />
              </BarChart>
            </ResponsiveContainer>
            <div className="absolute inset-0 flex items-center justify-center">
              <span className="rounded-lg border border-white/5 bg-black/40 px-4 py-2 text-sm text-slate-400 backdrop-blur-sm">
                当前后端还没有提供图表数据
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function MetricCard({
  title,
  value,
  icon: Icon,
  color,
}: {
  title: string;
  value: string | number;
  icon: any;
  color: 'blue' | 'emerald' | 'orange' | 'pink';
}) {
  const colorStyles = {
    blue: 'from-blue-500/20 text-blue-400 border-blue-500/30',
    emerald: 'from-emerald-500/20 text-emerald-400 border-emerald-500/30',
    orange: 'from-orange-500/20 text-orange-400 border-orange-500/30',
    pink: 'from-pink-500/20 text-pink-400 border-pink-500/30',
  };

  return (
    <div className="group rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-md transition-colors hover:bg-white/10">
      <div className="mb-4 flex items-center justify-between">
        <div className={`flex h-10 w-10 items-center justify-center rounded-xl border bg-gradient-to-br shadow-lg ${colorStyles[color]}`}>
          <Icon className="h-5 w-5" />
        </div>
      </div>
      <div>
        <p className="mb-1 inline-block origin-left text-3xl font-bold text-white transition-transform group-hover:scale-105">
          {value}
        </p>
        <p className="text-sm uppercase tracking-wider text-slate-500">{title}</p>
      </div>
    </div>
  );
}
