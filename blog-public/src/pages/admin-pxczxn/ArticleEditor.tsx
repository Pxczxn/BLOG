


import { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { ArrowLeft, Save, Send, Image as ImageIcon } from 'lucide-react';
import request from '../../lib/request';
import AdminPageHeader from '../../components/admin/AdminPageHeader';

export default function ArticleEditor() {
  const { id } = useParams();
  const isEdit = !!id;
  const navigate = useNavigate();

  const [form, setForm] = useState({
    title: '',
    summary: '',
    content: '',
    coverImage: '',
    categoryId: '',
    status: 'DRAFT',
  });
  
  const [categories, setCategories] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  
  const [tagInput, setTagInput] = useState('');

  useEffect(() => {
    const fetchDeps = async () => {
      try {
        const catRes: any = await request.get('/api/public/categories');
        setCategories(catRes?.data ?? catRes ?? []);
      } catch (e) {
         setCategories([]);
      }
    };
    fetchDeps();

    if (isEdit) {
      const fetchArticle = async () => {
        try {
          setLoading(true);
          const res: any = await request.get(`/api/admin/articles/${id}`);
          const data = res?.data ?? res;
          setForm({
            title: data.title || '',
            summary: data.summary || '',
            content: data.content || '',
            coverImage: data.coverImage || data.cover || '',
            categoryId: data.category?.id || data.categoryId || '',
            status: data.status || 'DRAFT',
          });
          
          
          if (data.tags) {
             setTagInput(data.tags.map((t: any) => t.name || t).join(', '));
          }
        } catch (e) {
          setError('获取文章详情失败');
        } finally {
          setLoading(false);
        }
      };
      fetchArticle();
    }
  }, [id, isEdit]);

  const handleSubmit = async (e: React.FormEvent, forceStatus?: string) => {
    e.preventDefault();
    if (!form.title.trim() || !form.content.trim()) {
      setError('标题和内容不能为空');
      return;
    }
    
    setError('');
    setSaving(true);
    
    const payload = {
      ...form,
      categoryId: form.categoryId ? Number(form.categoryId) : null,
      status: forceStatus || form.status
    };
    
    try {
      let savedArticleId = id;
      if (isEdit) {
        await request.put(`/api/admin/articles/${id}`, payload);
      } else {
        const res: any = await request.post('/api/admin/articles', payload);
        const data = res?.data ?? res;
        savedArticleId = data.id;
      }
      
      
      
      
      
      
      navigate('/admin-pxczxn/articles');
    } catch (err: any) {
      setError(err.message || '保存失败');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="text-white">加载中...</div>;

  return (
    <div className="space-y-6 max-w-5xl mx-auto">
      <AdminPageHeader
        className="mb-6"
        title={isEdit ? '编辑文章' : '新增文章'}
        leading={
          <Link to="/admin-pxczxn/articles" className="flex h-8 w-8 items-center justify-center rounded-full border border-white/10 bg-white/5 text-slate-400 transition-colors hover:bg-white/10 hover:text-white">
            <ArrowLeft className="h-4 w-4" />
          </Link>
        }
        actions={
          <div className="flex items-center gap-3">
            <button 
              onClick={e => handleSubmit(e, 'DRAFT')}
              disabled={saving}
              className="flex items-center gap-2 rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-white transition-all hover:bg-white/10 disabled:opacity-50"
            >
              <Save className="h-4 w-4" /> 存为草稿
            </button>
            <button 
              onClick={e => handleSubmit(e, 'PUBLISHED')}
              disabled={saving}
              className="flex items-center gap-2 rounded-xl border-none bg-gradient-to-r from-purple-600 to-blue-600 px-4 py-2 text-sm font-medium text-white shadow-[0_0_15px_rgba(168,85,247,0.4)] transition-all hover:from-purple-500 hover:to-blue-500 disabled:opacity-50"
            >
              <Send className="h-4 w-4" /> 发布文章
            </button>
          </div>
        }
      />

      {error && (
        <div className="p-4 bg-red-500/10 border border-red-500/30 rounded-xl text-red-400 text-sm">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
           <div className="bg-white/[0.02] border border-white/5 rounded-3xl backdrop-blur-sm p-6 backdrop-blur-md">
             <input 
               type="text" 
               value={form.title}
               onChange={e => setForm({...form, title: e.target.value})}
               placeholder="输入文章标题..."
               className="w-full bg-transparent text-2xl font-bold text-white placeholder-slate-500 focus:outline-none mb-6"
             />
             
             <textarea 
               value={form.content}
               onChange={e => setForm({...form, content: e.target.value})}
               placeholder="输入文章正文 (Markdown)..."
               className="w-full h-[500px] bg-black/20 border border-white/10 rounded-xl p-4 text-sm text-slate-300 placeholder-slate-600 focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 resize-y font-mono leading-relaxed"
             ></textarea>
           </div>
        </div>

        <div className="space-y-6">
          <div className="bg-white/[0.02] border border-white/5 rounded-3xl backdrop-blur-sm p-6 backdrop-blur-md">
            <h3 className="text-sm font-bold text-white mb-4 uppercase tracking-wider">发布设置</h3>
            
            <div className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">分类目录</label>
                <select 
                  value={form.categoryId}
                  onChange={e => setForm({...form, categoryId: e.target.value})}
                  className="w-full bg-black/20 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 appearance-none"
                >
                  <option value="">选择分类...</option>
                  {categories.map(c => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">文章摘要</label>
                <textarea 
                  value={form.summary}
                  onChange={e => setForm({...form, summary: e.target.value})}
                  rows={3}
                  className="w-full bg-black/20 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 resize-none"
                  placeholder="留空则自动提取正文前100字..."
                ></textarea>
              </div>
              
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5">封面大图 (URL)</label>
                <div className="relative">
                  <ImageIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                  <input 
                    type="text" 
                    value={form.coverImage}
                    onChange={e => setForm({...form, coverImage: e.target.value})}
                    placeholder="https://..."
                    className="w-full bg-black/20 border border-white/10 rounded-lg pl-9 pr-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50"
                  />
                </div>
                {form.coverImage && (
                   <div className="mt-2 w-full h-24 rounded-lg overflow-hidden border border-white/10 relative">
                     <img src={form.coverImage} alt="封面预览" className="w-full h-full object-cover" />
                   </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
