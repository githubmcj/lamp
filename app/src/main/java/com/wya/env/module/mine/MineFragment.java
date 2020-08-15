package com.wya.env.module.mine;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.module.login.LoginActivity;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.AvatarImageView;
import com.wya.utils.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @date: 2018/7/3 13:55
 * @author: Chunjiang Mao
 * @classname: Fragment2
 * @describe: Example Fragment
 */
public class MineFragment extends BaseMvpFragment<MineFragmentPresenter> implements MineFragmentView {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.avatar)
    AvatarImageView avatar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tab_refresh)
    TableRow tabRefresh;
    @BindView(R.id.tab_about_us)
    TableRow tabAboutUs;
    @BindView(R.id.tab_exit)
    TableRow tabExit;

    private List<LampSetting> lampSettings = new ArrayList<>();
    private int listSize = 10;
    private MyLampAdapter myLampAdapter;
    private LoginInfo loginInfo;

    @Override
    protected int getLayoutResource() {
        return R.layout.two_fragment;
    }

    private void initData() {
        initListData();
        initUserInfo();
        initRecyclerView();
    }

    private void initUserInfo() {
        loginInfo = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LOGIN_INFO), LoginInfo.class);
        userName.setText(loginInfo.getUserName());
        email.setText(loginInfo.getUserEmail());
    }

    private void initListData() {
        lampSettings.clear();
        for (int i = 0; i < listSize; i++) {
            LampSetting lampSetting = new LampSetting();
            lampSetting.setName("设备" + i);
            lampSettings.add(lampSetting);
        }

    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        myLampAdapter = new MyLampAdapter(getActivity(), R.layout.lamp_setting_item, lampSettings);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, ScreenUtil.dip2px(getContext(), 10), true));
        recyclerView.setAdapter(myLampAdapter);
    }


    @Override
    protected void initView() {
        Glide.with(getActivity()).load("").apply(new RequestOptions().placeholder(R.drawable.avatar).error(R.drawable.avatar)).into(avatar);
        initData();
    }


    @OnClick({R.id.tab_refresh, R.id.tab_about_us, R.id.tab_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tab_refresh:
                showShort("刷新");
                break;
            case R.id.tab_exit:
                toExit();
                break;
            case R.id.tab_about_us:
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
            default:
                break;
        }
    }

    private void toExit() {
        SaveSharedPreferences.save(getActivity(), CommonValue.IS_LOGIN, false);
        SaveSharedPreferences.save(getActivity(), CommonValue.TOKEN, "");
        SaveSharedPreferences.save(getActivity(), CommonValue.LOGIN_INFO, "");
        if (!ActivityManager.getInstance().leaveFirstActivity()){
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
}
