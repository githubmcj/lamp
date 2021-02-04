package com.wya.env.bean.doodle;

import java.util.List;

public class EventAddMode {
    private int position;
    private List<CopyModeColor> copyModeColor;
    private int speed;
    private String name;
    private int lightType;
    private boolean del;

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public int getLightType() {
        return lightType;
    }

    public void setLightType(int lightType) {
        this.lightType = lightType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CopyModeColor> getCopyModeColor() {
        return copyModeColor;
    }

    public void setCopyModeColor(List<CopyModeColor> copyModeColor) {
        this.copyModeColor = copyModeColor;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
