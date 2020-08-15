package com.wya.env.base;

import android.app.Activity;
import android.content.Intent;

import com.wya.env.MainActivity;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.util.SaveSharedPreferences;

/**
 * @date: 2018/7/3 13:48
 * @author: Chunjiang Mao
 * @classname: BaseMvpFragment
 * @describe: BaseMvpFragment
 */

public abstract class BaseMvpFragment<T extends BasePresent> extends BaseLazyFragment implements BaseView {
    /**
     * 显示加载对话框
     */
    @Override
    public void showLoading() {
    
    }
    
    /**
     * 隐藏加载对话框
     */
    @Override
    public void hideLoading() {
    
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
        SaveSharedPreferences.save(getActivity(), CommonValue.IS_LOGIN, false);
        SaveSharedPreferences.save(getActivity(), CommonValue.TOKEN, "");
        SaveSharedPreferences.save(getActivity(), CommonValue.LOGIN_INFO, "");
        ActivityManager.getInstance().leaveFirstActivity();
        
    }
    
}
