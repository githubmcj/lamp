package com.wya.env.net.api;

import com.wya.env.bean.BaseResult;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.net.RetrofitFactory;

import java.util.HashMap;

import io.reactivex.Observable;

/**
 * @date: 2018/7/3 13:58
 * @author: Chunjiang Mao
 * @classname: ResultApi
 * @describe: 传参api
 */

public class ResultApi {
    /**
     * 登录
     *
     * @param userName
     * @param pwd
     * @return
     */
    public Observable<BaseResult<LoginInfo>> loginApi(String userName, String pwd) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userEmail", userName);
        hashMap.put("password", pwd);
        return RetrofitFactory.getInstance().create(Api.class).login(hashMap);
    }

    /**
     * 注册
     *
     * @param userEmail
     * @param pwd
     * @param userName
     * @return
     */
    public Observable<BaseResult<Object>> registerApi(String userEmail, String pwd,String userName) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userEmail", userEmail);
        hashMap.put("password", pwd);
        hashMap.put("userName", userName);
        return RetrofitFactory.getInstance().create(Api.class).register(hashMap);
    }

    /**
     * 修改密码
     *
     * @param userEmail
     * @param pwd
     * @param code
     * @return
     */
    public Observable<BaseResult<Object>> changePasswordApi(String userEmail, String pwd,String code) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userEmail", userEmail);
        hashMap.put("password", pwd);
        hashMap.put("code", code);
        return RetrofitFactory.getInstance().create(Api.class).changePassword(hashMap);
    }

    /**
     * 发送验证码
     *
     * @param userEmail
     * @return
     */
    public Observable<BaseResult<Object>> getCodeApi(String userEmail) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userEmail", userEmail);
        return RetrofitFactory.getInstance().create(Api.class).getCode(hashMap);
    }
}
