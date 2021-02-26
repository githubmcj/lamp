package com.wya.env.module.forgetpassword;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wya.env.R;
import com.wya.env.base.BaseMvpActivity;
import com.wya.env.util.CountDownTimerUtils;
import com.wya.uikit.button.WYAButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @date: 2020/8/1 14:20
 * @author: Chunjiang Mao
 * @classname: ForgetPasswordActivity
 * @describe: 忘记密码
 */
public class ForgetPasswordActivity extends BaseMvpActivity<ForgetPasswordPresent> implements ForgetPasswordView {


    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.btn_code)
    TextView btnCode;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.sure_password)
    EditText surePassword;
    @BindView(R.id.but_forget_password)
    WYAButton butForgetPassword;

    private ForgetPasswordPresent forgetPasswordPresent;

    @Override
    protected void initView() {
        setTitle(getResources().getString(R.string.forget));
        forgetPasswordPresent = new ForgetPasswordPresent();
        forgetPasswordPresent.mView = this;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_forget_password;
    }


    @OnClick({R.id.btn_code, R.id.but_forget_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_code:
                if (TextUtils.isEmpty(email.getText().toString())) {
                    showShort("please enter email");
                    return;
                }
                sendCode();
                break;
            case R.id.but_forget_password:
                if (TextUtils.isEmpty(email.getText().toString())) {
                    showShort("please enter email");
                    return;
                }
                if (TextUtils.isEmpty(code.getText().toString())) {
                    showShort("please enter code");
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
                forgetPasswordPresent.changePassword(email.getText().toString(), password.getText().toString(), code.getText().toString());
                break;
            default:
                break;
        }
    }

    private void sendCode() {
        forgetPasswordPresent.getCode(email.getText().toString());
    }

    @Override
    public void onRegisterResult() {
        this.finish();
    }

    @Override
    public void onCodeResult() {
        CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(btnCode, 60000, 1000);
        mCountDownTimerUtils.start();
    }

}