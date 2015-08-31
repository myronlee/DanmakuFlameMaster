
package com.sample;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.VideoView;

import com.alipay.mobile.beehive.compositeui.danmaku.controller.IDanmakuView;
import com.alipay.mobile.beehive.compositeui.danmaku.model.AlipayDanmaku;
import com.alipay.mobile.beehive.compositeui.danmaku.model.BaseDanmaku;
import com.alipay.mobile.beehive.compositeui.danmaku.parser.BaseDanmakuParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private IDanmakuView mDanmakuView;

    private View mMediaController;

    public PopupWindow mPopupWindow;

    private Button mBtnRotate;

    private Button mBtnHideDanmaku;

    private Button mBtnShowDanmaku;

    private BaseDanmakuParser mParser;

    private Button mBtnPauseDanmaku;

    private Button mBtnResumeDanmaku;

    private Button mBtnSendDanmaku;

    private long mPausedPosition;

    private Button mBtnSendDanmakus;
    private EditText editText;
    private long videoStartTime;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        mMediaController = findViewById(R.id.media_controller);
        mBtnRotate = (Button) findViewById(R.id.rotate);
        mBtnHideDanmaku = (Button) findViewById(R.id.btn_hide);
        mBtnShowDanmaku = (Button) findViewById(R.id.btn_show);
        mBtnPauseDanmaku = (Button) findViewById(R.id.btn_pause);
        mBtnResumeDanmaku = (Button) findViewById(R.id.btn_resume);
        mBtnSendDanmaku = (Button) findViewById(R.id.btn_send);
        mBtnSendDanmakus = (Button) findViewById(R.id.btn_send_danmakus);
        mBtnRotate.setOnClickListener(this);
        mBtnHideDanmaku.setOnClickListener(this);
        mMediaController.setOnClickListener(this);
        mBtnShowDanmaku.setOnClickListener(this);
        mBtnPauseDanmaku.setOnClickListener(this);
        mBtnResumeDanmaku.setOnClickListener(this);
        mBtnSendDanmaku.setOnClickListener(this);
        mBtnSendDanmakus.setOnClickListener(this);

        editText = (EditText) findViewById(R.id.editText);
        editText.setOnClickListener(this);

        // VideoView
        VideoView mVideoView = (VideoView) findViewById(R.id.videoview);
        // DanmakuView
        mDanmakuView = (IDanmakuView) findViewById(R.id.sv_danmaku);



        if (mVideoView != null) {
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
//                    mDanmakuView.setVideoStartTime(System.currentTimeMillis());
//                    videoStartTime = System.currentTimeMillis();
                }
            });
            mVideoView.setVideoPath(Environment.getExternalStorageDirectory() + "/1.mp4");
        }

    }

    private List<BaseDanmaku> getDanmukus(String prefix) {

        List<BaseDanmaku> danmakus = new ArrayList<BaseDanmaku>();
        for (int i = 0; i < 10; i++) {
            danmakus.add(new AlipayDanmaku.Builder(prefix +  + i + i + i + "1584个收藏是闹那样", i * 1000 + 300)
                    .build());
            danmakus.add(new AlipayDanmaku.Builder(prefix +  + i + i + i + "。。。。。。。。。。。。。。。", i * 1000 + 200)
                    .duration(2000)
                    .build());
            danmakus.add(new AlipayDanmaku.Builder(prefix +  + i + i + i + "   暖被窝一次一千円节假日半价  /", i * 1000 + 400)
                    .textColor(Color.YELLOW)
                    .build());
            danmakus.add(new AlipayDanmaku.Builder(prefix +  + i + i + i + "1耶耶耶耶耶耶耶耶耶耶耶耶", i * 1000 + 800)
                    .textSize(32)
                    .build());
            danmakus.add(new AlipayDanmaku.Builder(prefix +  + i + i + i + "我是小伙伴！ 我是小伙伴！", i * 1000 + 900)
                    .duration(1500)
                    .textColor(Color.YELLOW)
                    .textSize(32)
                    .build());
        }
        return danmakus;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }
/*

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

*/

    @Override
    public void onClick(View v) {

        if (v == mBtnHideDanmaku) {
            mDanmakuView.hide();
        } else if (v == mBtnShowDanmaku) {
            mDanmakuView.addDanmaku(getDanmukus(String.valueOf(++count)));
            mDanmakuView.show();
        } else if (v == mBtnSendDanmaku) {
            mDanmakuView.addDanmaku(new AlipayDanmaku.Builder("这条弹幕会立即显示", mDanmakuView.getCurrentTime()).textColor(Color.YELLOW).build());
//            addDanmakuNow();
        }
    }


    private void addDanmakuNow() {
        if (!mDanmakuView.isStarted()) {
            Toast.makeText(MainActivity.this, "弹幕视图还没有启用", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = editText.getText().toString();
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(MainActivity.this, "弹幕不能为空", Toast.LENGTH_SHORT).show();
        } else {
            //如果想要让弹幕立刻显示，可设置弹幕的time为DanmakuView.getCurrentTime()（即视频当前播放的时间）
            mDanmakuView.addDanmaku(new AlipayDanmaku.Builder(comment, mDanmakuView.getCurrentTime()).textColor(Color.YELLOW).build());
        }
    }

}
