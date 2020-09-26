package com.wya.env.module.register;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    @BindView(R.id.img_read2)
    ImageView imgRead2;

    private boolean isRead;
    private boolean isRead2;
    private RegisterPresent registerPresent;
    private Lamps lamps;

    /**
     * 灯光模板
     */
    private List<LampModel> lampModels;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void initView() {
        setTitle(getResources().getString(R.string.register));
        setLeftText("Back");
        isRead = true;
        isRead2 = true;
        registerPresent = new RegisterPresent();
        registerPresent.mView = this;
        lampModels = getModels();
        setRead(isRead);
        setRead2(isRead2);
        //设置Hello World前三个字符有点击事件
        SpannableStringBuilder textSpanned = new SpannableStringBuilder("I declare to have read,understood and agreed the Terms&Conditions of use of this app and the Privacy Police");
        textSpanned.setSpan(new ForegroundColorSpan(Color.RED),
                48, 65, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textSpanned.setSpan(new ForegroundColorSpan(Color.RED),
                92, 107, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "Hello World", Toast.LENGTH_SHORT).show();
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "Hello World2", Toast.LENGTH_SHORT).show();
            }
        };
        textSpanned.setSpan(clickableSpan,
                48, 65, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textSpanned.setSpan(clickableSpan2,
                92, 107, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //注意：此时必须加这一句，不然点击事件不会生效
        tvRegisterProtocol.setMovementMethod(LinkMovementMethod.getInstance());
        tvRegisterProtocol.setText(textSpanned);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setRead(boolean isRead) {
        if (isRead) {
            imgRead.setBackground(getResources().getDrawable(R.drawable.xuanzekuangxuanze));
        } else {
            imgRead.setBackground(getResources().getDrawable(R.drawable.xuanzekuangmoren));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setRead2(boolean setRead2) {
        if (setRead2) {
            imgRead2.setBackground(getResources().getDrawable(R.drawable.xuanzekuangxuanze));
        } else {
            imgRead2.setBackground(getResources().getDrawable(R.drawable.xuanzekuangmoren));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnClick({R.id.img_read, R.id.img_read2, R.id.but_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_read:
                isRead = !isRead;
                setRead(isRead);
                break;
            case R.id.img_read2:
                isRead2 = !isRead2;
                setRead2(isRead2);
                break;
//            case R.id.tv_register_protocol:
//                startActivity(new Intent(RegisterActivity.this, RegisterProtocolActivity.class));
//                break;
            case R.id.but_login:
                if (TextUtils.isEmpty(userName.getText().toString())) {
                    showShort("please enter Full Name");
                    return;
                }
                if (TextUtils.isEmpty(email.getText().toString())) {
                    showShort("please enter email");
                    return;
                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    showShort("please enter password");
                    return;
                }
                if(password.getText().toString().length() < 8){
                    showShort("Password(min 8 chars)");
                    return;
                }
                if (TextUtils.isEmpty(surePassword.getText().toString())) {
                    showShort("confirm password");
                    return;
                }



                if (!password.getText().toString().equals(surePassword.getText().toString())) {
                    showShort("password is different");
                    return;
                }
                if (!isRead || !isRead2) {
                    showShort("please make sure read protocol");
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
        if (lamps != null && lamps.getLampSettings() != null && lamps.getLampSettings().size() > 0) {
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

    // TODO 行列修改
    int column = 15;
    int size = 300;
    int row = 20;

    private List<LampModel> getModels() {
        List<LampModel> mLampModels = new ArrayList<>();
        mLampModels.add(getModel1());
        mLampModels.add(getModel2());
        mLampModels.add(getModel3());
        mLampModels.add(getModel4());
        mLampModels.add(getModel5());
        mLampModels.add(getModel6());
        mLampModels.add(getModel7());
        mLampModels.add(getModel8());
        mLampModels.add(getModel9());
        mLampModels.add(getModel10());
        return mLampModels;
    }

    private LampModel getModel10() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Bright Delightlux");
        List<DoodlePattern> modeArr = new ArrayList<>();


        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();

                int w = (int) (Math.random() * 10);
                if (w == 6) {
                    doodle.setColor("#FF00FF");
                } else if (w == 3) {
                    doodle.setColor("#FFFFFF");
                } else {
                    doodle.setColor("#000000");
                }

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel9() {
        String[] colorHexArr = {"#FF0000", "#00FF00", "#FFFFFF", "#000000", "#007FFF", "#0000FF", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Glow");
        List<DoodlePattern> modeArr = new ArrayList<>();

        for (int k = 0; k < 2; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i % row - 0 + row + 1) / 1 % 4]);

                doodle.setFlash(0);
                int x = (int) (Math.random() * 2);
                if (x == 1) {
                    doodle.setColor("#000000");
                }
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel8() {

        String[] colorHexArr = {"#FA0000", "#FAA500", "#FAFF00", "#00FF00", "#007FFF", "#0000FF", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Vertical");
        List<DoodlePattern> modeArr = new ArrayList<>();

        for (int k = 0; k < column; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i / row - k + column + 1) / 3 % 7]);

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;

    }

    private LampModel getModel7() {
        String[] colors = {"#FA0000", "#FAA500", "#00FF00"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Sunset");
        List<DoodlePattern> modeArr = new ArrayList<>();

        int alpha = 14;
        int beta = 7;
        int gama = 0;
        for (int i = 0; i < 21; i++) {
            double a = tan((alpha + i) % 21 * Math.PI / 42);
            double b = tan((beta + i) % 21 * Math.PI / 42);
            double c = tan((gama + i) % 21 * Math.PI / 42);

            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int j = 0; j < size; j++) {
                Doodle doodle = new Doodle();
                doodle.setColor("#000000");

                doodle.setFlash(0);
                light_status.put(String.valueOf(j), doodle);

                double l = j;

                if (a > b && b > c) {
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= a) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < a && (double) (row - 1 - j % row) / (double) (j / row + 1) >= b) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);

                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < b && (double) (row - 1 - j % row) / (double) (j / row + 1) >= c) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < c) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);
                    }
                }

                if (a < c && b > c) {
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= b) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(1);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < b && (double) (row - 1 - j % row) / (double) (j / row + 1) >= c) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= a && (double) (row - 1 - j % row) / (double) (j / row + 1) < c) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < a) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                }

                if (a > b && b < c) {
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= c) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < c && (double) (row - 1 - j % row) / (double) (j / row + 1) >= a) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= b && (double) (row - 1 - j % row) / (double) (j / row + 1) < a) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < b) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                }

            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);

        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }


    private LampModel getModel6() {
        String[] colorHexArr = {"#FA0000", "#FAA500", "#000000", "#00FF00", "#007FFF", "#000000", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Updown");
        List<DoodlePattern> modeArr = new ArrayList<>();

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i % row - k + row + 1) / 3 % 7]);

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel5() {
        String[] colorHexArr = {"#FA0000", "#FAA500", "#FAFF00", "#00FF00", "#007FFF", "#0000FF", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Horizontal Flag");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i % row - k + row + 1) / 3 % 7]);

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }


    private LampModel getModel4() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Sparkles");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#F99601");
                    } else {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    doodle.setColor("#000000");
                    if (k != 0) {
                        int x = (int) (Math.random() * 2);
                        if (x == 1) {
                            doodle.setColor("#000000");
                        }
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel1() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Diagonal");
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

                    doodle.setFlash(0);
                    light_status.put(String.valueOf(i * size / column + j), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel2() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Fireworks");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#ff0000");
                    } else {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);


                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#00ff00");
                    } else {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);

        }
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#0000ff");
                    } else {
                        doodle.setColor("#000000");
                    }


                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#ffffff");
                    } else {
                        doodle.setColor("#000000");
                    }


                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }


    private LampModel getModel3() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Waves");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#ff0000");
                    } else {
                        doodle.setColor("#000000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#000000");
                    } else {
                        doodle.setColor("#ff0000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#00ff00");
                    } else {
                        doodle.setColor("#000000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#000000");
                    } else {
                        doodle.setColor("#00ff00");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#0000ff");
                    } else {
                        doodle.setColor("#000000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#000000");
                    } else {
                        doodle.setColor("#0000ff");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }
}