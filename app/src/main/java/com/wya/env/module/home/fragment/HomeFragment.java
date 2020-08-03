package com.wya.env.module.home.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.bean.doodle.UserInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.LampView;
import com.wya.utils.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @date: 2018/7/3 13:55
 * @author: Chunjiang Mao
 * @classname: Fragment1
 * @describe: Example Fragment
 */

public class HomeFragment extends BaseMvpFragment<HomeFragmentPresenter> implements HomeFragmentView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.lamp_view)
    LampView lampView;
    @BindView(R.id.name)
    TextView name;
    private LampModelAdapter adapter;

    private UserInfo userInfo;
    private List<DoodlePattern> doodlePatterns = new ArrayList<>();

    private HomeFragmentPresenter fp = new HomeFragmentPresenter();

    private int listSize = 31;

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
        fp.mView = this;
        LogUtil.e(isVisible + "------------");
        if (isVisible) {
            initData();//初始化数据
        }
    }

    private void initData() {
        getData();
        initRecyclerView();
    }

    private void getData() {
        userInfo = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.USER_INFO), UserInfo.class);
        if (userInfo.getDoodlePatterns() == null) {
            userInfo.setDoodlePatterns(new ArrayList<>());
        }
        doodlePatterns = userInfo.getDoodlePatterns();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new LampModelAdapter(getActivity(), R.layout.lamp_pattern_item, doodlePatterns);
        recyclerView.setAdapter(adapter);
        //RecyclerView条目点击事件
        adapter.setOnItemClickListener((adapter, view, position) -> {
            name.setText(doodlePatterns.get(position).getName());
            for (int i = 0; i < doodlePatterns.size(); i++) {
                doodlePatterns.get(i).setChose(false);
                lampView.setData(doodlePatterns.get(position).getDoodles());
            }
            doodlePatterns.get(position).setChose(true);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.home_fragment;
    }

    @Override
    protected void initView() {
        fp.mView = this;
        lampView.setFocusable(false);
        initData();//初始化数据
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getData();
            if (adapter != null) {
                adapter.setNewData(doodlePatterns);
            }
        }
    }
}
