package com.wya.env.common;

/**
 * @date: 2018/7/3 13:51
 * @author: Chunjiang Mao
 * @classname: CommonValue
 * @describe: 公共字段类
 */

public interface CommonValue {
    /**
     * 是否登录成功
     */
    String IS_LOGIN = "is_login";
    /**
     * SharedPreferences文件名
     */
    String SHARE_PREFERENCES_NAME = "share_preferences_name";

    /**
     * 登录信息
     */
    String LOGIN_INFO = "LoginInfo";
    String TO_REFRESH = "ToRefresh";

    String TOKEN = "token";

    /**
     * 搜索到的灯
     */
    String LAMPS = "lamps";

     int UDP_PORT = 6000;
     int TCP_PORT = 6600;



    int column = 15;
    int size = 300;
    int row = 20;
}
