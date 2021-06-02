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

    /**
     * 圣诞树数据
     */
    String CONFIGFILE = "config_file";
    /**
     * 存的设备
     */
    String CONFIGDEVICE = "config_device";
    /**
     * 设备信息
     */
    String DOODLECONFIG = "doodle_config";

    int UDP_PORT = 6000;
    int TCP_PORT = 6600;

    String FAVORITE = "favorite";


    String COLUMN = "column";
    String SIZE = "size";
    String ROW = "row";

    String COLOR_TYPE = "color_type";
    String IP = "ip";
}
