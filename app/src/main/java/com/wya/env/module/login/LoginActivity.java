package com.wya.env.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wya.env.MainActivity;
import com.wya.env.R;
import com.wya.env.base.BaseMvpActivity;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.bean.doodle.UserInfo;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.module.forgetpassword.ForgetPasswordActivity;
import com.wya.env.module.register.RegisterActivity;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.uikit.button.WYAButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private  UserInfo userInfo;

    @Override
    protected void initView() {
        showToolBar(false);
        loginPresent.mView = this;
    }

    /**
     * 登录结果的返回
     *
     * @param loginInfo
     */
    @Override
    public void onLoginResult(LoginInfo loginInfo) {
        //保存数据
        saveInfo(loginInfo);
        //跳转到主界面
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

    private void saveInfo(LoginInfo loginInfo) {
        SaveSharedPreferences.save(LoginActivity.this, CommonValue.IS_LOGIN, true);

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
                String userName = email.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                boolean isRight = loginPresent.checkInfo(userName, pwd, this);
                if (isRight) {
                    //  loginPresent.login(userName, pwd);
                    userInfo = new Gson().fromJson(SaveSharedPreferences.getString(this, CommonValue.USER_INFO), UserInfo.class);
                    if(userInfo == null){
                        UserInfo userInfo = new UserInfo();
                        userInfo.setDoodlePatterns(new ArrayList<>());
                        userInfo.setEmail("dsad");
                        userInfo.setUserName("abc");
                        SaveSharedPreferences.save(this, CommonValue.USER_INFO, new Gson().toJson(userInfo));
                    }
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }


                break;
            case R.id.register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            default:
                break;
        }
    }
}
