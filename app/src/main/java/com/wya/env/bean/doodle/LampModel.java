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
    boolean music;
    boolean isChose;

    public List<DoodlePattern> getModeArr() {
        return modeArr;
    }

    public void setModeArr(List<DoodlePattern> modeArr) {
        this.modeArr = modeArr;
    }


    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChose() {
        return isChose;
    }

    public void setChose(boolean chose) {
        isChose = chose;
    }
}
