package com.wya.env.bean.login;

import com.wya.env.bean.doodle.LampSetting;

import java.util.List;

public class Lamps {
    private List<LampSetting> lampSettings;

    private String chose_ip;
    private int size;
    private String name;
    private int column;
    private int row;

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
