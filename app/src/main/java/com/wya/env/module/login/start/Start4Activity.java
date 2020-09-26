package com.wya.env.module.login.start;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wya.env.MainActivity;
import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.manager.ActivityManager;
import com.wya.uikit.button.WYAButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


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
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start4;
    }

    @OnClick({R.id.use, R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.use:
                startActivity(new Intent(Start4Activity.this, MainActivity.class));
                ActivityManager.getInstance().exitApp();
                break;
            case R.id.add:
                // 跳转到主界面
                startActivity(new Intent(Start4Activity.this, Start2Activity.class));
                finish();
                break;
            default:
                break;
        }
    }
}
