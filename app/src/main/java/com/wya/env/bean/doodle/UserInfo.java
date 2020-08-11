package com.wya.env.bean.doodle;

import java.util.ArrayList;
import java.util.List;
/**
 * @date: 2020/8/3 14:23
 * @author: Chunjiang Mao
 * @classname: UserInfo
 * @describe: 个人信息
 */
public class UserInfo {
    private String userName;
    private String email;
    private List<LampModel> lampModels = new ArrayList<>();

    public List<LampModel> getLampModels() {
        return lampModels;
    }

    public void setLampModels(List<LampModel> lampModels) {
        this.lampModels = lampModels;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
