package com.wya.env.net.api;

import com.wya.env.App;
import com.wya.env.bean.BaseResult;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.doodle.NetModel;
import com.wya.env.bean.doodle.SaveModel;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.Constance;
import com.wya.env.net.RetrofitFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @date: 2018/7/3 13:58
 * @author: Chunjiang Mao
 * @classname: ResultApi
 * @describe: 传参api
 */

public class ResultApi {

    private Map<String, String> getHeaderMap() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("x-client-token", App.TOKEN);
        return headerMap;
    }

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
    public Observable<BaseResult<LoginInfo>> registerApi(String userEmail, String pwd, String userName) {
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
    public Observable<BaseResult<Object>> changePasswordApi(String userEmail, String pwd, String code) {
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

    /**
     * 保存模板
     *
     * @param content 模板json
     * @return
     */
    public Observable<BaseResult<Object>> saveModelApi(String content) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("content", content);
        return RetrofitFactory.getInstance().create(Api.class).saveModel(getHeaderMap(), hashMap);
    }

    /**
     * 獲取模板
     *
     * @return
     */
    public Observable<BaseResult<List<SaveModel>>> getSaveModelsApi() {
        HashMap<String, String> hashMap = new HashMap<>();
        return RetrofitFactory.getInstance().create(Api.class).getSaveModels(getHeaderMap(), hashMap);
    }


    /**
     * 獲取模板
     *
     * @return
     */
    public Observable<BaseResult<List<NetModel>>> getModelsApi(HashMap<String, String> hashMap) {
        return RetrofitFactory.getInstance().create(Api.class).getModels(getHeaderMap(), hashMap);
    }

    /**
     * 獲取json
     *
     * @return
     */
    public Observable<LampModel> getJson(String json) {
        return RetrofitFactory.getInstance().create(Api.class).getJson(json);
    }
}
