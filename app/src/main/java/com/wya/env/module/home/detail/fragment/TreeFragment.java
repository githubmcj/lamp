package com.wya.env.module.home.detail.fragment;

import android.annotation.SuppressLint;

import com.easysocket.utils.LogUtil;
import com.wya.env.R;
import com.wya.env.base.BaseLazyFragment;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.event.EventSendUpd;
import com.wya.env.common.CommonValue;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.TreeView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

@SuppressLint("ValidFragment")
public class TreeFragment extends BaseLazyFragment {
    @BindView(R.id.tree)
    TreeView tree;
    private LampModel model;

    private int colorType;
    private boolean toShow;


    public TreeFragment(LampModel model, int colorType, boolean toShow) {
        this.model = model;
        this.toShow = toShow;
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
        tree.setModel(model.getModeArr(), model.getLight(), toShow);
        tree.requestLayout();
        setUdp();
    }

    private void setUdp() {
        if(!toShow){
            EventSendUpd eventSendUpd = new EventSendUpd();
            eventSendUpd.setLampModel(model);
            EventBus.getDefault().post(eventSendUpd);
        }
    }


    public void setSpeed(int speed) {
        this.model.setSpeed(speed);
        tree.setSpeed(speed);
        setUdp();
    }


    public void setLampModel(LampModel mLampModel) {
        this.model = mLampModel;
        tree.setModel(model.getModeArr(), model.getLight(), toShow);
        setUdp();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(toShow){
            stop();
        }
    }

    private void stop(){
        tree.toStopSendUdpModeData(true, false);
        tree.stopSendUdpData();
    }
}
