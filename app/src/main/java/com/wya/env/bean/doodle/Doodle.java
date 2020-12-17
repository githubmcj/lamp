package com.wya.env.bean.doodle;

import android.graphics.Color;

import com.wya.env.util.ColorUtil;

public class Doodle implements Cloneable {

    private String color;

    private String showColor;

    /**
     * 是否闪烁 0 不闪， 1闪
     */
    private int isFlash;


    private int addr;

    private float X;

    private float Y;

    private float Z;

    public int getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public float getZ() {
        return Z;
    }

    public void setZ(float z) {
        Z = z;
    }

    public String getShowColor() {
        return showColor;
    }

    public void setShowColor(String showColor) {
        this.showColor = showColor;
    }

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


    /**
     * 灯的颜色+亮度
     *
     * @return
     */
    public int getLampColor(int light) {
        if (light > 255) {
            if (showColor != null && !showColor.equals("")) {
                return Color.argb(255, ColorUtil.int2Rgb(Color.parseColor(showColor))[0], ColorUtil.int2Rgb(Color.parseColor(showColor))[1], ColorUtil.int2Rgb(Color.parseColor(showColor))[2]);
            } else if (color != null && !color.equals("")) {
                return Color.argb(255, ColorUtil.int2Rgb(Color.parseColor(color))[0], ColorUtil.int2Rgb(Color.parseColor(color))[1], ColorUtil.int2Rgb(Color.parseColor(color))[2]);
            } else {
                return Color.argb(0, 0, 0, 0);
            }
        } else if (light > 0) {
            if (showColor != null && !showColor.equals("")) {
                return Color.argb(light, ColorUtil.int2Rgb(Color.parseColor(showColor))[0], ColorUtil.int2Rgb(Color.parseColor(showColor))[1], ColorUtil.int2Rgb(Color.parseColor(showColor))[2]);
            } else if (color != null && !color.equals("")) {
                return Color.argb(light, ColorUtil.int2Rgb(Color.parseColor(color))[0], ColorUtil.int2Rgb(Color.parseColor(color))[1], ColorUtil.int2Rgb(Color.parseColor(color))[2]);
            } else {
                return Color.argb(0, 0, 0, 0);
            }
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
