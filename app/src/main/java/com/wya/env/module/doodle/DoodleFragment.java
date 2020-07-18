package com.wya.env.module.doodle;

import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.module.home.fragment.Fragment1Presenter;
import com.wya.env.module.home.fragment.Fragment1View;
import com.wya.utils.utils.ScreenUtil;

/**
 * @date: 2018/7/3 13:55
 * @author: Chunjiang Mao
 * @classname: Fragment1
 * @describe: Example Fragment
 */

public class DoodleFragment extends BaseMvpFragment<Fragment1Presenter> implements Fragment1View {


    private Fragment1Presenter fp = new Fragment1Presenter();

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
      /*  fp.mView=this;
        if (isVisible) {
            initData();//初始化数据
        }*/
    }

    private void initData() {
        //        if (!isFirst) {
        initListData();
    }

    private void initListData() {
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.doodle_fragment;
    }

    @Override
    protected void initView() {
        fp.mView = this;
        initData();//初始化数据
    }

}
