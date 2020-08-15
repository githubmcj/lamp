package com.wya.env.util;


import android.os.CountDownTimer;
import android.widget.TextView;

import com.wya.env.R;

public class CountDownTimerUtils extends CountDownTimer {
    private TextView mTextView;

    public CountDownTimerUtils(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        // 设置不可点击
        mTextView.setClickable(false);
        // 设置倒计时时间
        mTextView.setText(millisUntilFinished / 1000 + "s后重新获取");
        // 设置按钮为灰色，这时是不能点击的
        mTextView.setBackgroundResource(R.drawable.code_enable);
    }

    @Override
    public void onFinish() {
        mTextView.setText("重新获取");
        // 重新获得点击
        mTextView.setClickable(true);
        // 还原背景色
        mTextView.setBackgroundResource(R.drawable.code_normal);
    }
}