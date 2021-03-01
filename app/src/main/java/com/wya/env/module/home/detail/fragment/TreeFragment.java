package com.wya.env.module.home.detail.fragment;

import android.annotation.SuppressLint;

import com.wya.env.R;
import com.wya.env.base.BaseLazyFragment;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.common.CommonValue;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.TreeView;

import butterknife.BindView;

@SuppressLint("ValidFragment")
public class TreeFragment extends BaseLazyFragment {
    @BindView(R.id.tree)
    TreeView tree;
    private LampModel model;

    private int colorType;


    public TreeFragment(LampModel model, int colorType) {
        this.model = model;
        this.colorType = colorType;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.tree_fragment;
    }

    @Override
    protected void initView() {
        tree.setColorType(colorType);
        tree.setSize(model.getSize());
        tree.setColumn(model.getColumn());
        tree.setModelName(model.getName());
        tree.setMirror(model.getMirror());
        tree.setSpeed(model.getSpeed());
        tree.setModel(model.getModeArr(), model.getLight(), true);
        tree.requestLayout();

//        setTcpData(model.getModeArr());
    }

    @Override
    public void onStart() {
        super.onStart();
        tree.setMirror(model.getMirror());
        tree.setModel(model.getModeArr(), model.getLight(), true);
    }


    @Override
    public void onStop() {
        super.onStop();
        tree.toStopSendUdpModeData(true, true);
        tree.stopSendUdpData();
    }


    public void setSpeed(int speed) {
        tree.setSpeed(speed);
    }

    public void setLampModel(LampModel mLampModel) {
        this.model = mLampModel;
        tree.setModel(model.getModeArr(), model.getLight(), true);
    }
}
