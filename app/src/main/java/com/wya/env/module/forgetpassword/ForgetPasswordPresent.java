package com.wya.env.module.forgetpassword;

import android.app.Activity;
import android.widget.Toast;

import com.wya.env.base.BasePresent;
import com.wya.env.bean.BaseResult;
import com.wya.env.net.BaseExt;
import com.wya.env.net.BaseSubscriber;
import com.wya.env.net.api.ResultApi;
import com.wya.env.util.ResultStatusUtil;

/**
 * @date: 2020/8/1 14:20
 * @author: Chunjiang Mao
 * @classname: ForgetPasswordPresent
 * @describe: 忘记密码
 */
public class ForgetPasswordPresent extends BasePresent<ForgetPasswordView> {
    private ResultApi resultApi = new ResultApi();

    /**
     * 修改密码的方法
     *
     * @param userEmail
     * @param pwd
     * @param code
     */
    public void changePassword(String userEmail, String pwd, String code) {
        mView.showLoading();
        BaseExt.ext(resultApi.changePasswordApi(userEmail, pwd, code), new BaseSubscriber<BaseResult<Object>>(mView) {
            @Override
            public void onNext(BaseResult<Object> loginInfoBaseResult) {
                if (ResultStatusUtil.resultStatus(mView, loginInfoBaseResult.code, loginInfoBaseResult.msg, loginInfoBaseResult.success)) {
                    Toast.makeText((Activity) mView, loginInfoBaseResult.msg, Toast.LENGTH_SHORT).show();
                    mView.onRegisterResult();
                }
            }
        });
    }

    /**
     * @param userEmail
     */
    public void getCode(String userEmail) {
        mView.showLoading();
        BaseExt.ext(resultApi.getCodeApi(userEmail), new BaseSubscriber<BaseResult<Object>>(mView) {
            @Override
            public void onNext(BaseResult<Object> loginInfoBaseResult) {
                if (ResultStatusUtil.resultStatus(mView, loginInfoBaseResult.code, loginInfoBaseResult.msg, loginInfoBaseResult.success)) {
                    Toast.makeText((Activity) mView, loginInfoBaseResult.msg, Toast.LENGTH_SHORT).show();
                    mView.onCodeResult();
                }
            }
        });
    }
}
