package com.wya.env.module.login.start;

import android.content.Intent;
import android.view.KeyEvent;
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
        initShowToolBar(false);
    }

    @Override
    protected int getLayoutID() {
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
                if(ActivityManager.getInstance().leaveActivity(NoFoundDeviceActivity.class)){
                    startActivity(new Intent(NoFoundDeviceActivity.this, MainActivity.class));
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ActivityManager.getInstance().popOthersActivity(Start1Activity.class);
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
