package com.wya.env.net.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

import com.wya.env.bean.BaseResult;
import com.wya.env.bean.doodle.SaveModel;
import com.wya.env.bean.login.LoginInfo;

import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * @date: 2018/7/3 13:44
 * @author: Chunjiang Mao
 * @classname: Api
 * @describe: 请求数据的接口
 */

public interface Api {
    /**
     * 登录
     *
     * @param hashMap
     * @return
     */
    @POST("loanFlow/user/login")
    Observable<BaseResult<LoginInfo>> login(@Body HashMap<String, String> hashMap);

    /**
     * 注册
     *
     * @param hashMap
     * @return
     */
    @POST("loanFlow/user/regist")
    Observable<BaseResult<LoginInfo>> register(@Body HashMap<String, String> hashMap);

    /**
     * 修改密码
     *
     * @param hashMap
     * @return
     */
    @POST("loanFlow/user/changePassword")
    Observable<BaseResult<Object>> changePassword(@Body HashMap<String, String> hashMap);

    /**
     * 获取验证码
     *
     * @param hashMap
     * @return
     */
    @POST("loanFlow/user/sendEmail")
    Observable<BaseResult<Object>> getCode(@Body HashMap<String, String> hashMap);

    /**
     * 保存模板
     *
     * @param headerMap
     * @param hashMap
     * @return
     */
    @POST("loanFlow/template/save")
    Observable<BaseResult<Object>> saveModel(@HeaderMap Map<String, String> headerMap, @Body HashMap<String, String> hashMap);

    /**
     * 获取模板
     *
     * @param headerMap
     * @param hashMap
     * @return
     */
    @POST("loanFlow/template/getList")
    Observable<BaseResult<List<SaveModel>>> getSaveModels(@HeaderMap Map<String, String> headerMap, @Body HashMap<String, String> hashMap);
}
