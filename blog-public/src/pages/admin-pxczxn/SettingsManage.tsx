import { useEffect, useMemo, useState, type ReactNode } from 'react';
import toast from 'react-hot-toast';
import { CheckCircle, Code, Globe, Home, Info, Save, Search, User, X } from 'lucide-react';
import {
  DEFAULT_SITE_SETTINGS,
  type SiteSettings,
  fetchAdminSiteSettings,
  updateAdminSiteSettings,
} from '../../lib/siteSettings';

type SectionId = 'basic' | 'home' | 'seo' | 'about' | 'tech';

const sections = [
  { id: 'basic' as const, title: '基础信息', desc: '站点名称、签名和管理员昵称', icon: Info, color: 'bg-purple-500' },
  { id: 'home' as const, title: '首页主张', desc: '第一屏标题和介绍文案', icon: Home, color: 'bg-sky-500' },
  { id: 'seo' as const, title: 'SEO 与开关', desc: '关键词、描述和游客评论权限', icon: Search, color: 'bg-blue-500' },
  { id: 'about' as const, title: '关于我', desc: '个人简介与博客定位', icon: User, color: 'bg-pink-500' },
  { id: 'tech' as const, title: '技术栈', desc: '前端、后端和工程化能力', icon: Code, color: 'bg-emerald-500' },
];

export default function SettingsManage() {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [activeModal, setActiveModal] = useState<SectionId | null>(null);
  const [form, setForm] = useState<SiteSettings>(DEFAULT_SITE_SETTINGS);

  useEffect(() => {
    fetchAdminSiteSettings()
      .then(setForm)
      .catch(() => toast.error('站点设置加载失败，已使用默认值兜底'))
      .finally(() => setLoading(false));
  }, []);

  const activeSection = useMemo(
    () => sections.find((section) => section.id === activeModal),
    [activeModal],
  );

  const updateField = <K extends keyof SiteSettings>(key: K, value: SiteSettings[K]) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      const savedSettings = await updateAdminSiteSettings(form);
      setForm(savedSettings);
      setSaved(true);
      setActiveModal(null);
      toast.success('站点设置已保存');
      window.setTimeout(() => setSaved(false), 3000);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : '保存失败');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">系统设置</h1>
          <p className="mt-1 text-sm text-slate-400">统一管理站点基础信息、SEO、首页文案和关于页内容。</p>
        </div>
        {saved ? (
          <span className="flex items-center gap-1 text-sm text-emerald-400">
            <CheckCircle className="h-4 w-4" />
            配置已更新
          </span>
        ) : null}
      </div>

      <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-5 text-sm text-slate-300">
        <div className="mb-2 flex items-center gap-2 font-semibold text-white">
          <Globe className="h-4 w-4 text-blue-300" />
          当前站点预览
        </div>
        <div className="grid gap-3 md:grid-cols-3">
          <Preview label="站点" value={form.siteName} loading={loading} />
          <Preview label="首页标题" value={form.homeTitle} loading={loading} />
          <Preview label="管理员" value={form.adminNick} loading={loading} />
        </div>
      </div>

      <div className="relative z-10 grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-5">
        {sections.map((section) => (
          <button
            key={section.id}
            type="button"
            onClick={() => setActiveModal(section.id)}
            className="group flex min-h-44 flex-col items-start rounded-2xl border border-white/5 bg-white/[0.02] p-5 text-left backdrop-blur-md transition-all hover:-translate-y-0.5 hover:border-white/10 hover:bg-white/[0.05]"
          >
            <div className={`mb-4 rounded-xl ${section.color} bg-opacity-20 p-3 transition-transform group-hover:scale-105`}>
              <section.icon className="h-5 w-5 text-white" />
            </div>
            <h3 className="mb-2 text-base font-bold text-white">{section.title}</h3>
            <p className="text-xs leading-relaxed text-slate-400">{section.desc}</p>
          </button>
        ))}
      </div>

      {activeModal && activeSection ? (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm">
          <div className="w-full max-w-2xl overflow-hidden rounded-2xl border border-white/10 bg-slate-950 shadow-2xl">
            <div className="flex items-center justify-between border-b border-white/5 bg-white/[0.03] px-6 py-4">
              <div>
                <h3 className="text-xl font-bold text-white">{activeSection.title}</h3>
                <p className="mt-1 text-xs text-slate-500">{activeSection.desc}</p>
              </div>
              <button
                type="button"
                onClick={() => setActiveModal(null)}
                className="rounded-full p-2 text-slate-400 transition-colors hover:bg-white/5 hover:text-white"
                aria-label="关闭"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            <div className="max-h-[70vh] space-y-4 overflow-y-auto p-6">
              {activeModal === 'basic' ? (
                <>
                  <Field label="站点名称">
                    <TextInput value={form.siteName} onChange={(value) => updateField('siteName', value)} maxLength={80} />
                  </Field>
                  <Field label="站点副标题">
                    <TextInput value={form.siteSub} onChange={(value) => updateField('siteSub', value)} maxLength={160} />
                  </Field>
                  <Field label="管理员昵称">
                    <TextInput value={form.adminNick} onChange={(value) => updateField('adminNick', value)} maxLength={80} />
                  </Field>
                </>
              ) : null}

              {activeModal === 'home' ? (
                <>
                  <Field label="首页主标题">
                    <TextInput value={form.homeTitle} onChange={(value) => updateField('homeTitle', value)} maxLength={80} />
                  </Field>
                  <Field label="首页介绍文案">
                    <TextArea value={form.homeIntro} onChange={(value) => updateField('homeIntro', value)} rows={4} maxLength={300} />
                  </Field>
                </>
              ) : null}

              {activeModal === 'seo' ? (
                <>
                  <Field label="SEO 关键词">
                    <TextInput value={form.keywords} onChange={(value) => updateField('keywords', value)} maxLength={300} />
                  </Field>
                  <Field label="站点描述">
                    <TextArea value={form.description} onChange={(value) => updateField('description', value)} rows={4} maxLength={500} />
                  </Field>
                  <label className="flex cursor-pointer items-center gap-3 rounded-xl border border-white/10 bg-black/30 px-4 py-3">
                    <input
                      type="checkbox"
                      checked={form.allowGuest}
                      onChange={(event) => updateField('allowGuest', event.target.checked)}
                      className="h-4 w-4 rounded border-white/10 bg-black/40 text-blue-500 focus:ring-blue-500"
                    />
                    <span className="text-sm font-medium text-slate-300">允许游客评论</span>
                  </label>
                </>
              ) : null}

              {activeModal === 'about' ? (
                <>
                  <Field label="个人简介">
                    <TextArea value={form.aboutBio} onChange={(value) => updateField('aboutBio', value)} rows={5} maxLength={800} />
                  </Field>
                  <Field label="补充说明">
                    <TextArea value={form.aboutBioSub} onChange={(value) => updateField('aboutBioSub', value)} rows={4} maxLength={800} />
                  </Field>
                </>
              ) : null}

              {activeModal === 'tech' ? (
                <>
                  <Field label="前端技术">
                    <TextInput value={form.frontend} onChange={(value) => updateField('frontend', value)} maxLength={300} />
                  </Field>
                  <Field label="后端技术">
                    <TextInput value={form.backend} onChange={(value) => updateField('backend', value)} maxLength={300} />
                  </Field>
                  <Field label="工程化">
                    <TextInput value={form.engineering} onChange={(value) => updateField('engineering', value)} maxLength={300} />
                  </Field>
                  <Field label="其他工具">
                    <TextInput value={form.other} onChange={(value) => updateField('other', value)} maxLength={300} />
                  </Field>
                </>
              ) : null}
            </div>

            <div className="flex items-center justify-end gap-3 border-t border-white/5 bg-white/[0.02] px-6 py-4">
              <button
                type="button"
                onClick={() => setActiveModal(null)}
                className="px-4 py-2 text-sm font-medium text-slate-400 transition-colors hover:text-white"
              >
                取消
              </button>
              <button
                type="button"
                onClick={handleSave}
                disabled={saving}
                className="flex items-center gap-2 rounded-xl bg-gradient-to-r from-purple-600 to-blue-600 px-6 py-2 text-sm font-medium text-white shadow-[0_0_15px_rgba(168,85,247,0.3)] transition-all disabled:opacity-50"
              >
                <Save className="h-4 w-4" />
                {saving ? '保存中...' : '保存'}
              </button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}

function Preview({ label, value, loading }: { label: string; value: string; loading: boolean }) {
  return (
    <div className="rounded-xl border border-white/5 bg-black/20 px-4 py-3">
      <div className="text-xs text-slate-500">{label}</div>
      <div className="mt-1 truncate text-sm font-medium text-white">{loading ? '加载中...' : value}</div>
    </div>
  );
}

function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div>
      <label className="mb-1 block text-sm font-medium text-slate-400">{label}</label>
      {children}
    </div>
  );
}

function TextInput({ value, onChange, maxLength }: { value: string; onChange: (value: string) => void; maxLength: number }) {
  return (
    <input
      type="text"
      value={value}
      maxLength={maxLength}
      onChange={(event) => onChange(event.target.value)}
      className="w-full rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white outline-none transition-colors focus:border-purple-500"
    />
  );
}

function TextArea({
  value,
  onChange,
  rows,
  maxLength,
}: {
  value: string;
  onChange: (value: string) => void;
  rows: number;
  maxLength: number;
}) {
  return (
    <textarea
      value={value}
      rows={rows}
      maxLength={maxLength}
      onChange={(event) => onChange(event.target.value)}
      className="w-full resize-none rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white outline-none transition-colors focus:border-purple-500"
    />
  );
}
