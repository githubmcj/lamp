package com.wya.env.bean.doodle;

public class DoodleConfig {
    private int LampNum;
    private int LampType;
    private String OEM1 ="JY";
    private String OEM2 = "00000000";
    private String ModeID = "LL200-0000000001";
    private String UID = "AC67B2E8BCF0";

    public int getLampNum() {
        return LampNum;
    }

    public void setLampNum(int lampNum) {
        LampNum = lampNum;
    }

    public int getLampType() {
        return LampType;
    }

    public void setLampType(int lampType) {
        LampType = lampType;
    }

    public String getOEM1() {
        return OEM1;
    }

    public void setOEM1(String OEM1) {
        this.OEM1 = OEM1;
    }

    public String getOEM2() {
        return OEM2;
    }

    public void setOEM2(String OEM2) {
        this.OEM2 = OEM2;
    }

    public String getModeID() {
        return ModeID;
    }

    public void setModeID(String modeID) {
        ModeID = modeID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
