package com.wya.env.bean.doodle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DoodlePattern {
    private int column = 20;
    boolean isChose;
    HashMap<String, Doodle> light_status;
    List<Doodle> doodles;
    String name;
    boolean music;

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
