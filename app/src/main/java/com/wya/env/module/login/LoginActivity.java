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

    int column = 15;
    int size = 300;

    @Override
    protected void initView() {
        showToolBar(false);
        loginPresent.mView = this;
        lampModels = getModels();

        email.setText("666666@qq.com");
        password.setText("666666");
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
        mLampModels.add(getFirstModel());
        mLampModels.add(getSecondModel());
        mLampModels.add(getThirdModel());
        mLampModels.add(getFourthModel());
        return mLampModels;
    }


    private LampModel getSecondModel() {
        LampModel lampModel = new LampModel();
        lampModel.setName("第二个模板");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < size / column; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if (j % (size / column) == (i + k) % (size / column) || j % (size / column) == (i + k + 1) % (size / column) || j % (size / column) == (i + k + 2) % (size / column) || j % (size / column) == (i + k + 3) % (size / column)) {
                        doodle.setColor("#ff0000");
                    } else if (j % (size / column) == (i + k + 8) % (size / column) || j % (size / column) == (i + k + 9) % (size / column) || j % (size / column) == (i + k + 10) % (size / column) || j % (size / column) == (i + k + 11) % (size / column)) {
                        doodle.setColor("#ffffff");
                    } else {
                        doodle.setColor("#000000");
                    }
                    doodle.setLight(255);
                    doodle.setFlash(0);
                    light_status.put(String.valueOf(i * size / column + j), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        return lampModel;
    }

    private LampModel getThirdModel() {
        LampModel lampModel = new LampModel();
        lampModel.setName("第三个模板");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int i = 0; i < size / column; i++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int j = 0; j < size; j++) {
                Doodle doodle = new Doodle();
                if (j % (size / column) == i % (size / column) || j % (size / column) == (i + 1) % (size / column) || j % (size / column) == (i + 2) % (size / column)) {
                    doodle.setColor("#ff0000");
                } else if (j % (size / column) == (i + 10) % (size / column) || j % (size / column) == (i + 11) % (size / column) || j % (size / column) == (i + 12) % (size / column)) {
                    doodle.setColor("#00ffff");
                } else if (j % (size / column) == (i + 5) % (size / column) || j % (size / column) == (i + 6) % (size / column) || j % (size / column) == (i + 7) % (size / column)) {
                    doodle.setColor("#0000ff");
                } else {
                    doodle.setColor("#000000");
                }
                doodle.setLight(255);
                doodle.setFlash(0);
                light_status.put(String.valueOf(j), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        return lampModel;
    }


    private LampModel getFirstModel() {
        LampModel lampModel = new LampModel();
        lampModel.setName("第一个模板");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < size / column; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if (j % (size / column) == (i + k) % (size / column)) {
                        doodle.setColor("#ff0000");
                        doodle.setLight(255);
                    } else if (j % (size / column) == (i + k + 1) % (size / column)) {
                        doodle.setColor("#ff0000");
                        doodle.setLight(255 - 50);
                    } else if (j % (size / column) == (i + k + 2) % (size / column)) {
                        doodle.setColor("#ff0000");
                        doodle.setLight(255 - 2 * 50);
                    } else if (j % (size / column) == (i + k + 3) % (size / column)) {
                        doodle.setColor("#ff0000");
                        doodle.setLight(255 - 3 * 50);
                    } else if (j % (size / column) == (i + k + 4) % (size / column)) {
                        doodle.setColor("#ff0000");
                        doodle.setLight(255 - 4 * 50);
                    } else {
                        doodle.setColor("#000000");
                    }
                    doodle.setLight(255);
                    doodle.setFlash(0);
                    light_status.put(String.valueOf(i * size / column + j), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        return lampModel;
    }


    private LampModel getFourthModel() {
        LampModel lampModel = new LampModel();
        lampModel.setName("第四个模板");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int i = 0; i < size / column; i++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int j = 0; j < size; j++) {
                Doodle doodle = new Doodle();
                if (j % (size / column) == (i) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255);
                } else if (j % (size / column) == (i + 1) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 20);
                } else if (j % (size / column) == (i + 2) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 2 * 20);
                } else if (j % (size / column) == (i + 3) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 3 * 20);
                } else if (j % (size / column) == (i + 4) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 4 * 20);
                } else if (j % (size / column) == (i + 5) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 5 * 20);
                } else if (j % (size / column) == (i + 6) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 6 * 20);
                } else if (j % (size / column) == (i + 7) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 7 * 20);
                } else if (j % (size / column) == (i + 8) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 8 * 20);
                } else if (j % (size / column) == (i + 9) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 9 * 20);
                } else if (j % (size / column) == (i + 10) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 10 * 20);
                } else if (j % (size / column) == (i + 11) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 11 * 20);
                } else if (j % (size / column) == (i + 12) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 12 * 20);
                } else if (j % (size / column) == (i + 13) % (size / column)) {
                    doodle.setColor("#ff0000");
                    doodle.setLight(255 - 13 * 20);
                } else {
                    doodle.setColor("#000000");
                    doodle.setLight(255);
                }
                light_status.put(String.valueOf(j), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        return lampModel;
    }

}
