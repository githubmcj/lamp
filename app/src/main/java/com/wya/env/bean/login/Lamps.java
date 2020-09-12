package com.wya.env.bean.login;

import com.wya.env.bean.doodle.LampSetting;

import java.util.List;

public class Lamps {
    private List<LampSetting> lampSettings;

    private String chose_ip;
    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<LampSetting> getLampSettings() {
        return lampSettings;
    }

    public void setLampSettings(List<LampSetting> lampSettings) {
        this.lampSettings = lampSettings;
    }

    public String getChose_ip() {
        return chose_ip;
    }

    public void setChose_ip(String chose_ip) {
        this.chose_ip = chose_ip;
    }
}
