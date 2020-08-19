package com.wya.env.bean.doodle;

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
    int isMirror;

    public int getIsMirror() {
        return isMirror;
    }

    public void setIsMirror(int isMirror) {
        this.isMirror = isMirror;
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
