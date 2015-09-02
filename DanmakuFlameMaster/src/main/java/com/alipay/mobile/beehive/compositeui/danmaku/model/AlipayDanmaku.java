package com.alipay.mobile.beehive.compositeui.danmaku.model;

import android.graphics.Color;

/**
 * Created by myron.lg on 2015/8/26.
 * ��Ļ
 */
public class AlipayDanmaku extends R2LDanmaku {


    public static class Builder {
        //�������,��Ϊ����������
        /**
         * ��Ļ���ݣ��Գ��Ȳ������ƣ����������15���ַ����ڣ����ڴ���֮������ȡ
         */
        private final String text;


        //���������ò���
        /**
         * ������Ļ��ʾʱ�����Ժ���Ϊ��λ��Ĭ��3��
         */
        private long duration = 3000;
        /**
         * ��Ļ�����С����dpΪ��λ��Ĭ��18dp
         */
        private int textSize = 18;
        /**
         * ��Ļ������ɫ��Ĭ�ϰ�ɫ
         */
        private int textColor = Color.WHITE;
        /**
         * ������Ļ���ֵ�ʱ�䣬�����ڵ�Ļ�����еĴ������
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
