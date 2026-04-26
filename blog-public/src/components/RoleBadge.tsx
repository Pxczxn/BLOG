import { ShieldCheck } from 'lucide-react';

const ROLE_META: Record<string, { label: string; className: string }> = {
  MODERATOR: {
    label: '管理员',
    className: 'border-emerald-400/30 bg-emerald-400/10 text-emerald-200',
  },
};

export default function RoleBadge({ role, compact = false }: { role?: string; compact?: boolean }) {
  if (!role || !ROLE_META[role]) {
    return null;
  }

  const meta = ROLE_META[role];

  return (
    <span
      className={`inline-flex items-center gap-1 rounded-full border px-2 py-0.5 text-[11px] font-medium ${meta.className}`}
    >
      <ShieldCheck className={compact ? 'h-3 w-3' : 'h-3.5 w-3.5'} />
      {meta.label}
    </span>
  );
}
