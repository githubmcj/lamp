package com.wya.env.bean.doodle;

import android.graphics.Color;

import com.wya.env.util.ColorUtil;

public class Doodle {
    private int color;

    private int light;

    /**
     * 闪烁时候展示的亮度
     */
    private int showLight;

    /**
     * 是否闪烁
     */
    private boolean isTwinkle;

    /**
     * 创建时间
     */
    private long createTime;

    public int getShowLight() {
        return showLight;
    }

    public void setShowLight(int showLight) {
        this.showLight = showLight;
    }


    public boolean isTwinkle() {
        return isTwinkle;
    }

    public void setTwinkle(boolean twinkle) {
        isTwinkle = twinkle;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


    /**
     * 灯的颜色+亮度
     *
     * @return
     */
    int[] rgb;

    public int getLampColor() {
        if (light > 255) {
            return Color.argb(255, rgb[0], rgb[1], rgb[2]);
        } else if (light > 0) {
            rgb = ColorUtil.int2Rgb(color);
            return Color.argb(light, rgb[0], rgb[1], rgb[2]);
        } else {
            return Color.argb(255, 0, 0, 0);
        }
    }

    /**
     * 闪烁灯的颜色+亮度
     *
     * @return
     */
    public int getShowLampColor() {
        if (showLight > 255) {
            return Color.argb(255, rgb[0], rgb[1], rgb[2]);
        } else if (showLight > 0) {
            rgb = ColorUtil.int2Rgb(color);
            return Color.argb(showLight, rgb[0], rgb[1], rgb[2]);
        } else {
            return Color.argb(0, rgb[0], rgb[1], rgb[2]);
        }
    }
}
