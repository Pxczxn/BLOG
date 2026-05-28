import { useEffect, useState, type ReactNode } from 'react';
import { Save, CheckCircle, Info, Globe, User, Code, X } from 'lucide-react';
import {
  DEFAULT_ABOUT_SETTINGS,
  DEFAULT_SITE_SETTINGS,
  type AboutSettings,
  type SiteSettings,
  readAboutSettings,
  readSiteSettings,
  saveAboutSettings,
  saveSiteSettings,
} from '../../lib/siteSettings';

type SectionId = 'basic' | 'seo' | 'about' | 'tech';

const sections = [
  { id: 'basic' as const, title: '基础信息配置', desc: '配置站点名称、副标题及管理员昵称', icon: Info, color: 'bg-purple-500' },
  { id: 'seo' as const, title: 'SEO 与功能开关', desc: '关键词、描述及游客评论权限', icon: Globe, color: 'bg-blue-500' },
  { id: 'about' as const, title: '关于我 (About)', desc: '编辑个人简介与主页 Bio 文案', icon: User, color: 'bg-pink-500' },
  { id: 'tech' as const, title: '技术栈 (Tech Stack)', desc: '配置前端、后端及工程化技术清单', icon: Code, color: 'bg-emerald-500' },
];

export default function SettingsManage() {
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [activeModal, setActiveModal] = useState<SectionId | null>(null);
  const [form, setForm] = useState<SiteSettings>(DEFAULT_SITE_SETTINGS);
  const [aboutForm, setAboutForm] = useState<AboutSettings>(DEFAULT_ABOUT_SETTINGS);

  useEffect(() => {
    setForm(readSiteSettings());
    setAboutForm(readAboutSettings());
  }, []);

  const handleSave = () => {
    setSaving(true);
    window.setTimeout(() => {
      saveSiteSettings(form);
      saveAboutSettings(aboutForm);
      setSaving(false);
      setSaved(true);
      setActiveModal(null);
      window.setTimeout(() => setSaved(false), 3000);
    }, 400);
  };

  return (
    <div className="space-y-6">
      <div className="mb-4 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">系统设置</h1>
          <p className="mt-1 text-sm text-slate-400">管理站点基础信息、SEO 默认值和关于页展示内容</p>
        </div>
        {saved ? (
          <span className="flex items-center gap-1 text-sm text-emerald-400">
            <CheckCircle className="h-4 w-4" />
            配置已更新
          </span>
        ) : null}
      </div>

      <div className="relative z-10 grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
        {sections.map((section) => (
          <button
            key={section.id}
            onClick={() => setActiveModal(section.id)}
            className="group flex flex-col items-start rounded-3xl border border-white/5 bg-white/[0.02] p-6 text-left backdrop-blur-md transition-all hover:border-white/10 hover:bg-white/[0.05]"
          >
            <div className={`mb-4 rounded-2xl ${section.color} bg-opacity-15 p-3 transition-transform group-hover:scale-110`}>
              <section.icon className="h-6 w-6 text-white" />
            </div>
            <h3 className="mb-2 text-lg font-bold text-white">{section.title}</h3>
            <p className="text-xs leading-relaxed text-slate-400">{section.desc}</p>
          </button>
        ))}
      </div>

      {activeModal ? (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm">
          <div className="w-full max-w-xl overflow-hidden rounded-3xl border border-white/10 bg-slate-900 shadow-2xl">
            <div className="flex items-center justify-between border-b border-white/5 bg-white/[0.02] px-6 py-4">
              <h3 className="text-xl font-bold text-white">
                {sections.find((section) => section.id === activeModal)?.title}
              </h3>
              <button
                onClick={() => setActiveModal(null)}
                className="rounded-full p-2 text-slate-400 transition-colors hover:bg-white/5 hover:text-white"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            <div className="max-h-[70vh] space-y-4 overflow-y-auto p-6">
              {activeModal === 'basic' ? (
                <>
                  <Field label="站点名称">
                    <input
                      type="text"
                      value={form.siteName}
                      onChange={(e) => setForm({ ...form, siteName: e.target.value })}
                      className="w-full rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white focus:border-purple-500 focus:outline-none"
                    />
                  </Field>
                  <Field label="站点副标题">
                    <input
                      type="text"
                      value={form.siteSub}
                      onChange={(e) => setForm({ ...form, siteSub: e.target.value })}
                      className="w-full rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white focus:border-purple-500 focus:outline-none"
                    />
                  </Field>
                  <Field label="管理员昵称">
                    <input
                      type="text"
                      value={form.adminNick}
                      onChange={(e) => setForm({ ...form, adminNick: e.target.value })}
                      className="w-full rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white focus:border-purple-500 focus:outline-none"
                    />
                  </Field>
                </>
              ) : null}

              {activeModal === 'seo' ? (
                <>
                  <Field label="SEO 关键词 (Keywords)">
                    <input
                      type="text"
                      value={form.keywords}
                      onChange={(e) => setForm({ ...form, keywords: e.target.value })}
                      className="w-full rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white focus:border-blue-500 focus:outline-none"
                    />
                  </Field>
                  <Field label="站点描述 (Description)">
                    <textarea
                      rows={4}
                      value={form.description}
                      onChange={(e) => setForm({ ...form, description: e.target.value })}
                      className="w-full resize-none rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white focus:border-blue-500 focus:outline-none"
                    />
                  </Field>
                  <label className="flex cursor-pointer items-center gap-3 pt-2">
                    <span className="text-sm font-medium text-slate-300">允许游客发布评论</span>
                    <input
                      type="checkbox"
                      checked={form.allowGuest}
                      onChange={(e) => setForm({ ...form, allowGuest: e.target.checked })}
                      className="h-4 w-4 rounded border-white/10 bg-black/40 text-blue-500 focus:ring-blue-500"
                    />
                  </label>
                </>
              ) : null}

              {activeModal === 'about' ? (
                <>
                  <Field label="主简介 (Bio)">
                    <textarea
                      rows={4}
                      value={aboutForm.bio}
                      onChange={(e) => setAboutForm({ ...aboutForm, bio: e.target.value })}
                      className="w-full resize-none rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white focus:border-pink-500 focus:outline-none"
                    />
                  </Field>
                  <Field label="补充说明">
                    <textarea
                      rows={3}
                      value={aboutForm.bioSub}
                      onChange={(e) => setAboutForm({ ...aboutForm, bioSub: e.target.value })}
                      className="w-full resize-none rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white focus:border-pink-500 focus:outline-none"
                    />
                  </Field>
                </>
              ) : null}

              {activeModal === 'tech' ? (
                <>
                  <Field label="前端技术">
                    <input
                      type="text"
                      value={aboutForm.frontend}
                      onChange={(e) => setAboutForm({ ...aboutForm, frontend: e.target.value })}
                      className="w-full rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white focus:border-emerald-500 focus:outline-none"
                    />
                  </Field>
                  <Field label="后端技术">
                    <input
                      type="text"
                      value={aboutForm.backend}
                      onChange={(e) => setAboutForm({ ...aboutForm, backend: e.target.value })}
                      className="w-full rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white focus:border-emerald-500 focus:outline-none"
                    />
                  </Field>
                  <Field label="工程化">
                    <input
                      type="text"
                      value={aboutForm.engineering}
                      onChange={(e) => setAboutForm({ ...aboutForm, engineering: e.target.value })}
                      className="w-full rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white focus:border-emerald-500 focus:outline-none"
                    />
                  </Field>
                  <Field label="其他工具">
                    <input
                      type="text"
                      value={aboutForm.other}
                      onChange={(e) => setAboutForm({ ...aboutForm, other: e.target.value })}
                      className="w-full rounded-xl border border-white/10 bg-black/40 px-4 py-2.5 text-sm text-white focus:border-emerald-500 focus:outline-none"
                    />
                  </Field>
                </>
              ) : null}
            </div>

            <div className="flex items-center justify-end gap-3 border-t border-white/5 bg-white/[0.01] px-6 py-4">
              <button
                onClick={() => setActiveModal(null)}
                className="px-4 py-2 text-sm font-medium text-slate-400 transition-colors hover:text-white"
              >
                取消
              </button>
              <button
                onClick={handleSave}
                disabled={saving}
                className="flex items-center gap-2 rounded-xl bg-gradient-to-r from-purple-600 to-blue-600 px-6 py-2 text-sm font-medium text-white shadow-[0_0_15px_rgba(168,85,247,0.3)] transition-all disabled:opacity-50"
              >
                <Save className="h-4 w-4" />
                {saving ? '保存中...' : '确认保存'}
              </button>
            </div>
          </div>
        </div>
      ) : null}
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
