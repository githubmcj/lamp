package com.wya.env.module.login;

import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.wya.env.MainActivity;
import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.bean.login.Lamps;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.module.login.start.Start1Activity;
import com.wya.env.module.register.RegisterActivity;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.uikit.button.WYAButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @date: 2018/4/8 13:58
 * @author: Chunjiang Mao
 * @classname: StartUpActivity
 * @describe: 启动页
 */

public class StartUpActivity extends BaseActivity {

    @BindView(R.id.but_login)
    WYAButton butLogin;
    @BindView(R.id.but_sign)
    WYAButton butSign;

    private Lamps lamps;

    @Override
    protected void initView() {
        showToolBar(false);
        setBackgroundColor(R.color.white, true);
        //是否登录
        boolean isLogin = SaveSharedPreferences.getBoolean(this, CommonValue.IS_LOGIN);
        if (isLogin) {
            // 保存数据
            lamps = new Gson().fromJson(SaveSharedPreferences.getString(this, CommonValue.LAMPS), Lamps.class);
            if(lamps != null && lamps.getLampSettings() != null && lamps.getLampSettings().size() > 0){
                if(lamps.getLampSettings().size() == 1 && lamps.getLampSettings().get(0).getName() == null){
                    startActivity(new Intent(StartUpActivity.this, Start1Activity.class));
                    finish();
                } else {
                    startActivity(new Intent(StartUpActivity.this, MainActivity.class));
                    ActivityManager.getInstance().exitApp();
                }
            } else {
                startActivity(new Intent(StartUpActivity.this, Start1Activity.class));
                finish();
            }
//            startActivity(new Intent(StartUpActivity.this, MainActivity.class));
//            ActivityManager.getInstance().exitApp();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.start_up_activity;
    }

    @OnClick({R.id.but_login, R.id.but_sign})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.but_login:
                startActivity(new Intent(StartUpActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.but_sign:
                startActivity(new Intent(StartUpActivity.this, RegisterActivity.class));
                finish();
                break;
            default:
                break;
        }
    }
}
