package com.wya.env.module.login.start;

import android.content.Intent;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.wya.env.MainActivity;
import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.manager.ActivityManager;
import com.wya.uikit.button.WYAButton;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * @date: 2020\9\28 0028 14:16
 * @author: Chunjiang Mao
 * @classname: Start4Activity
 * @describe: 
 */
public class Start4Activity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.use)
    WYAButton use;
    @BindView(R.id.add)
    WYAButton add;
    @BindView(R.id.tv_content)
    TextView tvContent;

    private String name;

    @Override
    protected void initView() {
        showToolBar(false);
        name = getIntent().getStringExtra("name");
        tvContent.setText("Now you can enjoy the full capabilities of your " + name + ".\\\nLet's start your wonderful journey!");


        RxView.clicks(use)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(Observable -> {
                    // 跳转到主界面
                    startActivity(new Intent(Start4Activity.this, MainActivity.class));
                    ActivityManager.getInstance().exitApp();
                });

        RxView.clicks(add)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(Observable -> {
                    startActivity(new Intent(Start4Activity.this, Start2Activity.class));
                    finish();
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start4;
    }

}
