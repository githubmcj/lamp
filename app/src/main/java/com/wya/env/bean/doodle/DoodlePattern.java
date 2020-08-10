package com.wya.env.bean.doodle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DoodlePattern {
    private int column = 20;
    boolean isChose;
    List<Doodle> doodles;
    List<Doodle> mirror_doodles;
    String name;
    boolean music;

    public List<Doodle> getMirror_doodles() {
        mirror_doodles = toMirror(doodles);
        return mirror_doodles;
    }

    private List<Doodle> toMirror(List<Doodle> doodles) {
        mirror_doodles = depCopy(doodles);
        for (int i = 0; i < mirror_doodles.size() / 2; i++) {
            if (i >= 0 && i < mirror_doodles.size() / column) {
                Collections.swap(mirror_doodles, i, 270 - i);
            }
        }
        return mirror_doodles;
    }

    /**
     * 深拷贝
     *
     * @param doodles
     * @return
     */
    public List<Doodle> depCopy(List<Doodle> doodles) {
        List<Doodle> destList = new ArrayList<>();
        for (Doodle doodle : doodles) {
            destList.add((Doodle) doodle.clone());
        }
        return destList;
    }

    public void setMirror_doodles(List<Doodle> mirror_doodles) {
        this.mirror_doodles = mirror_doodles;
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

    public List<Doodle> getDoodles() {
        return doodles;
    }

    public void setDoodles(List<Doodle> doodles) {
        this.doodles = doodles;
    }
}
