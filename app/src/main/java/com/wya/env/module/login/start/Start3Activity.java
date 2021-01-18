package com.wya.env.module.login.start;

import android.content.Intent;
import android.graphics.Paint;
import android.widget.TextView;

import com.wya.env.R;
import com.wya.env.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @date: 2020\9\28 0028 14:16
 * @author: Chunjiang Mao
 * @classname: Start3Activity
 * @describe: 
 */
public class Start3Activity extends BaseActivity {

    @BindView(R.id.next)
    TextView next;

    @Override
    protected void initView() {
        showToolBar(false);
        next.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start3;
    }

    @OnClick(R.id.next)
    public void onViewClicked() {
        startActivity(new Intent(Start3Activity.this, SearchDeviceByBleActivity.class));
    }
}
