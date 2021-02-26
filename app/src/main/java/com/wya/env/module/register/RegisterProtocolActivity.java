package com.wya.env.module.register;

import com.wya.env.R;
import com.wya.env.base.BaseActivity;

/**
 * @date: 2020/8/1 15:48
 * @author: Chunjiang Mao
 * @classname: RegisterProtocolActivity
 * @describe: 注册协议
 */
public class RegisterProtocolActivity extends BaseActivity {

    @Override
    protected void initView() {
        setTitle(getResources().getString(R.string.register_protocol_title));
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_register_protocol;
    }
}