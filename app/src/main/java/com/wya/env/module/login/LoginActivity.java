package com.wya.env.module.login;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
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
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.module.forgetpassword.ForgetPasswordActivity;
import com.wya.env.module.login.start.NoFoundDeviceActivity;
import com.wya.env.module.login.start.Start1Activity;
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

import static java.lang.Math.tan;

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

    private String[] snow_colors = {"#ffffff", "#B04F9C", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000"};
    private String[] fifth_colors = {"#FA0000", "#FAA500", "#00FF00"};

    private Lamps lamps;


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
    }

    @Override
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
        lamps = new Gson().fromJson(SaveSharedPreferences.getString(this, CommonValue.LAMPS), Lamps.class);
        if(lamps != null && lamps.getLampSettings() != null && lamps.getLampSettings().size() > 0){
            saveInfo(loginInfo);
            ActivityManager.getInstance().exitApp();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
            saveInfo(loginInfo);
            startActivity(new Intent(LoginActivity.this, Start1Activity.class));
            finish();
        }
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

    private List<LampModel> getModels() {
        List<LampModel> mLampModels = new ArrayList<>();
        mLampModels.add(getFirstModel());
        mLampModels.add(getSecondModel());
        mLampModels.add(getThirdModel());
        mLampModels.add(getFourthModel());
        mLampModels.add(getFifthModel());

        return mLampModels;
    }

    int alpha = 14;
    int beta = 7;
    int gama = 0;

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


//    private LampModel getFirstModel() {
//        LampModel lampModel = new LampModel();
//        lampModel.setName("第一个模板");
//        List<DoodlePattern> modeArr = new ArrayList<>();
//        for (int k = 0; k < size / column; k++) {
//            DoodlePattern doodlePattern = new DoodlePattern();
//            HashMap<String, Doodle> light_status = new HashMap<>();
//            for (int i = 0; i < column; i++) {
//                for (int j = 0; j < size / column; j++) {
//                    Doodle doodle = new Doodle();
//                    if (j % (size / column) == (i + k) % (size / column)) {
//                        doodle.setColor("#ff0000");
//                        doodle.setLight(255);
//                    } else if (j % (size / column) == (i + k + 1) % (size / column)) {
//                        doodle.setColor("#ff0000");
//                        doodle.setLight(255 - 50);
//                    } else if (j % (size / column) == (i + k + 2) % (size / column)) {
//                        doodle.setColor("#ff0000");
//                        doodle.setLight(255 - 2 * 50);
//                    } else if (j % (size / column) == (i + k + 3) % (size / column)) {
//                        doodle.setColor("#ff0000");
//                        doodle.setLight(255 - 3 * 50);
//                    } else if (j % (size / column) == (i + k + 4) % (size / column)) {
//                        doodle.setColor("#ff0000");
//                        doodle.setLight(255 - 4 * 50);
//                    } else {
//                        doodle.setColor("#000000");
//                    }
//                    doodle.setLight(255);
//                    doodle.setFlash(0);
//                    light_status.put(String.valueOf(i * size / column + j), doodle);
//                }
//            }
//            doodlePattern.setLight_status(light_status);
//            doodlePattern.setSize(size);
//            modeArr.add(doodlePattern);
//        }
//        lampModel.setModeArr(modeArr);
//        return lampModel;
//    }


//    private LampModel getFourthModel() {
//        LampModel lampModel = new LampModel();
//        lampModel.setName("第四个模板");
//        List<DoodlePattern> modeArr = new ArrayList<>();
//        for (int i = 0; i < size / column; i++) {
//            DoodlePattern doodlePattern = new DoodlePattern();
//            HashMap<String, Doodle> light_status = new HashMap<>();
//            for (int j = 0; j < size; j++) {
//                Doodle doodle = new Doodle();
//                if (j % (size / column) == (i) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255);
//                } else if (j % (size / column) == (i + 1) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 20);
//                } else if (j % (size / column) == (i + 2) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 2 * 20);
//                } else if (j % (size / column) == (i + 3) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 3 * 20);
//                } else if (j % (size / column) == (i + 4) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 4 * 20);
//                } else if (j % (size / column) == (i + 5) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 5 * 20);
//                } else if (j % (size / column) == (i + 6) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 6 * 20);
//                } else if (j % (size / column) == (i + 7) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 7 * 20);
//                } else if (j % (size / column) == (i + 8) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 8 * 20);
//                } else if (j % (size / column) == (i + 9) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 9 * 20);
//                } else if (j % (size / column) == (i + 10) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 10 * 20);
//                } else if (j % (size / column) == (i + 11) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 11 * 20);
//                } else if (j % (size / column) == (i + 12) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 12 * 20);
//                } else if (j % (size / column) == (i + 13) % (size / column)) {
//                    doodle.setColor("#ff0000");
//                    doodle.setLight(255 - 13 * 20);
//                } else {
//                    doodle.setColor("#000000");
//                    doodle.setLight(255);
//                }
//                light_status.put(String.valueOf(j), doodle);
//            }
//            doodlePattern.setLight_status(light_status);
//            doodlePattern.setSize(size);
//            modeArr.add(doodlePattern);
//        }
//        lampModel.setModeArr(modeArr);
//        return lampModel;
//    }

}
