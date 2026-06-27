import { useEffect, useMemo, useRef, useState } from 'react';
import { format } from 'date-fns';
import toast from 'react-hot-toast';
import { Ban, Check, CheckCircle2, ChevronDown, Filter, RefreshCcw, Search, Shield, UserCog, Users } from 'lucide-react';
import request, { getStaticUrl } from '../../lib/request';
import AdminPageHeader from '../../components/admin/AdminPageHeader';

type UserRole = 'USER' | 'MODERATOR';
type UserStatus = 'ACTIVE' | 'PENDING' | 'BANNED';

type ManagedUser = {
  id: number;
  username: string;
  email: string;
  displayName: string;
  avatar?: string;
  bio?: string;
  website?: string;
  role: UserRole;
  status: UserStatus;
  lastLoginAt?: string;
  createdAt?: string;
  updatedAt?: string;
};

const roleMeta: Record<UserRole | 'ALL', { label: string; className: string }> = {
  ALL: { label: '全部等级', className: 'border-white/10 bg-white/5 text-slate-300' },
  USER: { label: '普通用户', className: 'border-blue-500/30 bg-blue-500/10 text-blue-300' },
  MODERATOR: { label: '版主', className: 'border-purple-500/30 bg-purple-500/10 text-purple-300' },
};

const statusMeta: Record<UserStatus | 'ALL', { label: string; className: string; icon?: typeof CheckCircle2 }> = {
  ALL: { label: '全部状态', className: 'border-white/10 bg-white/5 text-slate-300' },
  ACTIVE: { label: '正常', className: 'border-emerald-500/30 bg-emerald-500/10 text-emerald-300', icon: CheckCircle2 },
  PENDING: { label: '待激活', className: 'border-amber-500/30 bg-amber-500/10 text-amber-300', icon: Shield },
  BANNED: { label: '已封禁', className: 'border-red-500/30 bg-red-500/10 text-red-300', icon: Ban },
};

export default function UserManage() {
  const [users, setUsers] = useState<ManagedUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState<number | null>(null);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [keywordInput, setKeywordInput] = useState('');
  const [keyword, setKeyword] = useState('');
  const [roleInput, setRoleInput] = useState<UserRole | 'ALL'>('ALL');
  const [role, setRole] = useState<UserRole | 'ALL'>('ALL');
  const [statusInput, setStatusInput] = useState<UserStatus | 'ALL'>('ALL');
  const [status, setStatus] = useState<UserStatus | 'ALL'>('ALL');

  const totalPages = Math.max(1, Math.ceil(total / pageSize));

  const stats = useMemo(
    () => ({
      active: users.filter((user) => user.status === 'ACTIVE').length,
      banned: users.filter((user) => user.status === 'BANNED').length,
      moderators: users.filter((user) => user.role === 'MODERATOR').length,
    }),
    [users],
  );

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res: any = await request.get('/api/admin/users', {
        params: {
          page,
          size: pageSize,
          keyword: keyword || undefined,
          role: role === 'ALL' ? undefined : role,
          status: status === 'ALL' ? undefined : status,
        },
      });
      const data = res?.data ?? res;
      setUsers(data.items || []);
      setTotal(data.total || 0);
    } catch {
      setUsers([]);
      toast.error('用户列表加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [page, pageSize, keyword, role, status]);

  const applyFilters = () => {
    setPage(1);
    setKeyword(keywordInput.trim());
    setRole(roleInput);
    setStatus(statusInput);
  };

  const clearFilters = () => {
    setKeywordInput('');
    setKeyword('');
    setRoleInput('ALL');
    setRole('ALL');
    setStatusInput('ALL');
    setStatus('ALL');
    setPage(1);
  };

  const updateUser = async (user: ManagedUser, patch: Partial<Pick<ManagedUser, 'role' | 'status'>>) => {
    try {
      setBusyId(user.id);
      const res: any = await request.put(`/api/admin/users/${user.id}`, {
        role: patch.role || user.role,
        status: patch.status || user.status,
      });
      const updated = res?.data ?? res;
      setUsers((current) => current.map((item) => (item.id === user.id ? updated : item)));
      toast.success('用户权限已更新');
    } catch {
      toast.error('用户更新失败');
      fetchUsers();
    } finally {
      setBusyId(null);
    }
  };

  const quickBan = (user: ManagedUser) => {
    updateUser(user, { status: user.status === 'BANNED' ? 'ACTIVE' : 'BANNED' });
  };

  return (
    <div className="animate-in fade-in space-y-6 duration-500">
      <AdminPageHeader title="用户管理" />

      <section className="grid gap-4 md:grid-cols-4">
        <MetricCard label="当前页用户" value={users.length} />
        <MetricCard label="正常账号" value={stats.active} />
        <MetricCard label="版主" value={stats.moderators} />
        <MetricCard label="封禁账号" value={stats.banned} />
      </section>

      <div className="grid gap-6 xl:grid-cols-[320px_minmax(0,1fr)]">
        <aside className="h-fit space-y-4 xl:sticky xl:top-6">
          <section className="rounded-2xl border border-white/5 bg-slate-900/40 p-5 shadow-2xl backdrop-blur-xl">
            <div className="mb-5 flex items-center justify-between">
              <div className="flex items-center gap-2 text-white">
                <Filter className="h-4 w-4 text-purple-400" />
                <h2 className="font-bold">筛选用户</h2>
              </div>
              <button type="button" onClick={clearFilters} className="flex items-center gap-1 text-xs text-slate-500 hover:text-white">
                <RefreshCcw className="h-3 w-3" />
                重置
              </button>
            </div>

            <div className="space-y-5">
              <div>
                <label className="mb-2 block text-xs font-semibold text-slate-500">关键词</label>
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
                  <input
                    value={keywordInput}
                    onChange={(event) => setKeywordInput(event.target.value)}
                    onKeyDown={(event) => {
                      if (event.key === 'Enter') applyFilters();
                    }}
                    placeholder="用户名、昵称或邮箱"
                    className="w-full rounded-xl border border-white/10 bg-black/20 py-2.5 pl-10 pr-4 text-sm text-white placeholder-slate-600 focus:border-purple-500/50 focus:outline-none"
                  />
                </div>
              </div>

              <FilterButtons
                label="用户等级"
                options={['ALL', 'USER', 'MODERATOR']}
                value={roleInput}
                meta={roleMeta}
                onChange={(value) => setRoleInput(value as UserRole | 'ALL')}
              />

              <FilterButtons
                label="账号状态"
                options={['ALL', 'ACTIVE', 'PENDING', 'BANNED']}
                value={statusInput}
                meta={statusMeta}
                onChange={(value) => setStatusInput(value as UserStatus | 'ALL')}
              />

              <label className="block text-xs font-semibold text-slate-500">
                每页数量
                <select
                  value={pageSize}
                  onChange={(event) => {
                    setPage(1);
                    setPageSize(Number(event.target.value));
                  }}
                  className="mt-2 w-full rounded-xl border border-white/10 bg-black/20 px-3 py-2 text-sm text-slate-200 focus:border-purple-500/50 focus:outline-none"
                >
                  <option value={10}>10 人</option>
                  <option value={20}>20 人</option>
                  <option value={50}>50 人</option>
                </select>
              </label>

              <button
                type="button"
                onClick={applyFilters}
                className="w-full rounded-xl border border-white/10 bg-white/5 py-2.5 text-sm font-bold text-white transition hover:bg-white/10"
              >
                应用筛选
              </button>
            </div>
          </section>
        </aside>

        <section className="overflow-hidden rounded-2xl border border-white/5 bg-slate-900/40 shadow-2xl backdrop-blur-xl">
          <div className="overflow-x-auto">
            <table className="w-full min-w-[980px] border-collapse text-left">
              <thead>
                <tr className="border-b border-white/5 bg-white/[0.02] text-[10px] uppercase tracking-[0.2em] text-slate-500">
                  <th className="px-5 py-4 font-bold">用户</th>
                  <th className="px-5 py-4 font-bold">联系方式</th>
                  <th className="px-5 py-4 text-center font-bold">等级</th>
                  <th className="px-5 py-4 text-center font-bold">状态</th>
                  <th className="px-5 py-4 font-bold">登录 / 注册</th>
                  <th className="px-5 py-4 text-right font-bold">操作</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/[0.03] text-sm">
                {loading ? (
                  <tr>
                    <td colSpan={6} className="px-6 py-20 text-center">
                      <RefreshCcw className="mx-auto mb-4 h-8 w-8 animate-spin text-purple-500/30" />
                      <span className="text-xs uppercase tracking-widest text-slate-600">用户加载中</span>
                    </td>
                  </tr>
                ) : users.length > 0 ? (
                  users.map((user) => {
                    const avatar = user.avatar ? getStaticUrl(user.avatar) : '';
                    return (
                      <tr key={user.id} className="group transition hover:bg-white/[0.02]">
                        <td className="px-5 py-4">
                          <div className="flex items-center gap-3">
                            <div className="flex h-11 w-11 items-center justify-center overflow-hidden rounded-full border border-white/10 bg-purple-500/10 text-sm font-bold text-purple-200">
                              {avatar ? <img src={avatar} alt="" className="h-full w-full object-cover" /> : user.displayName?.slice(0, 1) || 'U'}
                            </div>
                            <div className="min-w-0">
                              <p className="max-w-[220px] truncate font-bold text-slate-100">{user.displayName || user.username}</p>
                              <p className="mt-1 font-mono text-[10px] uppercase tracking-tight text-slate-600">@{user.username}</p>
                            </div>
                          </div>
                        </td>
                        <td className="px-5 py-4">
                          <div className="max-w-[260px] truncate text-slate-300">{user.email}</div>
                          <div className="mt-1 max-w-[260px] truncate text-xs text-slate-600">{user.bio || '暂无简介'}</div>
                        </td>
                        <td className="px-5 py-4 text-center">
                          <ThemeSelect
                            value={user.role}
                            disabled={busyId === user.id}
                            options={[
                              { value: 'USER', label: roleMeta.USER.label },
                              { value: 'MODERATOR', label: roleMeta.MODERATOR.label },
                            ]}
                            onChange={(value) => updateUser(user, { role: value as UserRole })}
                          />
                        </td>
                        <td className="px-5 py-4 text-center">
                          <ThemeSelect
                            value={user.status}
                            disabled={busyId === user.id}
                            options={[
                              { value: 'ACTIVE', label: statusMeta.ACTIVE.label },
                              { value: 'PENDING', label: statusMeta.PENDING.label },
                              { value: 'BANNED', label: statusMeta.BANNED.label },
                            ]}
                            onChange={(value) => updateUser(user, { status: value as UserStatus })}
                          />
                        </td>
                        <td className="px-5 py-4">
                          <p className="font-mono text-xs text-slate-400">{formatDate(user.lastLoginAt) || '从未登录'}</p>
                          <p className="mt-1 font-mono text-[10px] text-slate-600">注册 {formatDate(user.createdAt) || '--'}</p>
                        </td>
                        <td className="px-5 py-4 text-right">
                          <button
                            type="button"
                            disabled={busyId === user.id}
                            onClick={() => quickBan(user)}
                            className={`inline-flex items-center gap-2 rounded-xl border px-3 py-2 text-xs transition disabled:opacity-40 ${
                              user.status === 'BANNED'
                                ? 'border-emerald-500/20 bg-emerald-500/10 text-emerald-300 hover:bg-emerald-500/20'
                                : 'border-red-500/20 bg-red-500/10 text-red-300 hover:bg-red-500/20'
                            }`}
                          >
                            {user.status === 'BANNED' ? <CheckCircle2 className="h-4 w-4" /> : <Ban className="h-4 w-4" />}
                            {user.status === 'BANNED' ? '恢复' : '封禁'}
                          </button>
                        </td>
                      </tr>
                    );
                  })
                ) : (
                  <tr>
                    <td colSpan={6} className="px-6 py-20 text-center">
                      <Users className="mx-auto mb-4 h-10 w-10 text-slate-700" />
                      <p className="text-sm font-bold uppercase tracking-widest text-slate-500">没有找到用户</p>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <div className="border-t border-white/5 bg-white/[0.01] px-5 py-4">
            <div className="flex flex-wrap items-center justify-between gap-3">
              <p className="text-[10px] font-bold uppercase tracking-widest text-slate-600">
                第 {page} / {totalPages} 页 · 共 {total} 人
              </p>
              <div className="flex items-center gap-2">
                <button
                  type="button"
                  disabled={page <= 1}
                  onClick={() => setPage((current) => Math.max(1, current - 1))}
                  className="rounded-lg border border-white/5 bg-white/5 px-3 py-1 text-xs font-bold text-slate-400 transition hover:bg-white/10 hover:text-white disabled:opacity-30"
                >
                  上一页
                </button>
                <button
                  type="button"
                  disabled={page >= totalPages}
                  onClick={() => setPage((current) => Math.min(totalPages, current + 1))}
                  className="rounded-lg border border-white/5 bg-white/5 px-3 py-1 text-xs font-bold text-slate-400 transition hover:bg-white/10 hover:text-white disabled:opacity-30"
                >
                  下一页
                </button>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}

function ThemeSelect({
  value,
  options,
  disabled,
  onChange,
}: {
  value: string;
  options: Array<{ value: string; label: string }>;
  disabled?: boolean;
  onChange: (value: string) => void;
}) {
  const [open, setOpen] = useState(false);
  const rootRef = useRef<HTMLDivElement | null>(null);
  const selected = options.find((option) => option.value === value) ?? options[0];

  useEffect(() => {
    if (!open) return;

    const handlePointerDown = (event: PointerEvent) => {
      if (!rootRef.current?.contains(event.target as Node)) {
        setOpen(false);
      }
    };
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setOpen(false);
      }
    };

    document.addEventListener('pointerdown', handlePointerDown);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('pointerdown', handlePointerDown);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [open]);

  return (
    <div ref={rootRef} className="relative inline-flex min-w-[112px] justify-center text-left">
      <button
        type="button"
        disabled={disabled}
        aria-haspopup="listbox"
        aria-expanded={open}
        onClick={() => setOpen((current) => !current)}
        className={`inline-flex h-10 w-full items-center justify-between gap-3 rounded-xl border px-4 text-sm font-medium transition ${
          open
            ? 'border-purple-400/70 bg-purple-500/10 text-white shadow-lg shadow-purple-950/30'
            : 'border-white/10 bg-black/25 text-slate-100 hover:border-purple-400/40 hover:bg-white/[0.04]'
        } disabled:cursor-not-allowed disabled:opacity-50`}
      >
        <span className="truncate">{selected?.label}</span>
        <ChevronDown className={`h-4 w-4 shrink-0 text-slate-400 transition ${open ? 'rotate-180 text-purple-300' : ''}`} />
      </button>

      {open ? (
        <div
          role="listbox"
          className="absolute left-0 top-full z-30 mt-2 w-full overflow-hidden rounded-xl border border-purple-400/30 bg-[#080616]/95 p-1 shadow-2xl shadow-black/50 backdrop-blur-xl"
        >
          {options.map((option) => {
            const active = option.value === value;
            return (
              <button
                key={option.value}
                type="button"
                role="option"
                aria-selected={active}
                onClick={() => {
                  onChange(option.value);
                  setOpen(false);
                }}
                className={`flex w-full items-center justify-between gap-3 rounded-lg px-3 py-2 text-left text-sm transition ${
                  active ? 'bg-purple-500/25 text-white' : 'text-slate-300 hover:bg-white/10 hover:text-white'
                }`}
              >
                <span>{option.label}</span>
                {active ? <Check className="h-4 w-4 text-purple-300" /> : null}
              </button>
            );
          })}
        </div>
      ) : null}
    </div>
  );
}

function FilterButtons({
  label,
  options,
  value,
  meta,
  onChange,
}: {
  label: string;
  options: string[];
  value: string;
  meta: Record<string, { label: string; className: string }>;
  onChange: (value: string) => void;
}) {
  return (
    <div>
      <label className="mb-2 block text-xs font-semibold text-slate-500">{label}</label>
      <div className="grid gap-2">
        {options.map((option) => {
          const active = value === option;
          return (
            <button
              key={option}
              type="button"
              onClick={() => onChange(option)}
              className={`flex items-center justify-between rounded-xl border px-4 py-2.5 text-sm transition ${
                active ? 'border-purple-500/50 bg-purple-500/10 text-white' : 'border-white/5 bg-white/[0.02] text-slate-400 hover:bg-white/5'
              }`}
            >
              {meta[option].label}
              {active ? <span className="h-1.5 w-1.5 rounded-full bg-purple-400" /> : null}
            </button>
          );
        })}
      </div>
    </div>
  );
}

function MetricCard({ label, value }: { label: string; value: number }) {
  return (
    <div className="rounded-2xl border border-purple-500/20 bg-purple-500/10 p-4 backdrop-blur-md">
      <div className="flex items-center justify-between">
        <p className="text-xs text-slate-400">{label}</p>
        <UserCog className="h-4 w-4 text-purple-300" />
      </div>
      <p className="mt-2 text-2xl font-black text-white">{value.toLocaleString()}</p>
    </div>
  );
}

function formatDate(value?: string) {
  if (!value) return '';
  return format(new Date(value), 'yyyy/MM/dd HH:mm');
}
