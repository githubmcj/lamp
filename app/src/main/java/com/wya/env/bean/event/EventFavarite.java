package com.wya.env.bean.event;

public class EventFavarite {
    private String  creatTime;
    private int position;
    private int typeLamp;
    private boolean favorite;

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTypeLamp() {
        return typeLamp;
    }

    public void setTypeLamp(int typeLamp) {
        this.typeLamp = typeLamp;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
