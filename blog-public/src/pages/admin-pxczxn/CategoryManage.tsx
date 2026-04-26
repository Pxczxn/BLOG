/**
 * 分类管理页
 * <p>
 * 分类的增删改查
 */
import { useState, useEffect } from 'react';
import { Plus, Search, FolderTree, Trash2, Edit } from 'lucide-react';
import request from '../../lib/request';
import { format } from 'date-fns';
import AdminPageHeader from '../../components/admin/AdminPageHeader';

export default function CategoryManage() {
  const [categories, setCategories] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editId, setEditId] = useState<number | null>(null);
  const [form, setForm] = useState({ name: '', slug: '', description: '' });

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const res: any = await request.get('/api/public/categories');
      const data = res?.data ?? res;
      const list = Array.isArray(data) ? data : data.items || [];
      setCategories(list);
    } catch (error) {
      // Error handled silently
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleDelete = async (id: number) => {
    if (!window.confirm('确定要删除这个分类吗？')) return;
    try {
      await request.delete(`/api/admin/categories/${id}`);
      fetchCategories();
    } catch (error) {
      // Error handled silently
    }
  };

  const handleEdit = (category: any) => {
    setEditId(category.id);
    setForm({ name: category.name, slug: category.slug || '', description: category.description || '' });
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.name) return alert('分类名称不能为空');
    try {
      if (editId) {
        await request.put(`/api/admin/categories/${editId}`, form);
      } else {
        await request.post('/api/admin/categories', form);
      }
      setShowModal(false);
      setForm({ name: '', slug: '', description: '' });
      setEditId(null);
      fetchCategories();
    } catch (error) {
      // Error handled silently
    }
  };

  return (
    <div className="space-y-6">
      <AdminPageHeader
        title="分类管理"
        actions={
          <button
            onClick={() => { setEditId(null); setForm({ name: '', slug: '', description: '' }); setShowModal(true); }}
            className="flex items-center gap-2 rounded-xl border-none bg-gradient-to-r from-purple-600 to-blue-600 px-4 py-2 text-sm font-medium text-white shadow-[0_0_15px_rgba(168,85,247,0.4)] transition-all hover:from-purple-500 hover:to-blue-500"
          >
            <Plus className="h-4 w-4" />
            新增分类
          </button>
        }
      />

      <div className="bg-slate-950/45 border border-white/5 rounded-3xl p-4 shadow-[0_16px_45px_rgba(0,0,0,0.18)] flex flex-wrap gap-4 items-center justify-between">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-white/40" />
          <input 
            type="text" 
            placeholder="搜索分类名称..."
            className="py-2 pl-9 pr-4 rounded-lg bg-white/5 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 transition-all text-white placeholder-slate-500 w-64"
          />
        </div>
      </div>

      <div className="bg-slate-950/45 border border-white/5 rounded-3xl shadow-[0_16px_45px_rgba(0,0,0,0.18)] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/10 bg-black/20 text-xs uppercase tracking-wider text-slate-400">
                <th className="px-6 py-4 font-medium">分类名称</th>
                <th className="px-6 py-4 font-medium">别名</th>
                <th className="px-6 py-4 font-medium">描述</th>
                <th className="px-6 py-4 font-medium">文章数</th>
                <th className="px-6 py-4 font-medium">创建时间</th>
                <th className="px-6 py-4 font-medium text-right">操作</th>
              </tr>
            </thead>
            <tbody className="text-sm divide-y divide-white/5">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">加载中...</td>
                </tr>
              ) : categories.length > 0 ? (
                categories.map((category) => (
                  <tr key={category.id} className="hover:bg-white/5 transition-colors">
                    <td className="px-6 py-4 font-medium text-white">{category.name}</td>
                    <td className="px-6 py-4 text-slate-400">{category.slug}</td>
                    <td className="px-6 py-4 text-slate-400 max-w-[200px] truncate" title={category.description}>{category.description || '-'}</td>
                    <td className="px-6 py-4">
                      <span className="px-2 py-1 rounded bg-white/5 border border-white/10 text-slate-300 text-xs">
                        {category.articleCount || 0} 篇
                      </span>
                    </td>
                    <td className="px-6 py-4 text-slate-400">
                      {category.createdAt ? format(new Date(category.createdAt), 'yyyy-MM-dd HH:mm') : '-'}
                    </td>
                    <td className="px-6 py-4 text-right">
                       <div className="flex justify-end gap-3">
                         <button 
                           onClick={() => handleEdit(category)}
                           className="text-slate-400 hover:text-purple-400 transition-colors" title="编辑">
                           <Edit className="w-4 h-4" />
                         </button>
                         <button 
                           onClick={() => handleDelete(category.id)}
                           className="text-slate-400 hover:text-red-400 transition-colors" title="删除">
                           <Trash2 className="w-4 h-4" />
                         </button>
                       </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center">
                      <FolderTree className="w-12 h-12 text-slate-700 mb-3" />
                      <p>暂无分类数据</p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* 模态框 */}
      {showModal && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
           <div className="bg-[#020617]/80 backdrop-blur-xl border border-white/5 rounded-3xl p-6 w-full max-w-md shadow-2xl">
             <h2 className="text-xl font-bold text-white mb-6">{editId ? '编辑分类' : '新增分类'}</h2>
             <form onSubmit={handleSubmit} className="space-y-4">
               <div>
                 <label className="block text-sm font-medium text-slate-400 mb-1">名称</label>
                 <input 
                   type="text" required value={form.name} onChange={e => setForm({...form, name: e.target.value})}
                   className="w-full py-2 px-3 bg-black/20 border border-white/10 rounded-lg text-white focus:outline-none focus:border-purple-500/50" 
                 />
               </div>
               <div>
                 <label className="block text-sm font-medium text-slate-400 mb-1">别名 (Slug)</label>
                 <input 
                   type="text" value={form.slug} onChange={e => setForm({...form, slug: e.target.value})}
                   className="w-full py-2 px-3 bg-black/20 border border-white/10 rounded-lg text-white focus:outline-none focus:border-purple-500/50" 
                 />
               </div>
               <div>
                 <label className="block text-sm font-medium text-slate-400 mb-1">描述</label>
                 <textarea 
                   value={form.description} onChange={e => setForm({...form, description: e.target.value})}
                   className="w-full py-2 px-3 bg-black/20 border border-white/10 rounded-lg text-white focus:outline-none focus:border-purple-500/50 resize-y min-h-[80px]" 
                 ></textarea>
               </div>
               <div className="pt-4 flex justify-end gap-3">
                 <button type="button" onClick={() => setShowModal(false)} className="px-4 py-2 text-slate-400 hover:text-white">取消</button>
                 <button type="submit" className="px-4 py-2 bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-500 hover:to-blue-500 text-white rounded-xl shadow-[0_0_15px_rgba(168,85,247,0.4)] border-none transition-all">保存</button>
               </div>
             </form>
           </div>
        </div>
      )}
    </div>
  );
}
