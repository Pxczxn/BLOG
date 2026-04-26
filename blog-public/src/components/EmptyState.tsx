import { Link } from 'react-router-dom';
import type { LucideIcon } from 'lucide-react';

type EmptyStateAction = {
  label: string;
  to: string;
  variant?: 'primary' | 'ghost';
};

type EmptyStateProps = {
  icon: LucideIcon;
  title: string;
  description: string;
  actions?: EmptyStateAction[];
};

export default function EmptyState({ icon: Icon, title, description, actions = [] }: EmptyStateProps) {
  return (
    <div className="flex min-h-[220px] flex-col items-center justify-center rounded-2xl border border-white/5 bg-white/5 px-6 py-12 text-center backdrop-blur-sm">
      <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-2xl border border-purple-400/20 bg-purple-500/10 text-purple-300">
        <Icon className="h-6 w-6" />
      </div>
      <h3 className="text-lg font-semibold text-white">{title}</h3>
      <p className="mt-2 max-w-md text-sm leading-6 text-slate-400">{description}</p>
      {actions.length > 0 && (
        <div className="mt-5 flex flex-wrap items-center justify-center gap-3">
          {actions.map((action) => (
            <Link
              key={action.to}
              to={action.to}
              className={[
                'rounded-full border px-4 py-2 text-sm transition-colors',
                action.variant === 'ghost'
                  ? 'border-white/10 bg-white/5 text-slate-300 hover:border-white/20 hover:text-white'
                  : 'border-purple-400/40 bg-purple-500/20 text-purple-100 hover:bg-purple-500/30',
              ].join(' ')}
            >
              {action.label}
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
