package com.wya.env.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.widget.Toast;

import com.wya.env.R;
import com.wya.env.util.GestureFlingRightHelper;
import com.wya.uikit.dialog.WYALoadingDialog;
import com.wya.uikit.toolbar.BaseToolBarActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @date: 2018/7/3 13:48
 * @author: Chunjiang Mao
 * @classname: BaseActivity
 * @describe: BaseActivity
 */

public abstract class BaseActivity extends BaseToolBarActivity {
    private Unbinder unbinder;
    public WYALoadingDialog loadingDialog;
    private boolean mIsSwipeBack = false;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initToolBar();
        unbinder = ButterKnife.bind(this);
        loadingDialog = new WYALoadingDialog(this, false, true);
        loadingDialog.setText("loading...");
        initView();
        setSwipeBackEnable(false);
//        initGesture();
    }

    private void initGesture() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mGestureDetector = GestureFlingRightHelper.getInstance().getGestureDetector(this, () -> {
            if (mIsSwipeBack) {
                finish();
                return true;
            } else {
                return false;
            }
        }, outMetrics.widthPixels);
    }

    private void initToolBar() {
        initToolBarBgColor(getResources().getColor(R.color.white), true);
        initToolBarTitle("", 18, getResources().getColor(R.color.color_33), true);
        initImgLeft(R.drawable.fanhui, true);
    }

    /**
     * 初始化view
     */
    protected abstract void initView();

    public void showShort(String msg) {
        try {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // TODO 暂不处理这个奔溃问题
//            //解决在子线程中调用Toast的异常情况处理
//            Looper.prepare();
//            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//            Looper.loop();
        }
    }

    public void toastShowLong(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroy();
    }

}
