package com.wya.env.module.login.start;

import android.content.Intent;
import android.graphics.Paint;
import android.widget.TextView;

import com.wya.env.MainActivity;
import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.module.login.LoginActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class Start1Activity extends BaseActivity {

    @BindView(R.id.next)
    TextView next;

    @Override
    protected void initView() {
        showToolBar(false);
        next.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start1;
    }

    @OnClick(R.id.next)
    public void onViewClicked() {
        startActivity(new Intent(Start1Activity.this, Start2Activity.class));
    }
}
