import { FileText, Users, Eye, MessageSquare } from 'lucide-react';
import { AreaChart, Area, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export default function Dashboard() {
  // 空数据状态，展示坐标轴骨架
  const emptyData = [
    { name: '周一', value: 0 },
    { name: '周二', value: 0 },
    { name: '周三', value: 0 },
    { name: '周四', value: 0 },
    { name: '周五', value: 0 },
    { name: '周六', value: 0 },
    { name: '周日', value: 0 },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between mb-2 min-h-[40px]">
        <h1 className="text-2xl font-bold text-white">仪表盘</h1>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <MetricCard title="文章总数" value="--" icon={FileText} color="blue" />
        <MetricCard title="用户总数" value="--" icon={Users} color="emerald" />
        <MetricCard title="今日访问" value="--" icon={Eye} color="orange" />
        <MetricCard title="评论总数" value="--" icon={MessageSquare} color="pink" />
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-6">
        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-md">
          <h3 className="text-sm font-semibold text-white mb-6 uppercase tracking-wider relative inline-block">
            访问量趋势
            <span className="absolute -bottom-2 left-0 w-1/2 h-0.5 bg-blue-500"></span>
          </h3>
          <div className="h-72 w-full relative">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={emptyData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorVisits" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" vertical={false} />
                <XAxis dataKey="name" stroke="rgba(255,255,255,0.2)" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis stroke="rgba(255,255,255,0.2)" fontSize={12} tickLine={false} axisLine={false} />
                <Tooltip 
                  contentStyle={{ backgroundColor: 'rgba(15, 23, 42, 0.9)', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px' }}
                  itemStyle={{ color: '#fff' }}
                />
                <Area type="monotone" dataKey="value" stroke="#3b82f6" strokeWidth={3} fillOpacity={1} fill="url(#colorVisits)" />
              </AreaChart>
            </ResponsiveContainer>
            {/* Empty State Overlay */}
            <div className="absolute inset-0 flex items-center justify-center">
              <span className="bg-black/40 backdrop-blur-sm px-4 py-2 rounded-lg border border-white/5 text-sm text-slate-400">暂无数据</span>
            </div>
          </div>
        </div>

        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-md">
          <h3 className="text-sm font-semibold text-white mb-6 uppercase tracking-wider relative inline-block">
            文章发布情况
            <span className="absolute -bottom-2 left-0 w-1/2 h-0.5 bg-purple-500"></span>
          </h3>
          <div className="h-72 w-full relative">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={emptyData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" vertical={false} />
                <XAxis dataKey="name" stroke="rgba(255,255,255,0.2)" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis stroke="rgba(255,255,255,0.2)" fontSize={12} tickLine={false} axisLine={false} />
                <Tooltip 
                  contentStyle={{ backgroundColor: 'rgba(15, 23, 42, 0.9)', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px' }}
                  cursor={{fill: 'rgba(255,255,255,0.05)'}}
                />
                <Bar dataKey="value" fill="#a855f7" radius={[4, 4, 0, 0]} maxBarSize={40} />
              </BarChart>
            </ResponsiveContainer>
             {/* Empty State Overlay */}
             <div className="absolute inset-0 flex items-center justify-center">
              <span className="bg-black/40 backdrop-blur-sm px-4 py-2 rounded-lg border border-white/5 text-sm text-slate-400">暂无数据</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function MetricCard({ title, value, icon: Icon, color }: { title: string, value: string, icon: any, color: 'blue' | 'emerald' | 'orange' | 'pink' }) {
  const colorStyles = {
    blue: 'from-blue-500/20 text-blue-400 border-blue-500/30',
    emerald: 'from-emerald-500/20 text-emerald-400 border-emerald-500/30',
    orange: 'from-orange-500/20 text-orange-400 border-orange-500/30',
    pink: 'from-pink-500/20 text-pink-400 border-pink-500/30',
  };

  return (
    <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-md hover:bg-white/10 transition-colors group">
      <div className="flex items-center justify-between mb-4">
        <div className={`w-10 h-10 rounded-xl bg-gradient-to-br ${colorStyles[color]} border flex items-center justify-center shadow-lg`}>
          <Icon className="w-5 h-5" />
        </div>
      </div>
      <div>
        <p className="text-3xl font-bold text-white mb-1 group-hover:scale-105 origin-left transition-transform inline-block">{value}</p>
        <p className="text-sm text-slate-500 uppercase tracking-wider">{title}</p>
      </div>
    </div>
  );
}
