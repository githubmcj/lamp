package com.wya.env.module.login.start;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.wya.env.MainActivity;
import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.manager.ActivityManager;
import com.wya.uikit.button.WYAButton;

import butterknife.BindView;
import butterknife.OnClick;


public class NoFoundDeviceActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.next)
    TextView next;

    @BindView(R.id.again)
    WYAButton again;

    @Override
    protected void initView() {
        showToolBar(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_no_found_wifi;
    }

    @OnClick({R.id.next, R.id.again})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.again:
                startActivity(new Intent(NoFoundDeviceActivity.this, Start1Activity.class));
                finish();
                break;
            case R.id.next:
                ActivityManager.getInstance().exitApp();
                startActivity(new Intent(NoFoundDeviceActivity.this, MainActivity.class));
                break;
            default:
                break;
        }
    }
}
