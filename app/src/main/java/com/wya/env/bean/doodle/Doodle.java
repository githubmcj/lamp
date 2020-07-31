package com.wya.env.bean.doodle;

import android.graphics.Color;

import com.wya.env.util.ColorUtil;

public class Doodle {
    private int color;

    private int light;

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
        if (light > 0) {
            rgb = ColorUtil.int2Rgb(color);
            return Color.argb(light, rgb[0], rgb[1], rgb[2]);
        } else {
            return Color.argb(255, 0, 0, 0);
        }
    }
}
