package com.wya.env.bean.login;

import com.wya.env.bean.doodle.LampModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @date: 2018/5/31 13:50
 * @author: Chunjiang Mao
 * @classname: LoginInfo
 * @describe: LoginInfo
 */

public class LoginInfo {

    /**
     * id : 1
     * userName : laji
     * password : 123456789
     * gmtDatatime : 2020-08-04 16:28:34
     * status : 1
     * userEmail : 15168300680@163.com
     * token : 3a4aa3e6-28a0-433b-bb1d-bd499df13f29
     * code : null
     */

    private int id;
    private String userName;
    private String password;
    private String gmtDatatime;
    private int status;
    private String userEmail;
    private String token;
    private String code;
    /**
     * 灯的模板
     */
    private List<LampModel> lampModels = new ArrayList<>();

    public List<LampModel> getLampModels() {
        return lampModels;
    }

    public void setLampModels(List<LampModel> lampModels) {
        this.lampModels = lampModels;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGmtDatatime() {
        return gmtDatatime;
    }

    public void setGmtDatatime(String gmtDatatime) {
        this.gmtDatatime = gmtDatatime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
