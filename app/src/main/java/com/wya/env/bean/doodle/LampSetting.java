package com.wya.env.bean.doodle;

public class LampSetting {
    private String name;
    private boolean isOpen;
    private boolean hasTimer;
    private long startTime;
    private long stopTime;

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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }
}
