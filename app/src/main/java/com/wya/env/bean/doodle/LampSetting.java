package com.wya.env.bean.doodle;

public class LampSetting {
    private String name;
    private String deviceName;
    private boolean isChose;
    private boolean isOpen;
    private boolean hasTimer;
    private String s_hour = "0";
    private String s_min = "0";
    private String e_hour = "0";
    private String e_min = "0";
    private String ip;
    private int size;
    private int column;
    private int row;
    private String address;
    private String colorType;

    public String getColorType() {
        return colorType;
    }

    public void setColorType(String colorType) {
        this.colorType = colorType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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

    public String getS_hour() {
        return s_hour;
    }

    public void setS_hour(String s_hour) {
        this.s_hour = s_hour;
    }

    public String getS_min() {
        return s_min;
    }

    public void setS_min(String s_min) {
        this.s_min = s_min;
    }

    public String getE_hour() {
        return e_hour;
    }

    public void setE_hour(String e_hour) {
        this.e_hour = e_hour;
    }

    public String getE_min() {
        return e_min;
    }

    public void setE_min(String e_min) {
        this.e_min = e_min;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isChose() {
        return isChose;
    }

    public void setChose(boolean chose) {
        isChose = chose;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public boolean isHasTimer() {
        return hasTimer;
    }

    public void setHasTimer(boolean hasTimer) {
        this.hasTimer = hasTimer;
    }

}
