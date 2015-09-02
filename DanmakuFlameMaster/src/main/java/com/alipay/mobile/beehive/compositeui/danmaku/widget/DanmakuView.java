/*
 * Copyright (C) 2013 Chen Hui <calmer91@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alipay.mobile.beehive.compositeui.danmaku.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.alipay.mobile.beehive.compositeui.danmaku.controller.DrawHandler;
import com.alipay.mobile.beehive.compositeui.danmaku.controller.IDanmakuView;
import com.alipay.mobile.beehive.compositeui.danmaku.controller.IDanmakuViewController;
import com.alipay.mobile.beehive.compositeui.danmaku.controller.DrawHandler.Callback;
import com.alipay.mobile.beehive.compositeui.danmaku.controller.DrawHelper;
import com.alipay.mobile.beehive.compositeui.danmaku.model.BaseDanmaku;
import com.alipay.mobile.beehive.compositeui.danmaku.model.DanmakuTimer;
import com.alipay.mobile.beehive.compositeui.danmaku.model.IDanmakus;
import com.alipay.mobile.beehive.compositeui.danmaku.model.android.Danmakus;
import com.alipay.mobile.beehive.compositeui.danmaku.parser.BaseDanmakuParser;
import com.alipay.mobile.beehive.compositeui.danmaku.renderer.IRenderer.RenderingState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class DanmakuView extends View implements IDanmakuView, IDanmakuViewController {

    public static final String TAG = "DanmakuView";

    private Callback mCallback;

    private HandlerThread mHandlerThread;

    private DrawHandler handler;

    private boolean isSurfaceCreated;

    private boolean mEnableDanmakuDrwaingCache = true;

    private boolean mShowFps;

    private boolean mDanmakuVisible = true;

    protected int mDrawingThreadType = THREAD_TYPE_NORMAL_PRIORITY;

    private Object mDrawMonitor = new Object();

    private boolean mDrawFinished = false;

    private boolean mRequestRender = false;

    private long mUiThreadId;

    private long danmukuInterval = 200;

    private long lastDanmakuShowTime = 0;

    public void setDanmukuInterval(long danmukuInterval) {
        this.danmukuInterval = Math.max(0, danmukuInterval);
    }

    public long getDanmukuInterval() {
        return danmukuInterval;
    }

    /**
     * 弹幕的临时容器，在弹幕没有显示之前添加进来的弹幕会被临时放到这里，
     * 在弹幕显示的时候，这些弹幕会被真正的添加到弹幕视图
     */
    private List<BaseDanmaku> danmakuBuffer;

    public DanmakuView(Context context) {
        super(context);
        internalInit();
    }

    public DanmakuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        internalInit();
    }

    public DanmakuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        internalInit();
    }

    private void internalInit() {
        mUiThreadId = Thread.currentThread().getId();
        setBackgroundColor(Color.TRANSPARENT);
        setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        DrawHelper.useDrawColorToClearCanvas(true, false);

        danmakuBuffer = new ArrayList<BaseDanmaku>();
    }

    public void start() {
        start(0);
    }

    public void start(final long position) {
        BaseDanmakuParser parser = new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                Danmakus danmakus = new Danmakus();
                if (danmakuBuffer != null && danmakuBuffer.size() != 0) {
                    for (BaseDanmaku danmaku : danmakuBuffer) {
                        danmaku.setTimer(getTimer());
                        danmakus.addItem(danmaku);
                    }
                }
                return danmakus;
            }
        };
        setCallback(new Callback() {

            @Override
            public void updateTimer(DanmakuTimer timer) {
            }

            @Override
            public void prepared() {
                internalStart(position);
            }
        });
        prepareParser(parser);
    }

    /**
     * 添加弹幕
     *
     * @param danmakus
     */
    public void addDanmaku(List<BaseDanmaku> danmakus) {
        if (isPrepared() && isShown()) {
            // 弹幕打开时，添加弹幕，
            // 如果没有弹幕要显示，弹幕将直接显示
            // 如果有弹幕要显示，此条弹幕排在这些弹幕之后显示
            long earliestShowTime = Math.max(getCurrentTime(), lastDanmakuShowTime) + danmukuInterval;
            for (int i = 0; i < danmakus.size(); i++) {
                BaseDanmaku danmaku = danmakus.get(i);
                danmaku.textSize = dp2px(danmaku.textSize);
                danmaku.time = earliestShowTime + i * danmukuInterval;
                handler.addDanmaku(danmaku);
            }
            lastDanmakuShowTime = earliestShowTime + (danmakus.size() - 1) * danmukuInterval;
        } else {
            //弹幕关闭时，添加弹幕，弹幕时间由弹幕次序计算得到
            //添加的弹幕都会放到中转站，等待显示
            int danmukuNum = danmakuBuffer.size();
            for (int i = 0; i < danmakus.size(); i++) {
                BaseDanmaku danmaku = danmakus.get(i);
                danmaku.textSize = dp2px(danmaku.textSize);
                danmaku.time = (danmukuNum + i + 1) * danmukuInterval;
                danmakuBuffer.add(danmaku);
            }
            lastDanmakuShowTime = danmakuBuffer.size() * danmukuInterval;
        }
    }

    /**
     * 添加弹幕
     *
     * @param danmaku
     */
    public void addDanmaku(BaseDanmaku danmaku) {
        danmaku.textSize = dp2px(danmaku.textSize);

        if (isPrepared() && isShown()) {
            // 弹幕打开时，添加弹幕，
            // 如果没有弹幕要显示，弹幕将直接显示
            // 如果有弹幕要显示，此条弹幕排在这些弹幕之后显示
            long earliestShowTime = Math.max(getCurrentTime(), lastDanmakuShowTime);
            danmaku.time = earliestShowTime + danmukuInterval;
            handler.addDanmaku(danmaku);
        } else {
            //弹幕关闭时，添加弹幕，弹幕时间由弹幕次序计算得到
            danmaku.time = (danmakuBuffer.size() + 1) * danmukuInterval;
            danmakuBuffer.add(danmaku);
        }
    }

    @Override
    public void removeAllDanmakus() {
        if (handler != null) {
            handler.removeAllDanmakus();
        }
    }

    @Override
    public void removeAllLiveDanmakus() {
        if (handler != null) {
            handler.removeAllLiveDanmakus();
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
        if (handler != null) {
            handler.setCallback(callback);
        }
    }

    @Override
    public void release() {
        stop();
        if (mDrawTimes != null) mDrawTimes.clear();
    }

    @Override
    public void stop() {
        stopDraw();
    }

    private void stopDraw() {
        if (handler != null) {
            handler.quit();
            handler = null;
        }
        if (mHandlerThread != null) {
            try {
                mHandlerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    protected Looper getLooper(int type) {
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }

        int priority;
        switch (type) {
            case THREAD_TYPE_MAIN_THREAD:
                return Looper.getMainLooper();
            case THREAD_TYPE_HIGH_PRIORITY:
                priority = android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY;
                break;
            case THREAD_TYPE_LOW_PRIORITY:
                priority = android.os.Process.THREAD_PRIORITY_LOWEST;
                break;
            case THREAD_TYPE_NORMAL_PRIORITY:
            default:
                priority = android.os.Process.THREAD_PRIORITY_DEFAULT;
                break;
        }
        String threadName = "DFM Handler Thread #" + priority;
        mHandlerThread = new HandlerThread(threadName, priority);
        mHandlerThread.start();
        return mHandlerThread.getLooper();
    }

    private void prepareHandler() {
        if (handler == null) {
            handler = new DrawHandler(getLooper(mDrawingThreadType), this, mDanmakuVisible);
        }
    }

    @Override
    public void prepareParser(BaseDanmakuParser parser) {
        prepareHandler();
        handler.setParser(parser);
        handler.setCallback(mCallback);
        handler.prepare();
    }

    @Override
    public boolean isStarted() {
        return isPrepared();
    }

    @Override
    public boolean isPrepared() {
        return handler != null && handler.isPrepared();
    }

    @Override
    public void showFPS(boolean show) {
        mShowFps = show;
    }

    private static final int MAX_RECORD_SIZE = 50;
    private static final int ONE_SECOND = 1000;
    private LinkedList<Long> mDrawTimes;

    private boolean mClearFlag;

    private float fps() {
        long lastTime = System.currentTimeMillis();
        mDrawTimes.addLast(lastTime);
        float dtime = lastTime - mDrawTimes.getFirst();
        int frames = mDrawTimes.size();
        if (frames > MAX_RECORD_SIZE) {
            mDrawTimes.removeFirst();
        }
        return dtime > 0 ? mDrawTimes.size() * ONE_SECOND / dtime : 0.0f;
    }

    @Override
    public long drawDanmakus() {
        if (!isSurfaceCreated)
            return 0;
        if (!isShown())
            return -1;
        long stime = System.currentTimeMillis();
        lockCanvas();
        return System.currentTimeMillis() - stime;
    }

    @SuppressLint("NewApi")
    private void postInvalidateCompat() {
        mRequestRender = true;
        if (Build.VERSION.SDK_INT >= 16) {
            this.postInvalidateOnAnimation();
        } else {
            this.postInvalidate();
        }
    }

    private void lockCanvas() {
        if (mDanmakuVisible == false) {
            return;
        }
        postInvalidateCompat();
        synchronized (mDrawMonitor) {
            while ((!mDrawFinished) && (handler != null)) {
                try {
                    mDrawMonitor.wait(200);
                } catch (InterruptedException e) {
                    if (mDanmakuVisible == false || handler == null || handler.isStop()) {
                        break;
                    } else {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            mDrawFinished = false;
        }
    }

    private void lockCanvasAndClear() {
        mClearFlag = true;
        lockCanvas();
    }

    private void unlockCanvasAndPost() {
        synchronized (mDrawMonitor) {
            mDrawFinished = true;
            mDrawMonitor.notifyAll();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if ((!mDanmakuVisible) && (!mRequestRender)) {
            super.onDraw(canvas);
            return;
        }
        if (mClearFlag) {
            DrawHelper.clearCanvas(canvas);
            mClearFlag = false;
        } else {
            if (handler != null) {
                RenderingState rs = handler.draw(canvas);
                if (mShowFps) {
                    if (mDrawTimes == null)
                        mDrawTimes = new LinkedList<Long>();
                    String fps = String.format(Locale.getDefault(),
                            "fps %.2f,time:%d s,cache:%d,miss:%d", fps(), getCurrentTime() / 1000,
                            rs.cacheHitCount, rs.cacheMissCount);
                    DrawHelper.drawFPS(canvas, fps);
                }
            }
        }
        mRequestRender = false;
        unlockCanvasAndPost();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (handler != null) {
            handler.notifyDispSizeChanged(right - left, bottom - top);
        }
        isSurfaceCreated = true;
    }

    public void toggle() {
        if (isSurfaceCreated) {
            if (handler == null)
                internalStart();
            else if (handler.isStop()) {
                resume();
            } else
                pause();
        }
    }

    @Override
    public void pause() {
        if (handler != null)
            handler.pause();
    }

    @Override
    public void resume() {
        if (handler != null && handler.isPrepared())
            handler.resume();
        else {
            restart();
        }
    }

    @Override
    public boolean isPaused() {
        if (handler != null) {
            return handler.isStop();
        }
        return false;
    }

    public void restart() {
        stop();
        internalStart();
    }

    @Override
    public void internalStart() {
        internalStart(0);
    }

    @Override
    public void internalStart(long postion) {
        if (handler == null) {
            prepareHandler();
        } else {
            handler.removeCallbacksAndMessages(null);
        }
        handler.obtainMessage(DrawHandler.START, postion).sendToTarget();
    }

    public void seekTo(Long ms) {
        if (handler != null) {
            handler.seekTo(ms);
        }
    }


    public void enableDanmakuDrawingCache(boolean enable) {
//        去掉DrawingCache
//        mEnableDanmakuDrwaingCache = enable;
    }

    @Override
    public boolean isDanmakuDrawingCacheEnabled() {
        return mEnableDanmakuDrwaingCache;
    }

    @Override
    public boolean isViewReady() {
        return isSurfaceCreated;
    }

    @Override
    public View getView() {
        return this;
    }

    /**
     * 打开弹幕
     */
    @Override
    public void show() {
        if (!isStarted()) {
            start();
        }
    }

    @Override
    public void hide() {
        //reset
        danmakuBuffer.clear();
        lastDanmakuShowTime = 0;

        stop();
    }

    @Override
    public void showAndResumeDrawTask(Long position) {
        mDanmakuVisible = true;
        mClearFlag = false;
        if (handler == null) {
            return;
        }
        handler.showDanmakus(position);
    }

    @Override
    public void internalHide() {
        mDanmakuVisible = false;
        if (handler == null) {
            return;
        }
        handler.hideDanmakus(false);
    }

    @Override
    public long hideAndPauseDrawTask() {
        mDanmakuVisible = false;
        if (handler == null) {
            return 0;
        }
        return handler.hideDanmakus(true);
    }

    @Override
    public void clear() {
        if (!isViewReady()) {
            return;
        }
        if (!mDanmakuVisible || Thread.currentThread().getId() == mUiThreadId) {
            mClearFlag = true;
            postInvalidateCompat();
        } else {
            lockCanvasAndClear();
        }
    }

    @Override
    public boolean isShown() {
        return super.isShown() && isPrepared() && mDanmakuVisible;
    }

    @Override
    public void setDrawingThreadType(int type) {
        mDrawingThreadType = type;
    }

    @Override
    public long getCurrentTime() {
        if (handler != null) {
            return handler.getCurrentTime();
        }
        return 0;
    }

    @Override
    @SuppressLint("NewApi")
    public boolean isHardwareAccelerated() {
        // >= 3.0
        if (Build.VERSION.SDK_INT >= 11) {
            return super.isHardwareAccelerated();
        } else {
            return false;
        }
    }

    @Override
    public void clearDanmakusOnScreen() {
        if (handler != null) {
            handler.clearDanmakusOnScreen();
        }
    }

    public float dp2px(final float dp) {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }
}
