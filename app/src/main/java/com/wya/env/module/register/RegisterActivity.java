package com.wya.env.module.register;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wya.env.R;
import com.wya.env.base.BaseMvpActivity;
import com.wya.uikit.button.WYAButton;

import butterknife.BindView;
import butterknife.OnClick;

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

    @Override
    protected void initView() {
        setTitle(getResources().getString(R.string.register));
        isRead = false;
        registerPresent = new RegisterPresent();
        registerPresent.mView = this;
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
    public void onRegisterResult() {
        this.finish();
    }
}