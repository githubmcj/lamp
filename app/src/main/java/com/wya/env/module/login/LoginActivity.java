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
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.module.forgetpassword.ForgetPasswordActivity;
import com.wya.env.module.login.start.Start1Activity;
import com.wya.env.module.register.RegisterActivity;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.uikit.button.WYAButton;
import com.wya.utils.utils.LogUtil;

import org.json.JSONObject;

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


    private Lamps lamps;

    @Override
    protected void initView() {
        initShowToolBar(false);
        loginPresent.mView = this;
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
        startActivity(new Intent(LoginActivity.this, Start1Activity.class));
        finish();
//        // 保存数据
        lamps = new Gson().fromJson(SaveSharedPreferences.getString(this, CommonValue.LAMPS), Lamps.class);
        if (lamps != null && lamps.getLampSettings() != null && lamps.getLampSettings().size() > 0) {
            saveInfo(loginInfo);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            ActivityManager.getInstance().exitApp();
        } else {
            saveInfo(loginInfo);
            startActivity(new Intent(LoginActivity.this, Start1Activity.class));
            finish();
        }
    }

    private void saveInfo(LoginInfo loginInfo) {
        SaveSharedPreferences.save(LoginActivity.this, CommonValue.IS_LOGIN, true);
        App.TOKEN = loginInfo.getToken();
        SaveSharedPreferences.save(LoginActivity.this, CommonValue.TOKEN, loginInfo.getToken());
        SaveSharedPreferences.save(this, CommonValue.LOGIN_INFO, new Gson().toJson(loginInfo));
    }

    @Override
    protected int getLayoutID() {
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
}
