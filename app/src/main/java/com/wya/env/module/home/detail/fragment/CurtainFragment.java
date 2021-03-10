package com.wya.env.module.home.detail.fragment;

import android.annotation.SuppressLint;

import com.wya.env.R;
import com.wya.env.base.BaseLazyFragment;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.event.EventSendUpd;
import com.wya.env.common.CommonValue;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.LampView;

import org.greenrobot.eventbus.EventBus;

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

    private int colorType;

    public CurtainFragment(LampModel model, int colorType) {
        this.model = model;
        this.colorType = colorType;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.curtain_fragment;
    }

    @Override
    protected void initView() {
        curtain.setColorType(colorType);
        curtain.setSize(model.getSize());
        curtain.setColumn(model.getColumn());
        curtain.setShape(0);
        curtain.requestLayout();
        curtain.setModelName(model.getName());
        curtain.setMirror(model.getMirror());
        curtain.setSpeed(model.getSpeed());
        curtain.setModel(model.getModeArr(), model.getLight(), false);
        setUdp();
    }

    private void setUdp() {
        EventSendUpd eventSendUpd = new EventSendUpd();
        eventSendUpd.setLampModel(model);
        EventBus.getDefault().post(eventSendUpd);
    }

    public void setSpeed(int speed) {
        this.model.setSpeed(speed);
        curtain.setSpeed(speed);
        setUdp();
    }

    public void setLampModel(LampModel mLampModel) {
        this.model = mLampModel;
        curtain.setModel(model.getModeArr(), model.getLight(), false);
        setUdp();
    }
}
