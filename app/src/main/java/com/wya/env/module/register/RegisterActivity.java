package com.wya.env.module.register;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wya.env.App;
import com.wya.env.MainActivity;
import com.wya.env.R;
import com.wya.env.base.BaseMvpActivity;
import com.wya.env.bean.doodle.Doodle;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.module.login.LoginActivity;
import com.wya.env.module.login.start.Start1Activity;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.uikit.button.WYAButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static java.lang.Math.tan;

/**
 * @date: 2020/8/1 15:54
 * @author: Chunjiang Mao
 * @classname: RegisterActivity
 * @describe: 注册用户
 */
public class RegisterActivity extends BaseMvpActivity<RegisterPresent> implements RegisterView {

    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.sure_password)
    EditText surePassword;
    @BindView(R.id.img_read)
    ImageView imgRead;
    @BindView(R.id.tv_register_protocol)
    TextView tvRegisterProtocol;
    @BindView(R.id.but_login)
    WYAButton butLogin;

    private boolean isRead;
    private RegisterPresent registerPresent;
    private Lamps lamps;

    /**
     * 灯光模板
     */
    private List<LampModel> lampModels;

    @Override
    protected void initView() {
        setTitle(getResources().getString(R.string.register));
        isRead = false;
        registerPresent = new RegisterPresent();
        registerPresent.mView = this;
        lampModels = getModels();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnClick({R.id.img_read, R.id.tv_register_protocol, R.id.but_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_read:
                isRead = !isRead;
                if (isRead) {
                    imgRead.setBackground(getResources().getDrawable(R.drawable.xuanzekuangxuanze));
                } else {
                    imgRead.setBackground(getResources().getDrawable(R.drawable.xuanzekuangmoren));
                }
                break;
            case R.id.tv_register_protocol:
                startActivity(new Intent(RegisterActivity.this, RegisterProtocolActivity.class));
                break;
            case R.id.but_login:
                if (TextUtils.isEmpty(userName.getText().toString())) {
                    showShort("请输入用户名");
                    return;
                }
                if (TextUtils.isEmpty(email.getText().toString())) {
                    showShort("请输入邮箱");
                    return;
                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    showShort("请输入密码");
                    return;
                }
                if (TextUtils.isEmpty(surePassword.getText().toString())) {
                    showShort("请再次输入密码");
                    return;
                }
                if (!password.getText().toString().equals(surePassword.getText().toString())) {
                    showShort("两次密码不一致");
                    return;
                }
                if (!isRead) {
                    showShort("请确认已查看注册协议");
                    return;
                }
                registerPresent.register(email.getText().toString(), password.getText().toString(), userName.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onRegisterResult(LoginInfo loginInfo) {
        // 保存数据
        saveInfo(loginInfo);
        lamps = new Gson().fromJson(SaveSharedPreferences.getString(this, CommonValue.LAMPS), Lamps.class);
        if(lamps != null &&  lamps.getLampSettings() != null && lamps.getLampSettings().size() > 0){
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            ActivityManager.getInstance().exitApp();
        } else {
            startActivity(new Intent(RegisterActivity.this, Start1Activity.class));
            this.finish();
        }
    }

    private void saveInfo(LoginInfo loginInfo) {
        loginInfo.setLampModels(lampModels);
        SaveSharedPreferences.save(RegisterActivity.this, CommonValue.IS_LOGIN, true);
        App.TOKEN = loginInfo.getToken();
        SaveSharedPreferences.save(RegisterActivity.this, CommonValue.TOKEN, loginInfo.getToken());
        SaveSharedPreferences.save(this, CommonValue.LOGIN_INFO, new Gson().toJson(loginInfo));
    }

    int column = 20;
    int size = 300;

    private List<LampModel> getModels() {
        List<LampModel> mLampModels = new ArrayList<>();
        mLampModels.add(getFirstModel());
        mLampModels.add(getSecondModel());
        mLampModels.add(getThirdModel());
        mLampModels.add(getFourthModel());
//        mLampModels.add(getFifthModel());

        return mLampModels;
    }

    int alpha = 14;
    int beta = 7;
    int gama = 0;
    private String[] snow_colors = {"#ffffff", "#B04F9C", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000"};
    private String[] fifth_colors = {"#FA0000", "#FAA500", "#00FF00"};

    private LampModel getFifthModel() {
        LampModel lampModel = new LampModel();
        lampModel.setName("第5个模板");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            double a = tan((alpha + i) % 21 * Math.PI / 42);
            double b = tan((beta + i) % 21 * Math.PI / 42);
            double c = tan((gama + i) % 21 * Math.PI / 42);
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int j = 0; j < size; j++) {
                Doodle doodle = new Doodle();
                if (a > b && b > c){
                    if((double)(19-j%20)/(double)(j/20+1)>=a){
                        doodle.setColor(fifth_colors[0]);
                    }
                    if((double)(19-j%20)/(double)(j/20+1)<a&&(double)(19-j%20)/(double)(j/20+1)>=b){
                        doodle.setColor(fifth_colors[1]);
                    }
                    if((double)(19-j%20)/(double)(j/20+1)<b&&(double)(19-j%20)/(double)(j/20+1)>=c){
                        doodle.setColor(fifth_colors[2]);
                    }
                    if((double)(19-j%20)/(double)(j/20+1)<c){
                        doodle.setColor(fifth_colors[0]);
                    }
                }

                if (a < c && b > c){
                    if((double)(19-j%20)/(double)(j/20+1)>=b){
                        doodle.setColor(fifth_colors[1]);
                    }
                    if((double)(19-j%20)/(double)(j/20+1)<b&&(double)(19-j%20)/(double)(j/20+1)>=c){
                        doodle.setColor(fifth_colors[2]);
                    }
                    if((double)(19-j%20)/(double)(j/20+1)>=a&&(double)(19-j%20)/(double)(j/20+1)<c){
                        doodle.setColor(fifth_colors[0]);
                    }
                    if((double)(19-j%20)/(double)(j/20+1)<a){
                        doodle.setColor(fifth_colors[1]);
                    }
                }

                if (a > b && b < c){
                    if((double)(19-j%20)/(double)(j/20+1)>=c){
                        doodle.setColor(fifth_colors[2]);
                    }
                    if((double)(19-j%20)/(double)(j/20+1)<c&&(double)(19-j%20)/(double)(j/20+1)>=a){
                        doodle.setColor(fifth_colors[0]);
                    }
                    if((double)(19-j%20)/(double)(j/20+1)>=b&&(double)(19-j%20)/(double)(j/20+1)<a){
                        doodle.setColor(fifth_colors[1]);
                    }
                    if((double)(19-j%20)/(double)(j/20+1)<b){
                        doodle.setColor(fifth_colors[2]);
                    }
                }
                doodle.setLight(255);
                doodle.setFlash(0);
                light_status.put(String.valueOf( j), doodle);
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
        lampModel.setName("第4个模板");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < column; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if (i == k % column || i == (k + 1) % column || i == (k + 2) % column) {
                        doodle.setColor("#F2E93F");
                    } else if (i == (k + 3) % column || i == (k + 4) % column || i == (k + 5) % column) {
                        doodle.setColor("#EA1318");
                    } else if (i == (k + 6) % column || i == (k + 7) % column || i == (k + 8) % column) {
                        doodle.setColor("#F69218");
                    } else if (i == (k + 9) % column || i == (k + 10) % column || i == (k + 11) % column) {
                        doodle.setColor("#6BBA2B");
                    } else if (i == (k + 12) % column || i == (k + 13) % column || i == (k + 14) % column) {
                        doodle.setColor("#1A489E");
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
        lampModel.setName("第3个模板");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int j = 0; j < size; j++) {
                Doodle doodle = new Doodle();
                doodle.setColor(snow_colors[(int) (Math.random() * (snow_colors.length - 1))]);
                light_status.put(String.valueOf(j), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
            modeArr.add(doodlePattern);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        return lampModel;
    }

    private LampModel getFirstModel() {
        LampModel lampModel = new LampModel();
        lampModel.setName("第1个模板");
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
                        doodle.setColor("#F2E93F");
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

    private LampModel getSecondModel() {
        LampModel lampModel = new LampModel();
        lampModel.setName("第2个模板");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int i = 0; i < size / column; i++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int j = 0; j < size; j++) {
                Doodle doodle = new Doodle();
                if (j % (size / column) == (i + 3) % (size / column) || j % (size / column) == i % (size / column) || j % (size / column) == (i + 1) % (size / column) || j % (size / column) == (i + 2) % (size / column)) {
                    doodle.setColor("#F2E93F");
                } else if (j % (size / column) == (i + 4) % (size / column) || j % (size / column) == (i + 5) % (size / column) || j % (size / column) == (i + 6) % (size / column) || j % (size / column) == (i + 7) % (size / column)) {
                    doodle.setColor("#EA1318");
                } else if (j % (size / column) == (i + 8) % (size / column) || j % (size / column) == (i + 9) % (size / column) || j % (size / column) == (i + 10) % (size / column) || j % (size / column) == (i + 11) % (size / column)) {
                    doodle.setColor("#F69218");
                } else if (j % (size / column) == (i + 13) % (size / column) || j % (size / column) == (i + 14) % (size / column) || j % (size / column) == (i + 12) % (size / column) || j % (size / column) == (i + 15) % (size / column)) {
                    doodle.setColor("#6BBA2B");
                } else {
                    doodle.setColor("#1A489E");
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


}