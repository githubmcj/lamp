package com.wya.env.bean.doodle;

import java.io.Serializable;

public class CopyModeColor implements Serializable {
    private String color;
    private int w;
    private String showColor;

    public CopyModeColor(String showColor, int w, String color) {
        this.showColor = showColor;
        this.w = w;
        this.color = color;
    }

    public CopyModeColor() {

    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public String getShowColor() {
        return showColor;
    }

    public void setShowColor(String showColor) {
        this.showColor = showColor;
    }

}
