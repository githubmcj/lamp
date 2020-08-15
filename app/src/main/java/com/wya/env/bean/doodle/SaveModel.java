package com.wya.env.bean.doodle;

public class SaveModel {

    /**
     * id : 2
     * userId : 12
     * gmtDatetime : 2020-08-15 12:04:43
     * content : {"isChose":false,"modeArr":[{"light_status":{"0":{"color":"#EA1318","createTime":1597464277165,"isFlash":0,"light":255,"showLight":0}}}],"music":false,"name":"2"}
     * status : 1
     */

    private int id;
    private int userId;
    private String gmtDatetime;
    private String content;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGmtDatetime() {
        return gmtDatetime;
    }

    public void setGmtDatetime(String gmtDatetime) {
        this.gmtDatetime = gmtDatetime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
