package com.wya.env.module.netdata;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.DoodleConfig;
import com.wya.env.bean.doodle.NetModel;
import com.wya.env.bean.login.Lamps;
import com.wya.env.common.CommonValue;
import com.wya.env.module.home.detail.DetailActivity;
import com.wya.env.util.SaveSharedPreferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * @date: 2018/7/3 13:55
 * @author: Chunjiang Mao
 * @classname: NetDataFragment
 * @describe: Example Fragment
 */

public class NetDataFragment extends BaseMvpFragment<NetDataFragmentPresenter> implements NetDataFragmentView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerViewL;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    private NetDataModelAdapter adapter;

    int column;
    int size;
    int row;

    private int colorType;
    private DoodleConfig doodleConfig;
    private Lamps lamps;

    private boolean isChsoeDevice;
    private List<NetModel> data = new ArrayList<>();
    private NetDataFragmentPresenter netDataFragmentPresenter = new NetDataFragmentPresenter();
    private HashMap<String, String> hashMap;


    @Override
    protected int getLayoutResource() {
        return R.layout.more_fragment;
    }

    @Override
    protected void initView() {
        netDataFragmentPresenter.mView = this;
//        EventBus.getDefault().register(this);
        hashMap = new HashMap<>();
        doodleConfig = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.DOODLECONFIG), DoodleConfig.class);
        if (setType() && doodleConfig != null) {
            setRequest();
            initData();//初始化数据'
        } else {
            Toast.makeText(getActivity(), "Please connect device", Toast.LENGTH_SHORT).show();
        }


        adapter = new NetDataModelAdapter(getActivity(), R.layout.net_model_layout, data);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerViewL.setLayoutManager(gridLayoutManager);
        recyclerViewL.setAdapter(adapter);

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (setType() && doodleConfig != null) {
                    setRequest();
                    initData();//初始化数据'
                } else {
                    smartRefreshLayout.finishRefresh();
                    Toast.makeText(getActivity(), "Please connect device", Toast.LENGTH_SHORT).show();
                }
            }
        });
        smartRefreshLayout.setEnableLoadMore(false);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!TextUtils.isEmpty(data.get(position).getJson())) {
                    startActivity(new Intent(getActivity(), DetailActivity.class)
                            .putExtra("netType", 1)
                            .putExtra("netModel", (Serializable) data.get(position)));
                }
            }
        });
    }

    private int skip;
    private String take = "10";
    private int pilotType;
    private boolean hasDevice;

    private void setRequest() {
        if (doodleConfig != null) {
            hashMap.put("pilotType", pilotType + "");
            hashMap.put("pilotUUID", doodleConfig.getUID());
            hashMap.put("lampNum", size + "");
            hashMap.put("modelId", doodleConfig.getModeID());
            hashMap.put("oem2", doodleConfig.getOEM2());
            hashMap.put("oem1", doodleConfig.getOEM1());
            hashMap.put("take", take);
            hashMap.put("skip", skip + "");
        }
    }

    private boolean setType() {
        lamps = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LAMPS), Lamps.class);
        for (int i = 0; i < lamps.getLampSettings().size(); i++) {
            if (lamps.getLampSettings().get(i) != null && lamps.getLampSettings().get(i).getName() != null && lamps.getLampSettings().get(i).isChose()) {
                hasDevice = true;
                switch (lamps.getLampSettings().get(i).getName().substring(5, 6)) {
                    case "C":
                        pilotType = 0;
                        size = lamps.getLampSettings().get(i).getSize();
                        column = lamps.getLampSettings().get(i).getColumn();
                        break;
                    case "T":
                        pilotType = 1;
                        size = lamps.getLampSettings().get(i).getSize();
                        column = lamps.getLampSettings().get(i).getColumn();
                        break;
                    default:
                        break;
                }
                break;
            }
        }
        return hasDevice;
    }


    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
        netDataFragmentPresenter.mView = this;
        if (isVisible) {
            initData();//初始化数据
        }
    }

    private void initData() {
        skip = 0;
        hashMap.put("take", take);
        hashMap.put("skip", skip + "");
        netDataFragmentPresenter.getModels(hashMap);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isVisible()) {
//            initSendData();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (eventSendUpd != null) {
//            udpView.toStopSendUdpModeData(true, false);
//            eventSendUpd = null;
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onModelsResult(List<NetModel> data) {
        this.data = data;
        adapter.setNewData(data);
        adapter.notifyDataSetChanged();
        smartRefreshLayout.finishRefresh();
    }

    @Override
    public void onUpLoadModelResult() {

    }

    @Override
    public void onComplete() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }
}
