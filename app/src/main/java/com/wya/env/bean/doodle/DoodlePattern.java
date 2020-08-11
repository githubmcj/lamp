package com.wya.env.bean.doodle;

import java.util.HashMap;

/**
 * @date: 2020/8/11 10:01
 * @author: Chunjiang Mao
 * @classname: DoodlePattern
 * @describe: 一帧的样式
 */
public class DoodlePattern {

    private HashMap<String, Doodle> light_status;

    public HashMap<String, Doodle> getLight_status() {
        return light_status;
    }

    public void setLight_status(HashMap<String, Doodle> light_status) {
        this.light_status = light_status;
    }

}
