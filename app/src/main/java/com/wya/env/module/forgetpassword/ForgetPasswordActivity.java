package com.wya.env.module.forgetpassword;

import android.view.View;
import android.widget.EditText;

import com.wya.env.R;
import com.wya.env.base.BaseMvpActivity;
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
    WYAButton btnCode;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.sure_password)
    EditText surePassword;
    @BindView(R.id.but_forget_password)
    WYAButton butForgetPassword;

    @Override
    protected void initView() {
        setTitle(getResources().getString(R.string.forget));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget_password;
    }


    @OnClick({R.id.btn_code, R.id.but_forget_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_code:
                break;
            case R.id.but_forget_password:
                break;
        }
    }
}