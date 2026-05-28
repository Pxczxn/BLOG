/**
 * 社区用户个人中心
 */
import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'motion/react';
import toast from 'react-hot-toast';
import { User, Mail, Link as LinkIcon, Edit3, LogOut, Bell, FileText, Camera } from 'lucide-react';
import { useAuth } from '../lib/AuthContext';
import request, { getStaticUrl } from '../lib/request';
import RoleBadge from '../components/RoleBadge';
import Seo from '../components/Seo';

export default function UserProfile() {
  const { user, initializing, refreshUser, logout } = useAuth();
  const navigate = useNavigate();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [activeTab, setActiveTab] = useState('profile'); // profile, notifications
  const [form, setForm] = useState({
    displayName: '',
    bio: '',
    website: ''
  });
  const [saving, setSaving] = useState(false);
  const [notifications, setNotifications] = useState<any[]>([]);

  useEffect(() => {
    if (initializing) {
      return;
    }
    if (!user) {
      navigate('/login');
      return;
    }
    setForm({
      displayName: user.displayName || '',
      bio: user.bio || '',
      website: user.website || ''
    });
    
    if (activeTab === 'notifications') {
      fetchNotifications();
    }
  }, [user, initializing, navigate, activeTab]);

  const fetchNotifications = async () => {
    try {
      const res: any = await request.get('/api/community/notifications');
      setNotifications(res?.data ?? res ?? []);
    } catch (e) {
      console.error(e);
    }
  };

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setSaving(true);
      await request.patch('/api/community/me', form);
      await refreshUser();
      toast.success('资料更新成功', { duration: 1500 });
    } catch (error: any) {
      toast.error(error.message || '更新失败', { duration: 1800 });
    } finally {
      setSaving(false);
    }
  };

  const handleAvatarChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);
    
    try {
      setSaving(true);
      const res: any = await request.post('/api/community/upload/avatar', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      // Try to extract URL from various possible response formats
      const avatarUrl = res?.data?.url || res?.data || res?.url || res;
      if (typeof avatarUrl === 'string') {
        await request.patch('/api/community/me', { avatar: avatarUrl });
        await refreshUser();
        toast.success('头像更新成功', { duration: 1500 });
      } else {
        toast.error('头像上传成功，但没有拿到图片地址', { duration: 1800 });
      }
    } catch (error: any) {
      toast.error(error.message || '头像上传失败', { duration: 1800 });
    } finally {
      setSaving(false);
    }
  };

  const handleReadNotification = async (id: number) => {
    try {
      await request.post(`/api/community/notifications/${id}/read`);
      fetchNotifications();
    } catch (e) {
      console.error(e);
    }
  };

  if (initializing) {
    return (
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <Seo title="个人中心" description="社区用户个人中心。" path="/me" noindex />
        <div className="min-h-[40vh] flex items-center justify-center text-slate-400">
          正在同步你的账号信息...
        </div>
      </div>
    );
  }

  if (!user) return null;

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <Seo title="个人中心" description="管理个人资料、头像和通知消息。" path="/me" noindex />
      <div className="flex flex-col md:flex-row gap-8">
        
        {/* 侧边栏 */}
        <div className="md:w-64 shrink-0 space-y-4">
          <div className="bg-white/5 border border-white/10 rounded-3xl p-6 backdrop-blur-md flex flex-col items-center text-center">
            <div className="relative group mb-4">
              <div className="w-24 h-24 rounded-full border-2 border-purple-500/50 overflow-hidden bg-slate-800 flex items-center justify-center">
                {user.avatar ? (
                  <img src={getStaticUrl(user.avatar)} className="w-full h-full object-cover" />
                ) : (
                  <User className="w-10 h-10 text-slate-500" />
                )}
              </div>
              <div 
                onClick={() => fileInputRef.current?.click()}
                className="absolute inset-0 bg-black/50 flex items-center justify-center rounded-full opacity-0 group-hover:opacity-100 transition-opacity cursor-pointer backdrop-blur-sm"
              >
                <Camera className="w-6 h-6 text-white" />
              </div>
              <input type="file" ref={fileInputRef} className="hidden" accept="image/*" onChange={handleAvatarChange} />
            </div>
            
            <div className="mb-1 flex items-center gap-2">
              <h2 className="text-xl font-bold text-white">{user.displayName || user.username}</h2>
              <RoleBadge role={user.role} />
            </div>
            <p className="text-sm text-slate-400 mb-4">@{user.username}</p>
            
            <div className="flex gap-4 text-sm text-slate-300 w-full justify-center pt-4 border-t border-white/5">
               <div className="flex flex-col items-center">
                 <span className="font-bold text-white">{user.followerCount || 0}</span>
                 <span className="text-xs text-slate-500">粉丝</span>
               </div>
               <div className="flex flex-col items-center">
                 <span className="font-bold text-white">{user.followingCount || 0}</span>
                 <span className="text-xs text-slate-500">关注</span>
               </div>
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl overflow-hidden backdrop-blur-md">
             <button 
               onClick={() => setActiveTab('profile')}
               className={`w-full flex items-center gap-3 px-6 py-4 text-sm transition-colors ${activeTab === 'profile' ? 'bg-purple-500/20 text-purple-400 border-l-2 border-purple-500' : 'text-slate-300 hover:bg-white/5 border-l-2 border-transparent'}`}
             >
               <Edit3 className="w-4 h-4" /> 个人资料
             </button>
             <button 
               onClick={() => setActiveTab('notifications')}
               className={`w-full flex items-center gap-3 px-6 py-4 text-sm transition-colors ${activeTab === 'notifications' ? 'bg-purple-500/20 text-purple-400 border-l-2 border-purple-500' : 'text-slate-300 hover:bg-white/5 border-l-2 border-transparent'}`}
             >
               <Bell className="w-4 h-4" /> 消息通知
             </button>
             <button 
               onClick={logout}
               className="w-full flex items-center gap-3 px-6 py-4 text-sm text-red-400 hover:bg-red-500/10 border-l-2 border-transparent transition-colors"
             >
               <LogOut className="w-4 h-4" /> 退出登录
             </button>
          </div>
        </div>

        {/* 内容区域 */}
        <div className="flex-1">
          <motion.div 
            key={activeTab}
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.2 }}
            className="bg-white/5 border border-white/10 rounded-3xl p-6 md:p-10 backdrop-blur-md min-h-[400px]"
          >
             {activeTab === 'profile' && (
               <>
                 <h3 className="text-xl font-bold text-white mb-6">编辑资料</h3>
                 <form onSubmit={handleUpdateProfile} className="space-y-5">
                   <div>
                     <label className="block text-sm font-medium text-slate-400 mb-2">昵称</label>
                     <div className="relative">
                       <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                       <input 
                         type="text" value={form.displayName} onChange={e => setForm({...form, displayName: e.target.value})}
                         className="w-full pl-10 pr-4 py-2.5 bg-black/20 border border-white/10 rounded-xl text-white focus:outline-none focus:border-purple-500/50"
                       />
                     </div>
                   </div>
                   <div>
                     <label className="block text-sm font-medium text-slate-400 mb-2">个人主页 / 博客</label>
                     <div className="relative">
                       <LinkIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                       <input 
                         type="url" value={form.website} onChange={e => setForm({...form, website: e.target.value})}
                         placeholder="https://"
                         className="w-full pl-10 pr-4 py-2.5 bg-black/20 border border-white/10 rounded-xl text-white focus:outline-none focus:border-purple-500/50"
                       />
                     </div>
                   </div>
                   <div>
                     <label className="block text-sm font-medium text-slate-400 mb-2">个人简介</label>
                     <div className="relative">
                       <FileText className="absolute left-3 top-3 w-4 h-4 text-slate-500" />
                       <textarea 
                         value={form.bio} onChange={e => setForm({...form, bio: e.target.value})}
                         rows={4}
                         placeholder="介绍一下你自己..."
                         className="w-full pl-10 pr-4 py-2.5 bg-black/20 border border-white/10 rounded-xl text-white focus:outline-none focus:border-purple-500/50 resize-none"
                       ></textarea>
                     </div>
                   </div>
                   <div className="pt-4">
                     <button type="submit" disabled={saving} className="px-6 py-2.5 bg-purple-600 hover:bg-purple-500 text-white rounded-xl font-medium transition-all shadow-[0_0_15px_rgba(168,85,247,0.3)] disabled:opacity-50">
                       {saving ? '保存中...' : '保存更改'}
                     </button>
                   </div>
                 </form>
               </>
             )}

             {activeTab === 'notifications' && (
               <>
                 <h3 className="text-xl font-bold text-white mb-6">消息通知</h3>
                 <div className="space-y-4">
                   {notifications.length > 0 ? notifications.map(notify => (
                     <div key={notify.id} className={`p-4 rounded-2xl border transition-colors ${notify.isRead ? 'bg-white/5 border-white/5' : 'bg-purple-500/10 border-purple-500/30'}`}>
                       <div className="flex justify-between items-start gap-4">
                          <div>
                            <p className="text-sm text-slate-200 mb-1">{notify.content}</p>
                            <p className="text-xs text-slate-500">{new Date(notify.createdAt).toLocaleString()}</p>
                          </div>
                          {!notify.isRead && (
                             <button onClick={() => handleReadNotification(notify.id)} className="shrink-0 px-3 py-1 bg-white/10 hover:bg-white/20 rounded-lg text-xs text-white transition-colors">
                               标为已读
                             </button>
                          )}
                       </div>
                     </div>
                   )) : (
                     <div className="text-center py-16 text-slate-500 border border-white/5 rounded-2xl border-dashed">
                        <Bell className="w-8 h-8 text-slate-600 mx-auto mb-3" />
                        暂无新消息
                     </div>
                   )}
                 </div>
               </>
             )}
          </motion.div>
        </div>

      </div>
    </div>
  );
}
