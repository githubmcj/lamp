package com.wya.env.bean.event;

public class EventApply {
    /**
     * 0 开始， 1上传中， 2成功， 3异常
     */
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
