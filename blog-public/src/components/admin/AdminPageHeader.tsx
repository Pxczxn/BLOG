import type { ReactNode } from 'react';

type AdminPageHeaderProps = {
  title: string;
  leading?: ReactNode;
  actions?: ReactNode;
  className?: string;
};

export default function AdminPageHeader({ title, leading, actions, className = '' }: AdminPageHeaderProps) {
  return (
    <div className={`flex min-h-[40px] items-center justify-between gap-4 mb-2 ${className}`.trim()}>
      <div className="flex min-w-0 items-center gap-4">
        {leading}
        <h1 className="text-2xl font-bold tracking-tight text-white">{title}</h1>
      </div>

      {actions ? <div className="flex shrink-0 items-center gap-3">{actions}</div> : null}
    </div>
  );
}
