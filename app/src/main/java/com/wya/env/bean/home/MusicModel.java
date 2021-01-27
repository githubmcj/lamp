package com.wya.env.bean.home;

public class MusicModel {
    private int position;
    private int music;
    private boolean isClick;
    private int typeLamp;

    public int getTypeLamp() {
        return typeLamp;
    }

    public void setTypeLamp(int typeLamp) {
        this.typeLamp = typeLamp;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMusic() {
        return music;
    }

    public void setMusic(int music) {
        this.music = music;
    }
}
