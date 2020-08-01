package com.wya.env.module.home.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.Doodle;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.view.LampView;

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

    private List<DoodlePattern> doodlePatterns = new ArrayList<>();

    private HomeFragmentPresenter fp = new HomeFragmentPresenter();

    private int listSize = 31;

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
      /*  fp.mView=this;
        if (isVisible) {
            initData();//初始化数据
        }*/
    }

    private void initData() {
        initListData();
        initRecyclerView();
    }

    private void initListData() {
        doodlePatterns.clear();
        for (int i = 0; i < listSize; i++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            List<Doodle> doodles = new ArrayList<>();
            for (int j = 0; j < 300; j++) {
                Doodle doodle = new Doodle();
                switch (i % 5) {
                    case 0:
                        doodle.setColor(getActivity().getResources().getColor(R.color.app_blue_press));
                        break;
                    case 1:
                        doodle.setColor(getActivity().getResources().getColor(R.color.red));
                        break;
                    case 2:
                        doodle.setColor(getActivity().getResources().getColor(R.color.green));
                        break;
                    case 3:
                        doodle.setColor(getActivity().getResources().getColor(R.color.blue));
                        break;
                    case 4:
                        doodle.setColor(getActivity().getResources().getColor(R.color.c999999));
                        break;
                    default:
                        doodle.setColor(getActivity().getResources().getColor(R.color.black));
                        break;
                }
                doodle.setLight(255);
                doodles.add(doodle);
            }
            doodlePattern.setDoodles(doodles);
            doodlePattern.setName("模式" + i);
            doodlePatterns.add(doodlePattern);
        }

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
}
