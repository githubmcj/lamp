package com.wya.env.bean.doodle;

import android.graphics.Color;

import com.wya.env.util.ColorUtil;

public class Doodle implements Cloneable{

    private String color;

    private int light;

    /**
     * 闪烁时候展示的亮度
     */
    private int showLight;

    /**
     * 是否闪烁 0 不闪， 1闪
     */
    private int isFlash;

    /**
     * 创建时间
     */
    private long createTime;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getShowLight() {
        return showLight;
    }

    public void setShowLight(int showLight) {
        this.showLight = showLight;
    }


    public int isFlash() {
        return isFlash;
    }

    public void setFlash(int flash) {
        isFlash = flash;
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


    /**
     * 灯的颜色+亮度
     *
     * @return
     */
    public int getLampColor() {
        if (light > 255) {
            return Color.argb(255, ColorUtil.int2Rgb(Color.parseColor(color))[0], ColorUtil.int2Rgb(Color.parseColor(color))[1], ColorUtil.int2Rgb(Color.parseColor(color))[2]);
        } else if (light > 0) {
            return Color.argb(light, ColorUtil.int2Rgb(Color.parseColor(color))[0], ColorUtil.int2Rgb(Color.parseColor(color))[1], ColorUtil.int2Rgb(Color.parseColor(color))[2]);
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
            return Color.argb(255, ColorUtil.int2Rgb(Color.parseColor(color))[0], ColorUtil.int2Rgb(Color.parseColor(color))[1], ColorUtil.int2Rgb(Color.parseColor(color))[2]);
        } else if (showLight > 0) {
            return Color.argb(showLight, ColorUtil.int2Rgb(Color.parseColor(color))[0], ColorUtil.int2Rgb(Color.parseColor(color))[1], ColorUtil.int2Rgb(Color.parseColor(color))[2]);
        } else {
            return Color.argb(0, ColorUtil.int2Rgb(Color.parseColor(color))[0], ColorUtil.int2Rgb(Color.parseColor(color))[1], ColorUtil.int2Rgb(Color.parseColor(color))[2]);
        }
    }

    @Override
    public Object clone() {
        Doodle doodle = null;
        try {
            doodle = (Doodle) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return doodle;
    }
}
