package com.wya.env.bean.event;

import com.wya.env.bean.doodle.LampModel;

public  class EventCustomLampModel {
    private int netType;
    private boolean favorite;
    private LampModel lampModel;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getNetType() {
        return netType;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }

    public LampModel getLampModel() {
        return lampModel;
    }

    public void setLampModel(LampModel lampModel) {
        this.lampModel = lampModel;
    }
}
