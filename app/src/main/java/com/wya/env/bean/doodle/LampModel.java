package com.wya.env.bean.doodle;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @date: 2020/8/10 16:21
 * @author: Chunjiang Mao
 * @classname: ModeArr
 * @describe: 模板
 */
public class LampModel implements Cloneable, Serializable {

    private NetModel netModel;

    private List<DoodlePattern> modeArr;
    String name;
    String model_id;
    int id;
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
     * 如果是拷贝，存储的颜色 以,隔开  例#333333,#666666,#999999
     */
    List<CopyModeColor> copyModeColor;

    /**
     * 0为窗帘灯 1为圣诞树
     */
    int lightType;

    /**
     * 速度
     */
    int speed = 1;

    int fps = 1;

    String creatTime;

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public NetModel getNetModel() {
        return netModel;
    }

    public void setNetModel(NetModel netModel) {
        this.netModel = netModel;
    }

    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public List<CopyModeColor> getCopyModeColor() {
        return copyModeColor;
    }

    public void setCopyModeColor(List<CopyModeColor> copyModeColor) {
        this.copyModeColor = copyModeColor;
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

    @Override
    public LampModel clone() throws CloneNotSupportedException {
        LampModel lampModel = (LampModel) super.clone();
        return lampModel;
    }
}
