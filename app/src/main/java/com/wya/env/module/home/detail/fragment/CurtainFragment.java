package com.wya.env.module.home.detail.fragment;

import android.annotation.SuppressLint;

import com.wya.env.R;
import com.wya.env.base.BaseLazyFragment;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.common.CommonValue;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.LampView;

import butterknife.BindView;

/**
 * @date: 2020/11/4 17:09
 * @author: Chunjiang Mao
 * @classname: CurtainFragment
 * @describe:
 */
@SuppressLint("ValidFragment")
public class CurtainFragment extends BaseLazyFragment {

    @BindView(R.id.curtain)
    LampView curtain;
    private LampModel model;

    public CurtainFragment(LampModel model) {
        this.model = model;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.curtain_fragment;
    }

    @Override
    protected void initView() {
        curtain.setColorType(SaveSharedPreferences.getInt(getActivity(), CommonValue.COLOR_TYPE));
        curtain.setSize(model.getSize());
        curtain.setColumn(model.getColumn());
        curtain.setShape(0);
        curtain.requestLayout();
        curtain.setModelName(model.getName());
        curtain.setMirror(model.getMirror());
        curtain.setSpeed(model.getSpeed());
        curtain.setModel(model.getModeArr(), model.getLight(), true);
    }

    @Override
    public void onStart() {
        super.onStart();
        curtain.setMirror(model.getMirror());
        curtain.setModel(model.getModeArr(), model.getLight(), true);
    }


    @Override
    public void onStop() {
        super.onStop();
        curtain.toStopSendUdpModeData(true, false);
        curtain.stopSendUdpData();
    }

    public void setSpeed(int speed) {
        curtain.setSpeed(speed);
    }

    public void setLampModel(LampModel mLampModel) {
        this.model = mLampModel;
        curtain.setModel(model.getModeArr(), model.getLight(), true);
    }
}
