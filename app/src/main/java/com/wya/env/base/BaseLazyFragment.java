package com.wya.env.base;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wya.uikit.dialog.WYALoadingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @date: 2018/7/3 13:48
 * @author: Chunjiang Mao
 * @classname: BaseLazyFragment
 * @describe: BaseLazyFragment
 */

public abstract class BaseLazyFragment extends Fragment {
    protected View rootView;
    /**
     * 当前Fragment是否处于可见状态标志，防止因ViewPager的缓存机制而导致回调函数的触发
     */
    private boolean isFragmentVisible;
    /**
     * 是否是第一次开启网络加载
     */
    public boolean isFirst;
    public Unbinder mUnbinder;
    public String token;

    public WYALoadingDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutResource(), container, false);
            mUnbinder = ButterKnife.bind(this, rootView);
        }
        loadingDialog = new WYALoadingDialog(getActivity(), false, false);
        loadingDialog.setText("loading...");
        initView();
        //可见，但是并没有加载过
        if (isFragmentVisible && !isFirst) {
            onFragmentVisibleChange(true);
        }
        return rootView;
    }

    /**
     * 获取布局文件
     *
     * @return
     */
    protected abstract int getLayoutResource();

    /**
     * 初始化view
     */
    protected abstract void initView();

    public void showShort(String msg) {
        try {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //解决在子线程中调用Toast的异常情况处理
            Looper.prepare();
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

    }

    public void toastShowLong(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isFragmentVisible = true;
        }
        if (rootView == null) {
            return;
        }
        //可见，并且没有加载过
        if (!isFirst && isFragmentVisible) {
            onFragmentVisibleChange(true);
            return;
        }
        //由可见——>不可见 已经加载过
        if (isFragmentVisible) {
            onFragmentVisibleChange(false);
            isFragmentVisible = false;
        }
    }

    /**
     * 当前fragment可见状态发生变化时会回调该方法
     * 如果当前fragment是第一次加载，等待onCreateView后才会回调该方法，其它情况回调时机跟 {@link #setUserVisibleHint(boolean)}一致
     * 在该回调方法中你可以做一些加载数据操作，甚至是控件的操作.
     *
     * @param isVisible true  不可见 -> 可见
     *                  false 可见  -> 不可见
     */
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        super.onDestroy();
    }
}
