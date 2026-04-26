/*
 * 功能：前端模块逻辑。
 */
import React, { useRef, useState, useCallback, useEffect } from 'react';

const AvatarCropper = ({ image, onCrop, onCancel }) => {
    const containerRef = useRef(null);
    const [crop, setCrop] = useState({ x: 0, y: 0, size: 100 });
    const [isDragging, setIsDragging] = useState(false);
    const [isResizing, setIsResizing] = useState(false);
    const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });
    const [imageSize, setImageSize] = useState({ width: 0, height: 0 });

    const CONTAINER_SIZE = 280;
    const MIN_CROP_SIZE = 50;

    const handleImageLoad = useCallback((e) => {
        const { naturalWidth, naturalHeight } = e.target;
        setImageSize({ width: naturalWidth, height: naturalHeight });
        const minDim = Math.min(naturalWidth, naturalHeight);
        setCrop({
            x: (naturalWidth - minDim) / 2,
            y: (naturalHeight - minDim) / 2,
            size: minDim,
        });
    }, []);

    const getScaleParams = useCallback(() => {
        if (!imageSize.width || !imageSize.height) {
            return { scale: 1, dx: 0, dy: 0, w: CONTAINER_SIZE, h: CONTAINER_SIZE };
        }
        const scale = Math.min(CONTAINER_SIZE / imageSize.width, CONTAINER_SIZE / imageSize.height);
        const w = imageSize.width * scale;
        const h = imageSize.height * scale;
        const dx = (CONTAINER_SIZE - w) / 2;
        const dy = (CONTAINER_SIZE - h) / 2;
        return { scale, dx, dy, w, h };
    }, [imageSize]);

    const { scale, dx, dy, w, h } = getScaleParams();

    // 鎷栧姩瑁佸壀鍖哄煙
    const handleCropMouseDown = useCallback((e) => {
        e.preventDefault();
        e.stopPropagation();
        const container = containerRef.current;
        if (!container) return;

        const rect = container.getBoundingClientRect();
        const currentDisplayX = dx + crop.x * scale;
        const currentDisplayY = dy + crop.y * scale;

        setDragOffset({
            x: e.clientX - rect.left - currentDisplayX,
            y: e.clientY - rect.top - currentDisplayY,
        });
        setIsDragging(true);
    }, [crop, scale, dx, dy]);

    // 鎷栧姩璋冩暣澶у皬
    const handleResizeMouseDown = useCallback((e) => {
        e.preventDefault();
        e.stopPropagation();
        setIsResizing(true);
    }, []);

    const handleMouseMove = useCallback((e) => {
        const container = containerRef.current;
        if (!container || !imageSize.width) return;
        const rect = container.getBoundingClientRect();

        if (isDragging) {
            const cropDisplaySize = crop.size * scale;
            let newDisplayX = e.clientX - rect.left - dragOffset.x;
            let newDisplayY = e.clientY - rect.top - dragOffset.y;

            // Limit within the visible image bounds
            newDisplayX = Math.max(dx, Math.min(newDisplayX, dx + w - cropDisplaySize));
            newDisplayY = Math.max(dy, Math.min(newDisplayY, dy + h - cropDisplaySize));

            setCrop((prev) => ({
                ...prev,
                x: (newDisplayX - dx) / scale,
                y: (newDisplayY - dy) / scale,
            }));
        } else if (isResizing) {
            const cropDisplayX = dx + crop.x * scale;
            const cropDisplayY = dy + crop.y * scale;
            const mouseX = e.clientX - rect.left;
            const mouseY = e.clientY - rect.top;

            const centerX = cropDisplayX + (crop.size * scale) / 2;
            const centerY = cropDisplayY + (crop.size * scale) / 2;

            const distX = Math.abs(mouseX - centerX);
            const distY = Math.abs(mouseY - centerY);
            const newDisplaySize = Math.max(distX, distY) * 2;

            const minDisplaySize = MIN_CROP_SIZE * scale;
            const maxDisplaySize = Math.min(w, h); // Max size is limited by image min dimension display size

            const clampedDisplaySize = Math.max(minDisplaySize, Math.min(newDisplaySize, maxDisplaySize));
            const newSize = clampedDisplaySize / scale;

            let newX = crop.x + (crop.size - newSize) / 2;
            let newY = crop.y + (crop.size - newSize) / 2;

            // Ensure centered resizing stays within natural image bounds
            newX = Math.max(0, Math.min(newX, imageSize.width - newSize));
            newY = Math.max(0, Math.min(newY, imageSize.height - newSize));

            setCrop({
                x: newX,
                y: newY,
                size: newSize,
            });
        }
    }, [isDragging, isResizing, dragOffset, imageSize, crop, scale, dx, dy, w, h]);

    const handleMouseUp = useCallback(() => {
        setIsDragging(false);
        setIsResizing(false);
    }, []);

    useEffect(() => {
        const handleGlobalMouseMove = (e) => handleMouseMove(e);
        const handleGlobalMouseUp = () => handleMouseUp();

        if (isDragging || isResizing) {
            window.addEventListener('mousemove', handleGlobalMouseMove);
            window.addEventListener('mouseup', handleGlobalMouseUp);
        }

        return () => {
            window.removeEventListener('mousemove', handleGlobalMouseMove);
            window.removeEventListener('mouseup', handleGlobalMouseUp);
        };
    }, [isDragging, isResizing, handleMouseMove, handleMouseUp]);

    // 闃绘婊氳疆绌块€?    useEffect(() => {
        const preventScroll = (e) => {
            e.preventDefault();
            e.stopPropagation();
        };
        window.addEventListener('wheel', preventScroll, { passive: false });
        document.body.style.overflow = 'hidden';

        return () => {
            window.removeEventListener('wheel', preventScroll);
            document.body.style.overflow = '';
        };
    }, []);

    // 婊氳疆缂╂斁
    const handleWheel = useCallback((e) => {
        e.preventDefault();
        const delta = e.deltaY > 0 ? -10 : 10;
        const newSize = Math.max(MIN_CROP_SIZE, Math.min(crop.size + delta, Math.min(imageSize.width, imageSize.height)));

        let newX = crop.x - (newSize - crop.size) / 2;
        let newY = crop.y - (newSize - crop.size) / 2;

        newX = Math.max(0, Math.min(newX, imageSize.width - newSize));
        newY = Math.max(0, Math.min(newY, imageSize.height - newSize));

        setCrop({ x: newX, y: newY, size: newSize });
    }, [crop, imageSize]);

    const handleConfirm = useCallback(() => {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        const img = new Image();
        img.crossOrigin = 'anonymous';
        img.onload = () => {
            const outputSize = 400; // Increased resolution for better quality
            canvas.width = outputSize;
            canvas.height = outputSize;

            ctx.drawImage(
                img,
                crop.x,
                crop.y,
                crop.size,
                crop.size,
                0,
                0,
                outputSize,
                outputSize
            );

            canvas.toBlob((blob) => {
                onCrop(blob);
            }, 'image/png');
        };
        img.src = image;
    }, [image, crop, onCrop]);

    const cropDisplayX = dx + crop.x * scale;
    const cropDisplayY = dy + crop.y * scale;
    const cropDisplaySize = crop.size * scale;

    return (
        <div
            className="fixed inset-0 z-[100] flex items-center justify-center bg-black/90 p-4 backdrop-blur-sm"
            onWheel={(e) => { e.preventDefault(); e.stopPropagation(); }}
            style={{ touchAction: 'none' }}
        >
            <div className="w-full max-w-sm space-y-6 rounded-3xl border border-white/10 bg-[#0a0a0f] p-8 shadow-2xl">
                <div>
                    <h3 className="text-xl font-bold text-slate-100">瑁佸壀澶村儚</h3>
                    <p className="mt-2 text-sm text-slate-400">鎷栧姩绉诲姩鍖哄煙锛岄€氳繃婊氳疆鎴栬竟缂樻墜鏌勭缉鏀?/p>
                </div>

                <div
                    ref={containerRef}
                    className="relative mx-auto overflow-hidden rounded-2xl bg-black/40"
                    style={{ width: CONTAINER_SIZE, height: CONTAINER_SIZE }}
                    onWheel={handleWheel}
                >
                    {/* Background dimmed image */}
                    <img
                        src={image}
                        alt="棰勮"
                        className="absolute pointer-events-none opacity-30 select-none"
                        style={{
                            left: dx,
                            top: dy,
                            width: w,
                            height: h,
                        }}
                        onLoad={handleImageLoad}
                        draggable={false}
                    />
                    
                    {imageSize.width > 0 && (
                        <>
                            {/* Circle highlighting the crop area */}
                            <div
                                className="absolute cursor-move touch-none"
                                style={{
                                    left: cropDisplayX,
                                    top: cropDisplayY,
                                    width: cropDisplaySize,
                                    height: cropDisplaySize,
                                    borderRadius: '50%',
                                    overflow: 'hidden',
                                    border: '2px solid #22d3ee',
                                    boxShadow: '0 0 0 9999px rgba(0, 0, 0, 0.7)',
                                }}
                                onMouseDown={handleCropMouseDown}
                            >
                                <img
                                    src={image}
                                    alt="瑁佸壀棰勮"
                                    className="absolute pointer-events-none select-none"
                                    style={{
                                        left: dx - cropDisplayX,
                                        top: dy - cropDisplayY,
                                        width: w,
                                        height: h,
                                        maxWidth: 'none',
                                    }}
                                    draggable={false}
                                />
                            </div>

                            {/* Resize Handle */}
                            <div
                                className="absolute h-5 w-5 cursor-nwse-resize rounded-full border-2 border-cyan-400 bg-white shadow-lg shadow-cyan-500/50 transition-transform hover:scale-125"
                                style={{
                                    left: cropDisplayX + cropDisplaySize - 10,
                                    top: cropDisplayY + cropDisplaySize - 10,
                                    zIndex: 10,
                                }}
                                onMouseDown={handleResizeMouseDown}
                            />
                        </>
                    )}
                </div>

                <div className="flex gap-3 pt-2">
                    <button
                        type="button"
                        onClick={onCancel}
                        className="flex-1 rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm font-medium text-slate-300 transition hover:bg-white/10 hover:text-white"
                    >
                        鍙栨秷
                    </button>
                    <button
                        type="button"
                        onClick={handleConfirm}
                        className="flex-1 rounded-2xl bg-cyan-500 px-4 py-3 text-sm font-bold text-slate-950 shadow-lg shadow-cyan-500/20 transition hover:bg-cyan-400 hover:scale-[1.02]"
                    >
                        纭瑁佸壀
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AvatarCropper;

