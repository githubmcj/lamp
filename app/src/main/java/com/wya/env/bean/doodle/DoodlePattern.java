package com.wya.env.bean.doodle;

import java.util.List;

public  class DoodlePattern {
    boolean isChose;
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
