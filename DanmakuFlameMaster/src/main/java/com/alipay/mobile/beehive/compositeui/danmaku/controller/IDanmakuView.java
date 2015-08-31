
package com.alipay.mobile.beehive.compositeui.danmaku.controller;

import android.view.View;

import java.util.Collection;
import java.util.List;

import com.alipay.mobile.beehive.compositeui.danmaku.controller.DrawHandler.Callback;
import com.alipay.mobile.beehive.compositeui.danmaku.model.BaseDanmaku;
import com.alipay.mobile.beehive.compositeui.danmaku.parser.BaseDanmakuParser;

public interface IDanmakuView {
    
    public final static int THREAD_TYPE_NORMAL_PRIORITY = 0x0;
    public final static int THREAD_TYPE_MAIN_THREAD = 0x1;
    public final static int THREAD_TYPE_HIGH_PRIORITY = 0x2;
    public final static int THREAD_TYPE_LOW_PRIORITY = 0x3;

//    public void init();
//
//    public void init(Collection<BaseDanmaku> danmakus);

    boolean isStarted();

    public boolean isPrepared();
    
    public boolean isPaused();

    public boolean isHardwareAccelerated();
    /**
     * 
     * @param type One of THREAD_TYPE_MAIN_THREAD, THREAD_TYPE_HIGH_PRIORITY, THREAD_TYPE_NORMAL_PRIORITY, or THREAD_TYPE_LOW_PRIORITY.
     */
    public void setDrawingThreadType(int type);

    public void enableDanmakuDrawingCache(boolean enable);

    public boolean isDanmakuDrawingCacheEnabled();

    public void showFPS(boolean show);

    public void addDanmaku(List<BaseDanmaku> items);

    public void addDanmaku(BaseDanmaku item);
    
    public void removeAllDanmakus();
    
    public void removeAllLiveDanmakus();
    
    public void setCallback(Callback callback);
    
    /**
     * for getting the accurate play-time. use this method intead of parser.getTimer().currMillisecond
     * @return
     */
    public long getCurrentTime();
    
    
    // ------------- Android View方法  --------------------
    
    public View getView();

    public int getWidth();

    public int getHeight();

    public void setVisibility(int visibility);
    
    public boolean isShown();
    

    // ------------- 播放控制 -------------------
    
    public void prepareParser(BaseDanmakuParser parser);

    public void seekTo(Long ms);

    public void start(long position);

    public void start();

    public void internalStart();

    public void internalStart(long postion);

    public void stop();

    public void pause();

    public void resume();

    public void release();
    
    public void toggle();
    
    public void show();

    public void hide();
    
    public void internalHide();

    public void setVideoStartTime(long videoStartTime);
    
    /**
     * show the danmakuview again if you called hideAndPauseDrawTask()
     * @param position The position you want to resume
     * @see #hideAndPauseDrawTask
     */
    public void showAndResumeDrawTask(Long position);
    
    /**
     * InternalHide the danmakuview and pause the drawtask
     * @return the paused position
     * @see #showAndResumeDrawTask
     */
    public long hideAndPauseDrawTask();

    public void clearDanmakusOnScreen();
}
