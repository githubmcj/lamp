package com.wya.env.module.register;

import android.app.Activity;
import android.widget.Toast;

import com.wya.env.base.BasePresent;
import com.wya.env.bean.BaseResult;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.net.BaseExt;
import com.wya.env.net.BaseSubscriber;
import com.wya.env.net.api.ResultApi;
import com.wya.env.util.ResultStatusUtil;

/**
 * @date: 2020/8/14 17:29
 * @author: Chunjiang Mao
 * @classname: RegisterPresent
 * @describe: 注册
 */
public class RegisterPresent extends BasePresent<RegisterView> {

    private ResultApi resultApi = new ResultApi();

    /**
     * 注册的方法
     *
     * @param userName
     * @param pwd
     */
    public void register(String userEmail, String pwd, String userName) {
        //业务逻辑的处理
        mView.showLoading();
        BaseExt.ext(resultApi.registerApi(userEmail, pwd, userName), new BaseSubscriber<BaseResult<LoginInfo>>(mView) {
            @Override
            public void onNext(BaseResult<LoginInfo> loginInfoBaseResult) {
                if (ResultStatusUtil.resultStatus(mView, loginInfoBaseResult.code, loginInfoBaseResult.msg, loginInfoBaseResult.success)) {
                    Toast.makeText((Activity) mView, loginInfoBaseResult.msg, Toast.LENGTH_SHORT).show();
                    mView.onRegisterResult(loginInfoBaseResult.data);
                }
            }
        });
    }

}
