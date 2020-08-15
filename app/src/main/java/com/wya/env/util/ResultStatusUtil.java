package com.wya.env.util;

import com.wya.env.base.BaseView;

/**
 * @date: 2018/5/31 14:00
 * @author: Chunjiang Mao
 * @classname: ResultStatusUtil
 * @describe: 返回结果处理的封装
 */

public class ResultStatusUtil {
    public static boolean resultStatus(BaseView mView, String code, String msg, boolean success) {
        if (success) {
            return true;
        }
        if (!success) {
            if ("超时或者未登录!".equals(msg)) {
                mView.tokenFail(msg);
            } else {
                mView.failedResult(msg);
            }
            return false;
        }
        return false;
    }
}
