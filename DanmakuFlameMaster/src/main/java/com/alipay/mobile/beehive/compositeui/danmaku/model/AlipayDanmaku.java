package com.alipay.mobile.beehive.compositeui.danmaku.model;

import android.graphics.Color;

/**
 * Created by myron.lg on 2015/8/26.
 * 弹幕
 */
public class AlipayDanmaku extends R2LDanmaku {


    public static class Builder {
        //必需参数,作为构造器参数
        /**
         * 弹幕内容，对长度不做限制，如需控制在15个字符以内，请在传入之间做截取
         */
        private final String text;


        //其它可配置参数
        /**
         * 此条弹幕显示时长，以毫秒为单位，默认3秒
         */
        private long duration = 3000;
        /**
         * 弹幕字体大小，以dp为单位，默认18dp
         */
        private int textSize = 18;
        /**
         * 弹幕字体颜色，默认白色
         */
        private int textColor = Color.WHITE;
        /**
         * 此条弹幕出现的时间，由其在弹幕缓冲中的次序决定
         */
        private long time = -1;

        public Builder(String text) {
            this.text = text;
        }

        public Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder textSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public AlipayDanmaku build() {
            return new AlipayDanmaku(this);
        }
    }

    private AlipayDanmaku(Builder builder) {
        this(new Duration(builder.duration));
        this.text = builder.text;
        this.time = builder.time;
        this.duration = new Duration(builder.duration);
        this.textSize = builder.textSize;
        this.textColor = builder.textColor;
    }

    private AlipayDanmaku(Duration duration) {
        super(duration);
        this.isLive = true;
        this.priority = 1;
    }
}
