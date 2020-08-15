package com.wya.env.base;

import android.content.Intent;

import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.module.login.LoginActivity;
import com.wya.env.util.SaveSharedPreferences;

/**
 * @date: 2018/7/3 13:48
 * @author: Chunjiang Mao
 * @classname: BaseMvpActivity
 * @describe: BaseMvpActivity
 */

public abstract class BaseMvpActivity<T extends BasePresent> extends BaseActivity implements BaseView {

    /**
     * 显示加载对话框
     */
    @Override
    public void showLoading() {
        loadingDialog.show();
    }

    /**
     * 隐藏加载对话框
     */
    @Override
    public void hideLoading() {
        loadingDialog.dismiss();
    }

    /**
     * 失败回调
     *
     * @param s
     */
    @Override
    public void failedResult(String s) {
        showShort(s);
    }

    /**
     * token失效
     */
    @Override
    public void tokenFail(String msg) {
        showShort(msg);
        SaveSharedPreferences.save(this, CommonValue.IS_LOGIN, false);
        SaveSharedPreferences.save(this, CommonValue.TOKEN, "");
        SaveSharedPreferences.save(this, CommonValue.LOGIN_INFO, "");
        if (!ActivityManager.getInstance().leaveFirstActivity()){
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
