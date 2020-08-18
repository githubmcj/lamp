package com.wya.env.bean.doodle;

import android.graphics.Color;

import com.wya.env.util.ColorUtil;

public class Doodle implements Cloneable {

    private String color;

    private int light = 255;

    /**
     * 是否闪烁 0 不闪， 1闪
     */
    private int isFlash;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int isFlash() {
        return isFlash;
    }

    public void setFlash(int flash) {
        isFlash = flash;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        if(light < 0 ){
            this.light = 0;
        } else if(light > 255){
            this.light = 255;
        } else {
            this.light = light;
        }
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
            return Color.argb(0, 0, 0, 0);
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
