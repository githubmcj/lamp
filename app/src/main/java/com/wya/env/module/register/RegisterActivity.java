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
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.module.login.start.Start1Activity;
import com.wya.env.util.SaveSharedPreferences;
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
    @BindView(R.id.img_read2)
    ImageView imgRead2;

    private boolean isRead;
    private boolean isRead2;
    private RegisterPresent registerPresent;
    private Lamps lamps;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void initView() {
        setTitle(getResources().getString(R.string.register));
        setLeftText("Back");
        isRead = true;
        isRead2 = true;
        registerPresent = new RegisterPresent();
        registerPresent.mView = this;
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
                if (password.getText().toString().length() < 8) {
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
        SaveSharedPreferences.save(RegisterActivity.this, CommonValue.IS_LOGIN, true);
        App.TOKEN = loginInfo.getToken();
        SaveSharedPreferences.save(RegisterActivity.this, CommonValue.TOKEN, loginInfo.getToken());
        SaveSharedPreferences.save(this, CommonValue.LOGIN_INFO, new Gson().toJson(loginInfo));
    }
}