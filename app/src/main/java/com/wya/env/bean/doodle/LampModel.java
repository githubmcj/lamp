package com.wya.env.bean.doodle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @date: 2020/8/10 16:21
 * @author: Chunjiang Mao
 * @classname: ModeArr
 * @describe: 模板
 */
public class LampModel {
    private List<DoodlePattern> modeArr;
    String name;
    int music;
    int isChose;
    int mirror;
    int column;
    int lightRow;
    int size;
    int light = 100;
    /**
     * 模式类型  0：固定模式  1:自定义模式  2:拷贝固定模式
     */
    int modeType;

    /**
     * 如果是拷贝，这个值为被copy的模式的下标
     */
    int copyModeIndex;

    /**
     *  如果是拷贝，存储的颜色 以,隔开  例#333333,#666666,#999999
     */
    String copyModeColor;

    /**
     * 0为窗帘灯 1为圣诞树
     */
    int lightType;

    /**
     * 速度
     */
    int speed;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getModeType() {
        return modeType;
    }

    public void setModeType(int modeType) {
        this.modeType = modeType;
    }

    public int getCopyModeIndex() {
        return copyModeIndex;
    }

    public void setCopyModeIndex(int copyModeIndex) {
        this.copyModeIndex = copyModeIndex;
    }

    public String getCopyModeColor() {
        return copyModeColor;
    }

    public void setCopyModeColor(String copyModeColor) {
        this.copyModeColor = copyModeColor;
    }

    public List<String> getCopyModeColorList() {
        return Arrays.asList(this.copyModeColor.split(","));
    }

    public int getLightType() {
        return lightType;
    }

    public void setLightType(int lightType) {
        this.lightType = lightType;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getLightRow() {
        return lightRow;
    }

    public void setLightRow(int lightRow) {
        this.lightRow = lightRow;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMirror() {
        return mirror;
    }

    public void setMirror(int mirror) {
        this.mirror = mirror;
    }

    public List<DoodlePattern> getModeArr() {
        return modeArr;
    }

    public void setModeArr(List<DoodlePattern> modeArr) {
        this.modeArr = modeArr;
    }

    public int isMusic() {
        return music;
    }

    public void setMusic(int music) {
        this.music = music;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int isChose() {
        return isChose;
    }

    public void setChose(int chose) {
        isChose = chose;
    }
}
