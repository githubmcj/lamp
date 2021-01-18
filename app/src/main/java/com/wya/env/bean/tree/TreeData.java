package com.wya.env.bean.tree;

import com.wya.env.bean.doodle.Doodle;

import java.util.List;

public class TreeData {
    private String Devname;

    private String Dimension;

    private int LampTotalNumber;

    private int lampXnumber;

    private int lampYnumber;

    private double AspectRatio;

    private List<Doodle> AddrTab;

    public int getLampXnumber() {
        return lampXnumber;
    }

    public void setLampXnumber(int lampXnumber) {
        this.lampXnumber = lampXnumber;
    }

    public int getLampYnumber() {
        return lampYnumber;
    }

    public void setLampYnumber(int lampYnumber) {
        this.lampYnumber = lampYnumber;
    }

    public String getDevname() {
        return Devname;
    }

    public void setDevname(String devname) {
        Devname = devname;
    }

    public String getDimension() {
        return Dimension;
    }

    public void setDimension(String dimension) {
        Dimension = dimension;
    }

    public int getLampTotalNumber() {
        return LampTotalNumber;
    }

    public void setLampTotalNumber(int lampTotalNumber) {
        LampTotalNumber = lampTotalNumber;
    }

    public double getAspectRatio() {
        return AspectRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        AspectRatio = aspectRatio;
    }

    public List<Doodle> getAddrTab() {
        return AddrTab;
    }

    public void setAddrTab(List<Doodle> addrTab) {
        AddrTab = addrTab;
    }
}
