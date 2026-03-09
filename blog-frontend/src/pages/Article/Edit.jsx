import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Select, App, Upload, Tooltip } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import { Image as ImageIcon, Trash2, ArrowLeft, Settings2 } from 'lucide-react';
import MDEditor from '@uiw/react-md-editor';
import request from '../../utils/request';
import { toRelativeMediaUrl } from '../../utils/media';

const { Option } = Select;
const { TextArea } = Input;

const ArticleEdit = () => {
    const { message } = App.useApp();
    const [form] = Form.useForm();
    const [categories, setCategories] = useState([]);
    const [tags, setTags] = useState([]);
    const [coverUrl, setCoverUrl] = useState('');
    const [uploading, setUploading] = useState(false);
    const [savingStatus, setSavingStatus] = useState(null);

    const navigate = useNavigate();
    const { id } = useParams();
    const isEdit = !!id;

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            navigate('/login');
            return;
        }

        fetchOptions();
        if (isEdit) {
            fetchArticle();
        }
    }, [id, navigate]);

    const fetchOptions = async () => {
        try {
            const [catRes, tagRes] = await Promise.all([
                request.get('/api/public/categories'),
                request.get('/api/public/tags'),
            ]);
            const catData = catRes.data || catRes;
            const tagData = tagRes.data || tagRes;
            setCategories(Array.isArray(catData) ? catData : catData.items || []);
            setTags(Array.isArray(tagData) ? tagData : tagData.items || []);
        } catch (error) {
            message.error('获取分类或标签失败');
        }
    };

    const fetchArticle = async () => {
        try {
            const res = await request.get(`/api/admin/articles/${id}`);
            const data = res.data || res;
            const coverImage = toRelativeMediaUrl(data.coverImage || data.cover || '');
            form.setFieldsValue({
                ...data,
                categoryId: data.category?.id || data.categoryId,
                tagIds: data.tags?.map((tag) => tag.id) || data.tagIds || [],
                coverImage,
            });
            if (coverImage) {
                setCoverUrl(coverImage);
            }
        } catch (error) {
            message.error('获取文章信息失败');
        }
    };

    const handleUpload = async (options) => {
        const { file, onSuccess, onError } = options;
        const formData = new FormData();
        formData.append('file', file);
        formData.append('dir', 'covers');

        setUploading(true);
        try {
            const res = await request.post('/api/admin/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            const responseData = res.data || res;
            const imageUrl = toRelativeMediaUrl(responseData.url);
            setCoverUrl(imageUrl);
            form.setFieldsValue({ coverImage: imageUrl });
            onSuccess('ok');
            message.success('封面上传成功');
        } catch (error) {
            onError(error);
            message.error('封面上传失败');
        } finally {
            setUploading(false);
        }
    };

    const removeCover = (event) => {
        event.stopPropagation();
        setCoverUrl('');
        form.setFieldsValue({ coverImage: '' });
    };

    const handleSave = async (status) => {
        try {
            const values = await form.validateFields();
            setSavingStatus(status);
            onFinish({ ...values, status });
        } catch {
            message.warning('请填写必填项，如标题和正文');
        }
    };

    const onFinish = async (values) => {
        try {
            const { tagIds, ...otherValues } = values;
            const validCategoryId = otherValues.categoryId && otherValues.categoryId > 0 ? otherValues.categoryId : null;

            const payload = {
                title: otherValues.title?.trim() || '',
                summary: otherValues.summary?.trim() || '',
                content: otherValues.content?.trim() || '',
                coverImage: otherValues.coverImage || '',
                ...(validCategoryId && { categoryId: validCategoryId }),
                ...(otherValues.status && { status: otherValues.status }),
            };

            if (!payload.title) {
                message.error('标题不能为空');
                return;
            }
            if (!payload.content) {
                message.error('正文内容不能为空');
                return;
            }

            let articleId = id;

            if (isEdit) {
                await request.put(`/api/admin/articles/${id}`, payload);
            } else {
                const res = await request.post('/api/admin/articles', payload);
                const resData = res.data || res;
                articleId = resData.id;
            }

            if (articleId && tagIds !== undefined) {
                try {
                    await request.put(`/api/admin/articles/${articleId}/tags`, { tagIds });
                } catch {
                    message.warning('文章保存成功，但标签更新失败');
                }
            }

            message.success(values.status === 'PUBLISHED' ? '发布成功' : '草稿保存成功');
            navigate('/articles');
        } catch (error) {
            let errorMessage = '保存失败';
            if (error?.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error?.response?.data?.error) {
                errorMessage = error.response.data.error;
            } else if (error?.message) {
                errorMessage = error.message;
            }

            message.error(errorMessage);
        } finally {
            setSavingStatus(null);
        }
    };

    return (
        <div className="animate-in fade-in slide-in-from-bottom-4 duration-700 min-h-full flex flex-col font-sans w-full bg-transparent">
            <Form form={form} layout="vertical" className="flex flex-col h-full pb-24">
                <div className="sticky top-0 z-50 flex items-center justify-between px-8 py-4 border-b border-slate-200 dark:border-white/5 bg-white/80 dark:bg-[#09090b]/80 backdrop-blur-xl">
                    <Button
                        type="text"
                        icon={<ArrowLeft className="w-4 h-4" />}
                        onClick={() => navigate('/articles')}
                        className="flex items-center text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white transition-colors px-0 h-auto"
                    >
                        返回列表
                    </Button>

                    <div className="flex items-center gap-4">
                        <Button
                            onClick={() => handleSave('DRAFT')}
                            loading={savingStatus === 'DRAFT'}
                            className="bg-transparent hover:bg-slate-100 dark:hover:bg-white/5 border-none text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 font-medium transition-all shadow-none"
                        >
                            保存草稿
                        </Button>
                        <Button
                            type="primary"
                            onClick={() => handleSave('PUBLISHED')}
                            loading={savingStatus === 'PUBLISHED'}
                            className="bg-indigo-500 hover:bg-indigo-600 border-none shadow-lg shadow-indigo-500/20 text-white font-medium transition-all px-6 h-9 rounded-full"
                        >
                            发布文章
                        </Button>
                    </div>
                </div>

                <div className="max-w-[1200px] mx-auto w-full mt-10">
                    <div className="mb-8 bg-transparent px-4">
                        <Form.Item
                            name="title"
                            rules={[{ required: true, message: '请在此输入标题...' }]}
                            className="mb-0"
                        >
                            <Input
                                placeholder="输入文章标题..."
                                variant="borderless"
                                className="text-4xl md:text-5xl lg:text-5xl font-extrabold bg-transparent px-0 text-slate-900 dark:text-slate-100 placeholder:text-slate-300 dark:placeholder:text-slate-700/50 caret-indigo-500 shadow-none focus:shadow-none tracking-tight h-auto py-2"
                            />
                        </Form.Item>
                    </div>

                    <div className="flex gap-12 px-4 border-t border-slate-200 dark:border-white/5 pt-10">
                        <div className="flex-[7] min-w-0 pr-6 border-r border-dashed border-white/5">
                            <Form.Item
                                name="content"
                                rules={[{ required: true, message: '正文内容不能为空' }]}
                                className="mb-0 h-full w-full custom-editor-wrapper"
                            >
                                <MDEditor
                                    height={650}
                                    minHeight={600}
                                    preview="live"
                                    className="w-full !bg-transparent !border-0 editor-override"
                                    textareaProps={{
                                        placeholder: '从这里开始撰写你的代码与正文...',
                                    }}
                                />
                            </Form.Item>
                        </div>

                        <div className="w-[300px] flex-[3] flex-shrink-0">
                            <div className="sticky top-[100px] space-y-8">
                                <div className="flex items-center gap-2 text-slate-500 pb-3 border-b border-slate-200 dark:border-white/5">
                                    <Settings2 className="w-4 h-4" />
                                    <span className="text-xs font-semibold tracking-wider uppercase">发布设置</span>
                                </div>

                                <div className="space-y-6">
                                    <Form.Item
                                        label={<span className="text-slate-600 dark:text-slate-400 font-medium text-sm">分类目录</span>}
                                        name="categoryId"
                                        className="mb-0 article-meta-form-item"
                                    >
                                        <Select
                                            placeholder="未选择分类"
                                            allowClear
                                            styles={{ popup: { root: { backgroundColor: 'var(--ant-color-bg-elevated)', borderColor: 'var(--ant-color-border-secondary)' } } }}
                                            className="w-full mt-1 border-b border-dashed border-slate-200 dark:border-white/10"
                                            variant="borderless"
                                        >
                                            {categories.map((cat) => (
                                                <Option key={cat.id} value={cat.id}>{cat.name}</Option>
                                            ))}
                                        </Select>
                                    </Form.Item>

                                    <Form.Item
                                        label={<span className="text-slate-600 dark:text-slate-400 font-medium text-sm">内容标签</span>}
                                        name="tagIds"
                                        className="mb-0 article-meta-form-item"
                                    >
                                        <Select
                                            mode="multiple"
                                            placeholder="添加标签..."
                                            allowClear
                                            styles={{ popup: { root: { backgroundColor: 'var(--ant-color-bg-elevated)', borderColor: 'var(--ant-color-border-secondary)' } } }}
                                            className="w-full mt-1 border-b border-dashed border-slate-200 dark:border-white/10"
                                            variant="borderless"
                                        >
                                            {tags.map((tag) => (
                                                <Option key={tag.id} value={tag.id}>{tag.name}</Option>
                                            ))}
                                        </Select>
                                    </Form.Item>

                                    <Form.Item
                                        label={<span className="text-slate-600 dark:text-slate-400 font-medium text-sm">文章封面</span>}
                                        className="mb-0"
                                    >
                                        <Form.Item name="coverImage" noStyle>
                                            <Input type="hidden" />
                                        </Form.Item>

                                        {!coverUrl ? (
                                            <Upload
                                                name="file"
                                                customRequest={handleUpload}
                                                showUploadList={false}
                                                accept="image/*"
                                            >
                                                <div className="mt-3 w-full aspect-video rounded-xl border border-dashed border-slate-200 dark:border-white/10 hover:border-indigo-500/40 hover:bg-slate-50 dark:hover:bg-indigo-500/5 transition-all cursor-pointer flex flex-col items-center justify-center bg-slate-50 dark:bg-black/20 group">
                                                    {uploading ? (
                                                        <div className="w-5 h-5 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin mb-2"></div>
                                                    ) : (
                                                        <ImageIcon className="w-5 h-5 text-slate-600 group-hover:text-indigo-400 mb-2 transition-colors" />
                                                    )}
                                                    <span className="text-xs text-slate-500 group-hover:text-indigo-300 font-medium">
                                                        {uploading ? '上传中...' : '点击设置封面图'}
                                                    </span>
                                                </div>
                                            </Upload>
                                        ) : (
                                            <div className="mt-3 relative group w-full aspect-video rounded-xl overflow-hidden border border-white/10 bg-slate-50 dark:bg-black/20">
                                                <img src={coverUrl} alt="Cover Preview" className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
                                                <div className="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center backdrop-blur-sm gap-2">
                                                    <Upload
                                                        name="file"
                                                        customRequest={handleUpload}
                                                        showUploadList={false}
                                                        accept="image/*"
                                                    >
                                                        <Tooltip title="替换">
                                                            <Button type="text" shape="circle" icon={<ImageIcon className="w-4 h-4 mx-auto text-white" />} className="flex items-center justify-center hover:bg-white/20" />
                                                        </Tooltip>
                                                    </Upload>
                                                    <Tooltip title="移除">
                                                        <Button type="text" shape="circle" onClick={removeCover} icon={<Trash2 className="w-4 h-4 mx-auto text-red-400" />} className="flex items-center justify-center hover:bg-red-500/20" />
                                                    </Tooltip>
                                                </div>
                                            </div>
                                        )}
                                    </Form.Item>

                                    <Form.Item
                                        label={<span className="text-slate-600 dark:text-slate-400 font-medium text-sm">内容摘要</span>}
                                        name="summary"
                                        className="mb-0"
                                    >
                                        <TextArea
                                            placeholder="简短引言概括..."
                                            autoSize={{ minRows: 4, maxRows: 6 }}
                                            variant="borderless"
                                            className="mt-3 text-sm bg-slate-50 dark:bg-black/20 border border-slate-200 dark:border-white/5 text-slate-800 dark:text-slate-300 focus:border-indigo-500/30 resize-none hover:bg-slate-100 dark:hover:bg-black/30 w-full rounded-xl p-3"
                                        />
                                    </Form.Item>

                                    <div className="pt-6 border-t border-white/5 flex items-center justify-between opacity-60">
                                        <span className="text-xs text-slate-600 dark:text-slate-500 font-medium">当前状态</span>
                                        <span className="text-xs font-semibold text-slate-300 bg-slate-200 dark:bg-white/5 px-2 py-1 rounded">
                                            {isEdit ? '编辑中' : '新草稿'}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </Form>

            <style>{`
                body[data-color-mode="dark"] .custom-editor-wrapper .w-md-editor {
                    color: #cbd5e1 !important;
                }
                .custom-editor-wrapper .w-md-editor {
                    background: transparent !important;
                    border: none !important;
                    box-shadow: none !important;
                }
                .custom-editor-wrapper .w-md-editor-toolbar {
                    background: transparent !important;
                    border-bottom: 1px dashed var(--ant-color-border-secondary) !important;
                    padding: 8px 0;
                    margin-bottom: 10px;
                }
                body[data-color-mode="dark"] .custom-editor-wrapper .w-md-editor-toolbar li > button {
                    color: #64748b !important;
                }
                .custom-editor-wrapper .w-md-editor-text-pre > code,
                .custom-editor-wrapper .w-md-editor-text-input {
                    font-size: 16px !important;
                    line-height: 1.8 !important;
                    color: inherit !important;
                    font-family: 'Fira Code', 'JetBrains Mono', Consolas, monospace !important;
                }
                .custom-editor-wrapper .wmde-markdown {
                    background: transparent !important;
                    font-size: 16px !important;
                    line-height: 1.8 !important;
                    color: inherit !important;
                }
                .article-meta-form-item .ant-select-selector {
                    background: transparent !important;
                    color: inherit !important;
                    border-radius: 0 !important;
                    padding-left: 0 !important;
                    padding-right: 0 !important;
                    box-shadow: none !important;
                }
                .article-meta-form-item .ant-select-focused .ant-select-selector {
                    box-shadow: none !important;
                }
                body[data-color-mode="dark"] .article-meta-form-item .ant-select-arrow {
                    color: #52525b !important;
                }
                body[data-color-mode="dark"] .article-meta-form-item .ant-select-selection-item {
                    color: #f4f4f5 !important;
                    font-weight: 500;
                }
                body[data-color-mode="dark"] .article-meta-form-item .ant-select-selection-placeholder {
                    color: #52525b !important;
                }
                body[data-color-mode="dark"] .article-meta-form-item .ant-select-multiple .ant-select-selection-item {
                    background: rgba(255, 255, 255, 0.05) !important;
                    border: 1px solid rgba(255, 255, 255, 0.1) !important;
                    border-radius: 6px;
                }
                body[data-color-mode="dark"] .article-meta-form-item .ant-select-multiple .ant-select-selection-item-remove {
                    color: #71717a !important;
                }
                body[data-color-mode="dark"] .article-meta-form-item .ant-select-multiple .ant-select-selection-item-remove:hover {
                    color: #818cf8 !important;
                }
                .ant-form-item-label > label {
                    height: auto !important;
                    padding-bottom: 4px !important;
                }
                .ant-form-item-label {
                    padding: 0 !important;
                }
                .ant-form-item {
                    margin-bottom: 0;
                }
                body[data-color-mode="dark"] textarea::placeholder,
                body[data-color-mode="dark"] input::placeholder {
                    color: rgba(255, 255, 255, 0.2) !important;
                }
            `}</style>
        </div>
    );
};

export default ArticleEdit;
