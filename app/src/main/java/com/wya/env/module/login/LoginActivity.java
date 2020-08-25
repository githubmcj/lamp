package com.wya.env.module.login;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
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
import com.wya.utils.utils.LogUtil;

import org.json.JSONObject;

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

    private GoogleSignInClient mGoogleSignInClient;

    private int RC_SIGN_IN = 9001;

    private FaceBookLogin faceBookLogin = null;

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
        initGoogle();
        initFacebook();

//        email.setText("222222@qq.com");
//        password.setText("222222");
//        loginPresent.login("222222@qq.com", "222222");
    }

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }


    private void initFacebook() {
        faceBookLogin = new FaceBookLogin(this);

        faceBookLogin.setFacebookListener(new FaceBookLogin.FacebookListener() {
            @Override
            public void facebookLoginSuccess(JSONObject object) {
                showShort("facebook_account_oauth_Success !");
            }

            @Override
            public void facebookLoginFail(String message) {
                showShort("facebook_account_oauth_Fail !" + message);
            }

            @Override
            public void facebookLoginCancel() {
                showShort("facebook_account_oauth_Cancel !");
            }
        });
    }

    private void initGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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

    @OnClick({R.id.tv_forget_password, R.id.register, R.id.but_login, R.id.img_google, R.id.img_facebook})
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
            case R.id.img_google:
                signGoogleIn();
                break;
            case R.id.img_facebook:
                signFaceBookIn();
                break;
            default:
                break;
        }
    }

    /**
     * facebook 登陆
     */
    private void signFaceBookIn() {
        faceBookLogin.login();
    }

    /**
     * google 登陆
     */
    private void signGoogleIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            faceBookLogin.getCallbackManager().onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            LogUtil.e(new Gson().toJson(account));
            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            showShort("Google sign in fail");
        }
    }


    private String color_num = "#ff0000";
    private String color_jian = "#ff0000";
    private String color_gong = "#ffffff";
    private String color_fly = "#ff0000";
    private String color_us = "#ffffff";
    private String color_flash = "#ffffff";
    private String color_love_small = "#6BBA2B";
    private String color_love_milld = "#F2E93F";
    private String color_love_big = "#EA1318";
    private String color_qi = "#F2E93F";
    private String color_xi = "#F2E93F";
    private String color_kuai = "#F2E93F";
    private String color_le = "#F2E93F";
    private String color_wu = "#ffffff";
    private String color_hui = "#ffffff";
    private String color_jiao = "#ffffff";

    private List<LampModel> getModels() {
        List<LampModel> mLampModels = new ArrayList<>();
        mLampModels.add(getMaoLoveJiao());

//        mLampModels.add(getFirstModel());
        mLampModels.add(getSecondModel());
//        mLampModels.add(getThirdModel());
//        mLampModels.add(getFourthModel());
        return mLampModels;
    }

    private LampModel getMaoLoveJiao() {
        LampModel lampModel = new LampModel();
        lampModel.setName("毛爱姣");
        List<DoodlePattern> modeArr = new ArrayList<>();

        // 321
        for (int i = 0; i < 5; i++) {
            modeArr.add(getThree());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getBlack());
        }
        for (int i = 0; i < 5; i++) {
            modeArr.add(getTwo());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getBlack());
        }
        for (int i = 0; i < 5; i++) {
            modeArr.add(getOne());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getBlack());
        }

        // 搭箭
        for (int i = 0; i < 5; i++) {
            modeArr.add(getDaJian());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getLagong());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getLagong2());
        }
        for (int i = 0; i < 5; i++) {
            modeArr.add(getLagong3());
        }
        modeArr.add(getLagong2());
        modeArr.add(getLagong());
        modeArr.add(getShut());
        modeArr.add(getShut2());
        modeArr.add(getShut3());
        modeArr.add(getShut4());
        modeArr.add(getShut5());
        modeArr.add(getShut6());
        modeArr.add(getShut7());
        modeArr.add(getShut8());
        modeArr.add(getShut9());

        for (int i = 0; i < 3; i++) {
            modeArr.add(getBlack());
        }

        // 箭飞动画
        for (int i = 0; i < 7; i++) {
            modeArr.add(getFly(i));
        }
        // 爱心闪烁动画
        for (int i = 0; i < 5; i++) {
            modeArr.add(getFlash());
        }
        for (int i = 0; i < 10; i++) {
            modeArr.add(getFlashNo());
        }
        modeArr.add(getFlash2());
        modeArr.add(getFlash());
        modeArr.add(getFlash3());
        modeArr.add(getFlash());
        modeArr.add(getFlash2());
        modeArr.add(getFlash());
        modeArr.add(getFlash3());
        modeArr.add(getFlash());
        modeArr.add(getFlash2());
        modeArr.add(getFlash());
        modeArr.add(getFlash3());
        modeArr.add(getFlash());
        modeArr.add(getFlash2());
        modeArr.add(getFlash());
        modeArr.add(getFlash3());
        modeArr.add(getFlash());
        modeArr.add(getFlash2());

        // 爱心小
        for (int i = 0; i < 5; i++) {
            modeArr.add(getLoveSmall());
        }

        // 爱心中
        for (int i = 0; i < 5; i++) {
            modeArr.add(getLoveMilld());
        }

        // 爱心大
        for (int i = 0; i < 5; i++) {
            modeArr.add(getLoveBig());
        }

        // 爱心中靠拢
        for (int i = 0; i < 5; i++) {
            modeArr.add(getLoveMilldRed());
        }
        // 爱心大靠拢
        for (int i = 0; i < 5; i++) {
            modeArr.add(getLoveBigRed());
        }

        // 爱心大靠拢
        for (int i = 0; i < 5; i++) {
            modeArr.add(getLoveMilldRed2());
        }
        for (int i = 0; i < 5; i++) {
            modeArr.add(getLoveBigRed());
        }

        for (int i = 0; i < 4; i++) {
            modeArr.add(getLoveMilldRed2());
        }
        for (int i = 0; i < 4; i++) {
            modeArr.add(getLoveBigRed());
        }
        for (int i = 0; i < 3; i++) {
            modeArr.add(getLoveMilldRed2());
        }
        for (int i = 0; i < 3; i++) {
            modeArr.add(getLoveBigRed());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getLoveMilldRed2());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getLoveBigRed());
        }
        modeArr.add(getLoveMilldRed2());
        modeArr.add(getLoveBigRed());
        modeArr.add(getLoveMilldRed2());
        modeArr.add(getLoveBigRed());
        modeArr.add(getLoveMilldRed2());
        modeArr.add(getLoveBigRed());
        modeArr.add(getLoveMilldRed2());
        modeArr.add(getLoveBigRed());
        modeArr.add(getLoveMilldRed2());
        modeArr.add(getLoveBigRed());
        modeArr.add(getLoveMilldRed2());
        modeArr.add(getLoveBigRed());

        for (int i = 0; i < 2; i++) {
            modeArr.add(getBlack());
        }


        for (int i = 0; i < 5; i++) {
            modeArr.add(getWu());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getBlack());
        }


        for (int i = 0; i < 5; i++) {
            modeArr.add(getHui());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getBlack());
        }

        for (int i = 0; i < 5; i++) {
            modeArr.add(getJiao());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getBlack());
        }


        for (int i = 0; i < 5; i++) {
            modeArr.add(getQi());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getBlack());
        }

        for (int i = 0; i < 5; i++) {
            modeArr.add(getXi());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getBlack());
        }

        for (int i = 0; i < 5; i++) {
            modeArr.add(getKuai());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getBlack());
        }


        for (int i = 0; i < 5; i++) {
            modeArr.add(getLe());
        }
        for (int i = 0; i < 2; i++) {
            modeArr.add(getBlack());
        }

        lampModel.setModeArr(modeArr);
        return lampModel;
    }

    private DoodlePattern getJiao() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 3) {
                    if (i == 9) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4) {
                    if (i == 10) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5) {
                    if ((i == 2)) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 6) {
                    if (i == 2 || i > 5) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7) {
                    if (i == 2 || i == 4) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8) {
                    if (i < 5 || i == 8 || i == 12) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9) {
                    if (i == 2 || i == 7 || i == 13) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i == 2 || i == 6 || i == 8 || i == 12 || i == 14) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 6 || i == 9 || i == 11) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 3 || i == 10) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 2 || i == 4 || i == 9 || i == 11) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 0 || i == 1 || i == 5 || i == 8 || i == 12) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 15) {
                    if (i == 7 || i == 13) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 16) {
                    if (i == 6 || i == 14) {
                        doodle.setColor(color_jiao);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getHui() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 1 || j == 5) {
                    if (i == 4 || i == 10) {
                        doodle.setColor(color_hui);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 2 || j == 3 || j == 4) {
                    if ((i > 1 && i < 7) || (i > 7 && i < 13)) {
                        doodle.setColor(color_hui);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7 || j == 9 || j == 11) {
                    if ((i > 1 && i < 13)) {
                        doodle.setColor(color_hui);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8 || j == 10) {
                    if (i == 12) {
                        doodle.setColor(color_hui);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 2 || i == 7 || i == 11 || i == 12) {
                        doodle.setColor(color_hui);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 1 || i == 8 || i == 13) {
                        doodle.setColor(color_hui);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 15) {
                    if (i == 0 || i == 3 || i == 8) {
                        doodle.setColor(color_hui);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 16) {
                    if (i == 4 || i == 12) {
                        doodle.setColor(color_hui);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17) {
                    if (i > 4 && i < 14) {
                        doodle.setColor(color_hui);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getLe() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 2) {
                    if (i > 4 && i < 12) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 3) {
                    if (i > 3 && i < 11) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4) {
                    if (i == 2 || i == 3) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5) {
                    if (i == 2 || i == 3 || i == 7) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7 || j == 8 || j == 6) {
                    if (i == 2 || i == 3 || i == 7 || i == 8) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10 || j == 9) {
                    if (i > 1 && i < 13) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11 || j == 12) {
                    if (i == 7 || i == 8) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 3 || i == 4 || i == 7 || i == 8 || i == 11 || i == 10 || i == 12) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 2 || i == 3 || i == 4 || i == 7 || i == 8 || i == 13 || i == 10 || i == 12) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 15) {
                    if (i == 2 || i == 3 || i == 7 || i == 8 || i == 13 || i == 12) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 16) {
                    if (i == 5 || i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17) {
                    if (i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18) {
                    if (i == 7 || i == 8) {
                        doodle.setColor(color_le);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    /**
     * @return
     */
    private DoodlePattern getKuai() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j >= 3 && j <= 5) {
                    if (i == 2 || i == 8) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 6) {
                    if (i == 2 || i == 3 || (i > 5 && i < 12)) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7) {
                    if (i == 1 || i == 2 || i == 4 || i == 8 || i == 11) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8) {
                    if (i == 0 || i == 2 || i == 8 || i == 10) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9) {
                    if (i == 2 || (i > 3 && i < 13)) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i == 8 || i == 2) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 9 || i == 7) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 2 || i == 6 || i == 10) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 2 || i == 5 || i == 11) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 2 || i == 4 || i == 12) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 15) {
                    if (i == 2 || i == 14 || i == 13) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 16) {
                    if (i == 2) {
                        doodle.setColor(color_kuai);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getXi() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 3) {
                    if (i == 7 || i == 8) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4) {
                    if (i == 7 || i == 6) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5) {
                    if (i > 4 && i < 13) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 6) {
                    if (i > 3 && i < 12) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7) {
                    if (i == 3 || i == 4 || i == 10 || i == 9) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8) {
                    if (i == 8 || i == 9) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9 || j == 11) {
                    if (i == 8 || i == 7 || i == 5 || i == 6) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i == 7 || i == 5 || i == 6) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 4 || i == 5 || i == 7 || i == 8 || i == 9) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 4 || i == 3 || i == 8 || i == 9) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 2 || i == 3) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 15) {
                    if (i == 2 || i == 1) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 16) {
                    if (i == 1) {
                        doodle.setColor(color_xi);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getQi() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 4) {
                    if (i == 5) {
                        doodle.setColor(color_qi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5 || j == 6 || j == 7 || j == 8 || j == 11 || j == 12) {
                    if (i == 5 || i == 6) {
                        doodle.setColor(color_qi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9) {
                    if (i > 1 && i < 12) {
                        doodle.setColor(color_qi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 0 && i < 11) {
                        doodle.setColor(color_qi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 5 || i == 6 || i == 12) {
                        doodle.setColor(color_qi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i > 5 && i < 14) {
                        doodle.setColor(color_qi);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 15) {
                    if (i > 6 && i < 13) {
                        doodle.setColor(color_qi);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getLoveMilldRed2() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 3) {
                    if (i == 6 || i == 5 || i == 4 || i == 8 || i == 9 || i == 10) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4) {
                    if (i == 6 || i == 5 || i == 8 || i == 9) {
                        doodle.setColor(color_love_big);
                    } else if (i == 4 || i == 3 || i == 10 || i == 11 || i == 7) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5 || j == 6) {
                    if (i > 3 && i < 11) {
                        doodle.setColor(color_love_big);
                    } else if (i == 3 || i == 11) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7) {
                    if (i > 4 && i < 10) {
                        doodle.setColor(color_love_big);
                    } else if (i == 4 || i == 10) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8) {
                    if (i > 5 && i < 9) {
                        doodle.setColor(color_love_big);
                    } else if (i == 5 || i == 9) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9) {
                    if (i > 6 && i < 8) {
                        doodle.setColor(color_love_big);
                    } else if (i == 6 || i == 8) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i == 7) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 4 || i == 5 || i == 9 || i == 10) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17) {
                    if (i == 4 || i == 5 || i == 9 || i == 10) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18 || j == 16 || j == 15) {
                    if (i == 5 || i == 6 || i == 3 || i == 4 || i == 9 || i == 10 || i == 11 || i == 8) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i > 1 && i < 13) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getLoveBigRed() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 2) {
                    if ((i > 1 && i < 6) || (i > 8 && i < 13)) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 3) {
                    if (i == 6 || i == 5 || i == 4 || i == 8 || i == 9 || i == 10) {
                        doodle.setColor(color_love_big);
                    } else if (i > 0 && i < 14 && i != 7) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4) {
                    if (i == 6 || i == 5 || i == 8 || i == 9) {
                        doodle.setColor(color_love_big);
                    } else if (i == 4 || i == 3 || i == 10 || i == 11 || i == 7) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor(color_love_big);
                    }
                } else if (j == 5 || j == 6) {
                    if (i > 3 && i < 11) {
                        doodle.setColor(color_love_big);
                    } else if (i == 3 || i == 11) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor(color_love_big);
                    }
                } else if (j == 7) {
                    if (i > 4 && i < 10) {
                        doodle.setColor(color_love_big);
                    } else if (i == 4 || i == 10) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor(color_love_big);
                    }
                } else if (j == 8) {
                    if (i > 5 && i < 9) {
                        doodle.setColor(color_love_big);
                    } else if (i == 5 || i == 9) {
                        doodle.setColor(color_love_big);
                    } else if (i == 1 || i == 2 || i == 3 || i == 4 || i == 10 || i == 13 || i == 12 || i == 11) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9) {
                    if (i > 6 && i < 8) {
                        doodle.setColor(color_love_big);
                    } else if (i == 6 || i == 8) {
                        doodle.setColor(color_love_big);
                    } else if (i == 4 || i == 5 || i == 2 || i == 3 || i == 9 || i == 10 || i == 12 || i == 11) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i == 7 || i == 6 || i == 8) {
                        doodle.setColor(color_love_big);
                    } else if (i == 4 || i == 5 || i == 3 || i == 10 || i == 9 || i == 11) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 4 || i == 5 || i == 7 || i == 6 || i == 10 || i == 9 || i == 8) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 7 || i == 5 || i == 6 || i == 9 || i == 8) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 7 || i == 6 || i == 8) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 4 || i == 5 || i == 9 || i == 10) {
                        doodle.setColor(color_us);
                    } else if (i == 7) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17) {
                    if (i == 4 || i == 5 || i == 9 || i == 10) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18 || j == 16 || j == 15) {
                    if (i == 5 || i == 6 || i == 3 || i == 4 || i == 9 || i == 10 || i == 11 || i == 8) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i > 1 && i < 13) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getLoveMilldRed() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 3) {
                    if (i == 6 || i == 5 || i == 4 || i == 8 || i == 9 || i == 10) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4) {
                    if (i == 6 || i == 5 || i == 8 || i == 9) {
                        doodle.setColor(color_love_big);
                    } else if (i == 4 || i == 3 || i == 10 || i == 11 || i == 7) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5 || j == 6) {
                    if (i > 3 && i < 11) {
                        doodle.setColor(color_love_big);
                    } else if (i == 3 || i == 11) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7) {
                    if (i > 4 && i < 10) {
                        doodle.setColor(color_love_big);
                    } else if (i == 4 || i == 10) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8) {
                    if (i > 5 && i < 9) {
                        doodle.setColor(color_love_big);
                    } else if (i == 5 || i == 9) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9) {
                    if (i > 6 && i < 8) {
                        doodle.setColor(color_love_big);
                    } else if (i == 6 || i == 8) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i == 7) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17 || j == 14) {
                    if (i == 4 || i == 3 || i == 11 || i == 10) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18 || j == 16 || j == 15) {
                    if (i == 5 || i == 2 || i == 3 || i == 4 || i == 9 || i == 10 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i > 0 && i < 14) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getLoveBig() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 2) {
                    if ((i > 1 && i < 6) || (i > 8 && i < 13)) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 3) {
                    if (i == 6 || i == 5 || i == 4 || i == 8 || i == 9 || i == 10) {
                        doodle.setColor(color_love_milld);
                    } else if (i > 0 && i < 14 && i != 7) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4) {
                    if (i == 6 || i == 5 || i == 8 || i == 9) {
                        doodle.setColor(color_love_small);
                    } else if (i == 4 || i == 3 || i == 10 || i == 11 || i == 7) {
                        doodle.setColor(color_love_milld);
                    } else {
                        doodle.setColor(color_love_big);
                    }
                } else if (j == 5 || j == 6) {
                    if (i > 3 && i < 11) {
                        doodle.setColor(color_love_small);
                    } else if (i == 3 || i == 11) {
                        doodle.setColor(color_love_milld);
                    } else {
                        doodle.setColor(color_love_big);
                    }
                } else if (j == 7) {
                    if (i > 4 && i < 10) {
                        doodle.setColor(color_love_small);
                    } else if (i == 4 || i == 10) {
                        doodle.setColor(color_love_milld);
                    } else {
                        doodle.setColor(color_love_big);
                    }
                } else if (j == 8) {
                    if (i > 5 && i < 9) {
                        doodle.setColor(color_love_small);
                    } else if (i == 5 || i == 9) {
                        doodle.setColor(color_love_milld);
                    } else if (i == 1 || i == 2 || i == 3 || i == 4 || i == 13 || i == 12 || i == 11 || i == 10) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9) {
                    if (i > 6 && i < 8) {
                        doodle.setColor(color_love_small);
                    } else if (i == 6 || i == 8) {
                        doodle.setColor(color_love_milld);
                    } else if (i == 4 || i == 5 || i == 2 || i == 3 || i == 10 || i == 9 || i == 12 || i == 11) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i == 7) {
                        doodle.setColor(color_love_milld);
                    } else if (i == 4 || i == 5 || i == 6 || i == 3 || i == 10 || i == 8 || i == 9 || i == 11) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 4 || i == 5 || i == 6 || i == 7 || i == 10 || i == 9 || i == 8) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 7 || i == 5 || i == 6 || i == 9 || i == 8) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 7 || i == 6 || i == 8) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 2 || i == 3 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else if (i == 7) {
                        doodle.setColor(color_love_big);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17) {
                    if (i == 2 || i == 3 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18 || j == 16 || j == 15) {
                    if (i == 1 || i == 2 || i == 3 || i == 4 || i == 13 || i == 10 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i < 6 || i > 8) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    /**
     * 中爱心
     *
     * @return
     */
    private DoodlePattern getLoveMilld() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 3) {
                    if (i == 6 || i == 5 || i == 4 || i == 8 || i == 9 || i == 10) {
                        doodle.setColor(color_love_milld);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4) {
                    if (i == 6 || i == 5 || i == 8 || i == 9) {
                        doodle.setColor(color_love_small);
                    } else if (i == 4 || i == 3 || i == 10 || i == 11 || i == 7) {
                        doodle.setColor(color_love_milld);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5 || j == 6) {
                    if (i > 3 && i < 11) {
                        doodle.setColor(color_love_small);
                    } else if (i == 3 || i == 11) {
                        doodle.setColor(color_love_milld);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7) {
                    if (i > 4 && i < 10) {
                        doodle.setColor(color_love_small);
                    } else if (i == 4 || i == 10) {
                        doodle.setColor(color_love_milld);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8) {
                    if (i > 5 && i < 9) {
                        doodle.setColor(color_love_small);
                    } else if (i == 5 || i == 9) {
                        doodle.setColor(color_love_milld);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9) {
                    if (i > 6 && i < 8) {
                        doodle.setColor(color_love_small);
                    } else if (i == 6 || i == 8) {
                        doodle.setColor(color_love_milld);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i == 7) {
                        doodle.setColor(color_love_milld);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17 || j == 14) {
                    if (i == 2 || i == 3 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18 || j == 16 || j == 15) {
                    if (i == 1 || i == 2 || i == 3 || i == 4 || i == 13 || i == 10 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i < 6 || i > 8) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    /**
     * 小爱心
     *
     * @return
     */
    private DoodlePattern getLoveSmall() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 4) {
                    if (i == 6 || i == 5 || i == 8 || i == 9) {
                        doodle.setColor(color_love_small);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5 || j == 6) {
                    if (i > 3 && i < 11) {
                        doodle.setColor(color_love_small);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7) {
                    if (i > 4 && i < 10) {
                        doodle.setColor(color_love_small);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8) {
                    if (i > 5 && i < 9) {
                        doodle.setColor(color_love_small);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9) {
                    if (i > 6 && i < 8) {
                        doodle.setColor(color_love_small);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17 || j == 14) {
                    if (i == 2 || i == 3 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18 || j == 16 || j == 15) {
                    if (i == 1 || i == 2 || i == 3 || i == 4 || i == 13 || i == 10 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i < 6 || i > 8) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getFlash3() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 7) {
                    if (i == 7) {
                        doodle.setColor(color_flash);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 6 || j == 8) {
                    if (i == 8 || i == 6) {
                        doodle.setColor(color_flash);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17 || j == 14) {
                    if (i == 2 || i == 3 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18 || j == 16 || j == 15) {
                    if (i == 1 || i == 2 || i == 3 || i == 4 || i == 13 || i == 10 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i < 6 || i > 8) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }


    private DoodlePattern getFlashNo() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 17 || j == 14) {
                    if (i == 2 || i == 3 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18 || j == 16 || j == 15) {
                    if (i == 1 || i == 2 || i == 3 || i == 4 || i == 13 || i == 10 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i < 6 || i > 8) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getFlash2() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 7) {
                    if (i == 7 || i == 8 || i == 6) {
                        doodle.setColor(color_flash);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 6 || j == 8) {
                    if (i == 7) {
                        doodle.setColor(color_flash);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17 || j == 14) {
                    if (i == 2 || i == 3 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18 || j == 16 || j == 15) {
                    if (i == 1 || i == 2 || i == 3 || i == 4 || i == 13 || i == 10 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i < 6 || i > 8) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getFlash() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 7) {
                    if (i == 7) {
                        doodle.setColor(color_flash);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17 || j == 14) {
                    if (i == 2 || i == 3 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18 || j == 16 || j == 15) {
                    if (i == 1 || i == 2 || i == 3 || i == 4 || i == 13 || i == 10 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i < 6 || i > 8) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getFly(int index) {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 14 - index) {
                    if (i == index) {
                        doodle.setColor(color_fly);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13 - index) {
                    if (i == index + 1) {
                        doodle.setColor(color_fly);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17 || j == 14) {
                    if (i == 2 || i == 3 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18 || j == 16 || j == 15) {
                    if (i == 1 || i == 2 || i == 3 || i == 4 || i == 13 || i == 10 || i == 11 || i == 12) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i < 6 || i > 8) {
                        doodle.setColor(color_us);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getShut9() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    doodle.setColor(color_gong);
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
        return doodlePattern;
    }


    private DoodlePattern getShut8() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 0 || j == 1) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    doodle.setColor(color_gong);
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
        return doodlePattern;
    }

    private DoodlePattern getShut7() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 0 || j == 1 || j == 2 || j == 3 || j == 4) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    doodle.setColor(color_gong);
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
        return doodlePattern;
    }

    private DoodlePattern getShut6() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 0 || j == 1 || j == 2 || j == 3 || j == 4 || j == 5 || j == 6) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    doodle.setColor(color_gong);
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
        return doodlePattern;
    }

    private DoodlePattern getShut5() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 0) {
                    if (i == 6 || i == 5 || i == 7 || i == 8 || i == 9) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 1 || j == 2 || j == 3 || j == 4 || j == 5 || j == 6 || j == 7) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    doodle.setColor(color_gong);
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
        return doodlePattern;
    }

    private DoodlePattern getShut4() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 0) {
                    if (i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 1) {
                    if (i == 6 || i == 5 || i == 7 || i == 8 || i == 9) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 2 || j == 3 || j == 4 || j == 5 || j == 6 || j == 7 || j == 8) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    doodle.setColor(color_gong);
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
        return doodlePattern;
    }

    private DoodlePattern getShut3() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 0) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 1) {
                    if (i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 2) {
                    if (i == 6 || i == 5 || i == 7 || i == 8 || i == 9) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 3 || j == 4 || j == 5 || j == 6 || j == 7 || j == 8 || j == 9) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    doodle.setColor(color_gong);
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
        return doodlePattern;
    }

    private DoodlePattern getShut2() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 1) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 2) {
                    if (i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 3) {
                    if (i == 6 || i == 5 || i == 7 || i == 8 || i == 9) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4 || j == 5 || j == 6 || j == 7 || j == 8 || j == 9) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    doodle.setColor(color_gong);
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
        return doodlePattern;
    }

    private DoodlePattern getShut() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 2) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 3) {
                    if (i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4) {
                    if (i == 6 || i == 5 || i == 7 || i == 8 || i == 9) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5 || j == 6 || j == 7 || j == 8 || j == 9) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    doodle.setColor(color_gong);
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
        return doodlePattern;
    }

    private DoodlePattern getLagong3() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 7) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8) {
                    if (i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9) {
                    if (i == 6 || i == 5 || i == 7 || i == 8 || i == 9) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else if (i == 0 || i == 14 || i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 15) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else if (i == 3 || i == 11) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 16) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else if (i == 4 || i == 10) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 17) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else if (i == 5 || i == 9) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 18) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else if (i == 6 || i == 8) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 19) {
                    if (i == 7) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getLagong2() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 5) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 6) {
                    if (i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7) {
                    if (i == 6 || i == 5 || i == 7 || i == 8 || i == 9) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8 || j == 9) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else if (i < 2 || i > 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else if (i == 2 || i == 3 || i == 12 || i == 11) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 15) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else if (i == 4 || i == 5 || i == 6 || i == 8 || i == 9 || i == 10) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 16) {
                    if (i == 7) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getLagong() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 4) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5) {
                    if (i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 6) {
                    if (i == 6 || i == 5 || i == 7 || i == 8 || i == 9) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7 || j == 8 || j == 9) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else if (i < 4 || i > 10) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else if (i >= 4 && i <= 10) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getDaJian() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 3) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4) {
                    if (i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5) {
                    if (i == 6 || i == 5 || i == 7 || i == 8 || i == 9) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 6 || j == 7 || j == 8 || j == 9) {
                    if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_gong);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 2 || i == 12) {
                        doodle.setColor(color_gong);
                    } else if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 1 || i == 13) {
                        doodle.setColor(color_gong);
                    } else if (i == 7) {
                        doodle.setColor(color_jian);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    doodle.setColor(color_gong);
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
        return doodlePattern;
    }

    private DoodlePattern getTwo() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 3) {
                    if (i == 5 || i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4) {
                    if (i == 4 || i == 5 || i == 9 || i == 8) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5) {
                    if (i == 3 || i == 4 || i == 10 || i == 9) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 6) {
                    if (i == 3 || i == 4 || i == 10 || i == 9) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7) {
                    if (i == 10 || i == 9) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8) {
                    if (i == 9 || i == 10) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9) {
                    if (i == 9 || i == 8) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 10) {
                    if (i == 7 || i == 8) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i == 7 || i == 6) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 5 || i == 6) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 4 || i == 5) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 3 || i == 4) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 15 || j == 16) {
                    if (i > 2 && i < 12) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getThree() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 3 || j == 16) {
                    if (i == 5 || i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9 || j == 10) {
                    if (i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4 || j == 15) {
                    if (i == 4 || i == 5 || i == 8 || i == 9) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 5 || j == 14) {
                    if (i == 3 || i == 4 || i == 9 || i == 10) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 6 || j == 13) {
                    if (i == 3 || i == 4 || i == 9 || i == 10) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 7 || j == 12) {
                    if (i == 9 || i == 10) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 8 || j == 11) {
                    if (i == 9 || i == 8) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getOne() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j >= 3 && j <= 16) {
                    if (i == 6 || i == 7 || i == 8) {
                        doodle.setColor(color_num);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
    }

    private DoodlePattern getBlack() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                doodle.setColor("#000000");
                doodle.setLight(255);
                doodle.setFlash(0);
                light_status.put(String.valueOf(i * size / column + j), doodle);
            }
        }
        doodlePattern.setLight_status(light_status);
        doodlePattern.setSize(size);
        return doodlePattern;
    }

    private DoodlePattern getWu() {
        DoodlePattern doodlePattern = new DoodlePattern();
        HashMap<String, Doodle> light_status = new HashMap<>();
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                Doodle doodle = new Doodle();
                if (j == 3 || j == 6 || j == 8) {
                    if (i > 3 && i < 11) {
                        doodle.setColor(color_wu);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 4 || j == 5) {
                    if (i == 3 || i == 11) {
                        doodle.setColor(color_wu);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 9 || j == 10) {
                    if (i == 7) {
                        doodle.setColor(color_wu);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 11) {
                    if (i > 1 && i < 14) {
                        doodle.setColor(color_wu);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 12) {
                    if (i == 7) {
                        doodle.setColor(color_wu);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 13) {
                    if (i == 6 || i == 8) {
                        doodle.setColor(color_wu);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 14) {
                    if (i == 5 || i == 9) {
                        doodle.setColor(color_wu);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 15) {
                    if (i == 4 || i == 1 || i == 10 || i == 13) {
                        doodle.setColor(color_wu);
                    } else {
                        doodle.setColor("#000000");
                    }
                } else if (j == 16) {
                    if (i == 3 || i == 2 || i == 11 || i == 12) {
                        doodle.setColor(color_wu);
                    } else {
                        doodle.setColor("#000000");
                    }
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
        return doodlePattern;
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
