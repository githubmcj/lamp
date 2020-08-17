package com.wya.env.module.login;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wya.env.App;
import com.wya.env.MainActivity;
import com.wya.env.R;
import com.wya.env.base.BaseMvpActivity;
import com.wya.env.bean.doodle.Doodle;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.module.forgetpassword.ForgetPasswordActivity;
import com.wya.env.module.register.RegisterActivity;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.uikit.button.WYAButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @date: 2019/1/3 13:57
 * @author: Chunjiang Mao
 * @classname: LoginActivity
 * @describe: 登录
 */

public class LoginActivity extends BaseMvpActivity<LoginPresent> implements LoginView {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.but_login)
    WYAButton butLogin;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.register)
    TextView register;
    private LoginPresent loginPresent = new LoginPresent();

    private LoginInfo loginInfo;
    /**
     * 灯光模板
     */
    private List<LampModel> lampModels;

    @Override
    protected void initView() {
        showToolBar(false);
        loginPresent.mView = this;
        lampModels = getModels();

        email.setText("222222@qq.com");
        password.setText("222222");
    }

    /**
     * 登录结果的返回
     *
     * @param loginInfo
     */
    @Override
    public void onLoginResult(LoginInfo loginInfo) {
        // 保存数据
        saveInfo(loginInfo);
        // 跳转到主界面
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void saveInfo(LoginInfo loginInfo) {
        loginInfo.setLampModels(lampModels);
        SaveSharedPreferences.save(LoginActivity.this, CommonValue.IS_LOGIN, true);
        App.TOKEN = loginInfo.getToken();
        SaveSharedPreferences.save(LoginActivity.this, CommonValue.TOKEN, loginInfo.getToken());
        SaveSharedPreferences.save(this, CommonValue.LOGIN_INFO, new Gson().toJson(loginInfo));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.login_activity;
    }

    @OnClick({R.id.tv_forget_password, R.id.register, R.id.but_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_forget_password:
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                break;
            case R.id.but_login:
                String userEmail = email.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                boolean isRight = loginPresent.checkInfo(userEmail, pwd, this);
                if (isRight) {
                    loginPresent.login(userEmail, pwd);
                }
                break;
            case R.id.register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            default:
                break;
        }
    }

    private List<LampModel> getModels() {
        List<LampModel> mLampModels = new ArrayList<>();
        LampModel lampModel = new LampModel();
        lampModel.setName("第一个模板");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int j = 0; j < 300; j++) {
                Doodle doodle = new Doodle();
                if (j % 15 == i) {
                    doodle.setColor("#ffffff");
                    doodle.setLight(255);
                } else if (j % 15 == i + 1) {
                    doodle.setColor("#ffffff");
                    doodle.setLight(255 - 30);
                } else if (j % 15 == i + 2) {
                    doodle.setColor("#ffffff");
                    doodle.setLight(255 - 60);
                } else if (j % 15 == i + 3) {
                    doodle.setColor("#ffffff");
                    doodle.setLight(255 - 90);
                } else if (j % 15 == i + 4) {
                    doodle.setColor("#ffffff");
                    doodle.setLight(255 - 120);
                } else if (j % 15 == i + 5) {
                    doodle.setColor("#ffffff");
                    doodle.setLight(255 - 150);
                } else if (j % 15 == i + 6) {
                    doodle.setColor("#ffffff");
                    doodle.setLight(255 - 180);
                } else if (j % 15 == i + 7) {
                    doodle.setColor("#ffffff");
                    doodle.setLight(255 - 210);
                } else if (j % 15 == i + 8) {
                    doodle.setColor("#ffffff");
                    doodle.setLight(255 - 240);
                } else {
                    doodle.setColor("#000000");
                    doodle.setLight(255);
                }
                doodle.setFlash(0);
                light_status.put(String.valueOf(j), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(300);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        mLampModels.add(lampModel);
        return mLampModels;
    }

}
