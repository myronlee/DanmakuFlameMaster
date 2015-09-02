
package com.sample;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.VideoView;

import com.alipay.mobile.beehive.compositeui.danmaku.controller.IDanmakuView;
import com.alipay.mobile.beehive.compositeui.danmaku.model.AlipayDanmaku;
import com.alipay.mobile.beehive.compositeui.danmaku.model.BaseDanmaku;
import com.alipay.mobile.beehive.compositeui.danmaku.parser.BaseDanmakuParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private IDanmakuView mDanmakuView;

    private View mMediaController;

    public PopupWindow mPopupWindow;

    private Button mBtnRaiseDensity;

    private Button mBtnHideDanmaku;

    private Button mBtnShowDanmaku;

    private BaseDanmakuParser mParser;

    private Button mBtnToggleDanmaku;

    private Button mBtnReduceDanmaku;

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
        mBtnRaiseDensity = (Button) findViewById(R.id.rotate);
        mBtnHideDanmaku = (Button) findViewById(R.id.btn_hide);
        mBtnShowDanmaku = (Button) findViewById(R.id.btn_show);
        mBtnToggleDanmaku = (Button) findViewById(R.id.btn_pause);
        mBtnReduceDanmaku = (Button) findViewById(R.id.btn_resume);
        mBtnSendDanmaku = (Button) findViewById(R.id.btn_send);
        mBtnSendDanmakus = (Button) findViewById(R.id.btn_send_danmakus);
        mBtnRaiseDensity.setOnClickListener(this);
        mBtnHideDanmaku.setOnClickListener(this);
        mMediaController.setOnClickListener(this);
        mBtnShowDanmaku.setOnClickListener(this);
        mBtnToggleDanmaku.setOnClickListener(this);
        mBtnReduceDanmaku.setOnClickListener(this);
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
                }
            });
            mVideoView.setVideoPath(Environment.getExternalStorageDirectory() + "/1.mp4");
        }

    }

    private List<BaseDanmaku> getDanmukus(String prefix) {

        List<BaseDanmaku> danmakus = new ArrayList<BaseDanmaku>();
//        for (int i = 0; i < 5; i++) {
        danmakus.add(new AlipayDanmaku.Builder(prefix + "1584个收藏是闹那样")
                .build());
        danmakus.add(new AlipayDanmaku.Builder(prefix + "。。。。。。。。。。。。。。。")
                .duration(2000)
                .build());
        danmakus.add(new AlipayDanmaku.Builder(prefix + "   暖被窝一次一千円节假日半价  /")
                .textColor(Color.YELLOW)
                .build());
        danmakus.add(new AlipayDanmaku.Builder(prefix + "耶耶耶耶耶耶耶耶耶耶耶耶")
                .textSize(32)
                .build());
        danmakus.add(new AlipayDanmaku.Builder(prefix + "我是小伙伴！ 我是小伙伴！")
                .duration(1500)
                .textColor(Color.YELLOW)
                .textSize(32)
                .build());
        danmakus.add(new AlipayDanmaku.Builder(prefix + "一天不看香蕉君，不舒服斯基丶")
                .build());
        danmakus.add(new AlipayDanmaku.Builder(prefix + "卧槽")
                .duration(2000)
                .build());
        danmakus.add(new AlipayDanmaku.Builder(prefix + "小湿机你在看咩")
                .textColor(Color.YELLOW)
                .build());
        danmakus.add(new AlipayDanmaku.Builder(prefix + "金♂刚♂如♂来")
                .textSize(32)
                .build());
        danmakus.add(new AlipayDanmaku.Builder(prefix + "我瞎了。。。！")
                .duration(1500)
                .textColor(Color.YELLOW)
                .textSize(32)
                .build());
//        }
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
            mDanmakuView.show();
        } else if (v == mBtnSendDanmaku) {
            mDanmakuView.addDanmaku(new AlipayDanmaku.Builder("-----------一条弹幕---------------").textColor(Color.YELLOW).build());
        } else if (v == mBtnSendDanmakus) {
            mDanmakuView.addDanmaku(getDanmukus(String.valueOf(++count)));
        } else if (v == mBtnToggleDanmaku) {
            if (mDanmakuView.isShown()) {
                mDanmakuView.hide();
                mBtnToggleDanmaku.setText("打开弹幕");
            } else {
                mDanmakuView.show();
                mBtnToggleDanmaku.setText("关闭弹幕");
            }
        } else if(v == mBtnRaiseDensity){
            mDanmakuView.setDanmukuInterval(mDanmakuView.getDanmukuInterval()-20);
        } else if (v == mBtnReduceDanmaku){
            mDanmakuView.setDanmukuInterval(mDanmakuView.getDanmukuInterval()+20);
        }
    }
}
