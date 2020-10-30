package com.wya.env.module.doodle;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.util.ColorUtil;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.Circle;
import com.wya.env.view.LampView;
import com.wya.uikit.button.WYAButton;
import com.wya.uikit.dialog.CustomListener;
import com.wya.uikit.dialog.WYACustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;

/**
 * @date: 2018/7/3 13:55
 * @author: Chunjiang Mao
 * @classname: Fragment1
 * @describe: Example Fragment
 */

public class DoodleFragment extends BaseMvpFragment<DoodleFragmentPresenter> implements DoodleFragmentView {
    @BindView(R.id.circle1)
    Circle circle1;
    @BindView(R.id.tab1)
    TableRow tab1;
    @BindView(R.id.circle2)
    Circle circle2;
    @BindView(R.id.tab2)
    TableRow tab2;
    @BindView(R.id.circle3)
    Circle circle3;
    @BindView(R.id.tab3)
    TableRow tab3;
    @BindView(R.id.circle4)
    Circle circle4;
    @BindView(R.id.tab4)
    TableRow tab4;
    @BindView(R.id.circle5)
    Circle circle5;
    @BindView(R.id.tab5)
    TableRow tab5;
    @BindView(R.id.circle6)
    Circle circle6;
    @BindView(R.id.tab6)
    TableRow tab6;
    @BindView(R.id.circle7)
    Circle circle7;
    @BindView(R.id.tab7)
    TableRow tab7;
    @BindView(R.id.img_add)
    ImageView img_add;
    @BindView(R.id.tab_add)
    TableRow tabAdd;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.lamp_view)
    LampView lampView;
    @BindView(R.id.ll_bold_paint)
    LinearLayout llBoldPaint;
    @BindView(R.id.ll_thin_paint)
    LinearLayout llThinPaint;
    @BindView(R.id.ll_clean)
    LinearLayout llClean;
    @BindView(R.id.ll_twinkle)
    LinearLayout llTwinkle;
    @BindView(R.id.ll_save)
    LinearLayout llDelete;
    @BindView(R.id.img_bold_painter)
    ImageView imgBoldPainter;
    @BindView(R.id.img_thin_painter)
    ImageView imgThinPainter;
    @BindView(R.id.img_twinkle)
    ImageView imgTwinkle;
    @BindView(R.id.img_clean)
    ImageView imgClean;
    @BindView(R.id.img_mirror)
    ImageView imgMirror;
    @BindView(R.id.img_del)
    ImageView imgDel;
    @BindView(R.id.tab_del)
    TableRow tabDel;
    @BindView(R.id.tab_mirror)
    TableRow tabMirror;
    Unbinder unbinder;
    @BindView(R.id.img_all)
    ImageView imgAll;
    @BindView(R.id.tab_all)
    TableRow tabAll;
    Unbinder unbinder1;
    private DoodleFragmentPresenter doodleFragmentPresenter = new DoodleFragmentPresenter();

    private int color_index;
    private String chose_color;
    private String show_color;
    private String picker_chose_color;
    private int chose_light;
    /**
     * 0 不选中， 1 粗笔， 2 细笔  3 擦除
     */
    private int painter_type;

    /**
     * 是否闪烁
     */
    private boolean isTwinkle;

    /**
     * 是否镜像
     */
    private int isMirror;

    private WYACustomDialog choseColorDialog;

    private LoginInfo loginInfo;
    private List<LampModel> lampModels = new ArrayList<>();

    int column;
    int size;
    int row;

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
      /*  fp.mView=this;
        if (isVisible) {
            initData();//初始化数据
        }*/
    }

    private void initData() {
        chose_light = 100;
        lampView.setChoseColor(chose_color);
        lampView.setShowColor(show_color);
        //        if (!isFirst) {
//        initListData();
//        getData();

    }


//    private void getData() {
//        loginInfo = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LOGIN_INFO), LoginInfo.class);
//        if (loginInfo.getLampModels() == null) {
//            loginInfo.setLampModels(new ArrayList<>());
//        }
//        lampModels = loginInfo.getLampModels();
//    }
//
//    private void initListData() {
//    }


    @Override
    protected int getLayoutResource() {
        return R.layout.doodle_fragment;
    }

    @Override
    protected void initView() {
        doodleFragmentPresenter.mView = this;
        initData();//初始化数据
    }

    @OnClick({R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4, R.id.tab5, R.id.tab6, R.id.tab7, R.id.tab_add, R.id.ll_bold_paint, R.id.ll_thin_paint, R.id.ll_clean, R.id.ll_twinkle, R.id.ll_save, R.id.img_mirror, R.id.img_del, R.id.img_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tab1:
                color_index = 1;
                getColorIndex(color_index);
                break;
            case R.id.tab2:
                color_index = 2;
                getColorIndex(color_index);
                break;
            case R.id.tab3:
                color_index = 3;
                getColorIndex(color_index);
                break;
            case R.id.tab4:
                color_index = 4;
                getColorIndex(color_index);
                break;
            case R.id.tab5:
                color_index = 5;
                getColorIndex(color_index);
                break;
            case R.id.tab6:
                color_index = 6;
                getColorIndex(color_index);
                break;
            case R.id.tab7:
                color_index = 7;
                getColorIndex(color_index);
                break;
            case R.id.tab_add:
                choseColorDialog = new WYACustomDialog.Builder(getActivity())
                        .title("")
                        .message("")
                        .cancelable(true)
                        .cancelTouchout(true)
                        .setLayoutId(R.layout.chose_color_layout, new CustomListener() {
                            @Override
                            public void customLayout(View v) {
                                WYAButton sure = v.findViewById(R.id.sure);
                                ColorPickerView colorPickerView = v.findViewById(R.id.picker_view);
                                TextView tvLight = v.findViewById(R.id.tv_light);
                                Circle circle = v.findViewById(R.id.circle);
                                if (picker_chose_color == null) {
                                    picker_chose_color = "#ffffff";
                                }
                                chose_color = circle.getColor(picker_chose_color, chose_light);
                                tvLight.setText(chose_light + "");
                                colorPickerView.setInitialColor(Color.rgb(ColorUtil.int2Rgb(Color.parseColor(picker_chose_color))[0], ColorUtil.int2Rgb(Color.parseColor(picker_chose_color))[1], ColorUtil.int2Rgb(Color.parseColor(picker_chose_color))[2]));
                                show_color = circle.getShowColor(picker_chose_color, chose_light);
                                colorPickerView.subscribe(new ColorObserver() {
                                    @Override
                                    public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                                        picker_chose_color = String.format("#%06X", (0xFFFFFF & color));
                                        chose_color = circle.getColor(picker_chose_color, chose_light);
                                        show_color = circle.getShowColor(picker_chose_color, chose_light);
                                    }
                                });
                                sure.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        lampView.setChoseColor(chose_color);
                                        lampView.setShowColor(show_color);
                                        color_index = 0;
                                        getColorIndex(color_index);
                                        choseColorDialog.dismiss();
                                    }
                                });

                                SeekBar mSeekBar = (SeekBar) v.findViewById(R.id.seekbar);
                                mSeekBar.setProgress(chose_light);
                                mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        chose_light = progress;
                                        tvLight.setText(chose_light + "");
                                        chose_color = circle.getColor(picker_chose_color, chose_light);
                                        show_color = circle.getShowColor(picker_chose_color, chose_light);
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {
                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        lampView.setChoseColor(chose_color);
                                        lampView.setShowColor(show_color);
                                    }
                                });
                            }
                        })
                        .build();
                choseColorDialog.show();
                break;
            case R.id.ll_bold_paint:
                if (painter_type == 1) {
                    painter_type = 0;
                } else {
                    painter_type = 1;
                }
                lampView.setPaintBold(true);
                setPainter(painter_type);
                break;
            case R.id.ll_thin_paint:
                lampView.setPaintBold(false);
                if (painter_type == 2) {
                    painter_type = 0;
                } else {
                    painter_type = 2;
                }
                setPainter(painter_type);
                break;
            case R.id.ll_clean:
                if (painter_type == 3) {
                    painter_type = 0;
                } else {
                    painter_type = 3;
                }
                lampView.setPaintBold(false);
                setPainter(painter_type);
                break;
            case R.id.ll_twinkle:
                isTwinkle = !isTwinkle;
                lampView.setTwinkle(isTwinkle);
                if (isTwinkle) {
                    imgTwinkle.setImageDrawable(this.getResources().getDrawable(R.drawable.sahnshuodianji));
                } else {
                    imgTwinkle.setImageDrawable(this.getResources().getDrawable(R.drawable.sahnshuomoren));
                }
                break;
            case R.id.img_mirror:
                isMirror = 1 - isMirror;
                if (isMirror == 1) {
                    imgMirror.setImageDrawable(this.getResources().getDrawable(R.drawable.mirror_right));
                } else {
                    imgMirror.setImageDrawable(this.getResources().getDrawable(R.drawable.mirror_lef));
                }
                lampView.setMirror(isMirror);
                break;
            case R.id.img_all:
                lampView.setAllColor(chose_color);
                break;
            case R.id.ll_save:
                if (TextUtils.isEmpty(etName.getText().toString())) {
                    showShort("please enter mode name");
                    return;
                }
                if (!getIpAddressString().contains("192.168.4.")) {
                    toSave();
                } else {
                    showShort("Network not available");
                }
                break;
            case R.id.img_del:
                toCleanChose();
                break;
            default:
                break;
        }
    }

    /**
     * @return 本机ip地址
     */
    private String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }


    private void toCleanChose() {
        lampView.clean();
        color_index = 0;
        chose_color = null;
        lampView.setChoseColor(chose_color);
        lampView.setShowColor(chose_color);
        getColorIndex(color_index);
        isTwinkle = false;
        lampView.setTwinkle(isTwinkle);
        if (isTwinkle) {
            imgTwinkle.setImageDrawable(this.getResources().getDrawable(R.drawable.sahnshuodianji));
        } else {
            imgTwinkle.setImageDrawable(this.getResources().getDrawable(R.drawable.sahnshuomoren));
        }
        isMirror = 0;
        lampView.setMirror(isMirror);
        if (isMirror == 1) {
            imgMirror.setImageDrawable(this.getResources().getDrawable(R.drawable.mirror_right));
        } else {
            imgMirror.setImageDrawable(this.getResources().getDrawable(R.drawable.mirror_lef));
        }
        painter_type = 0;
        setPainter(painter_type);
        etName.setText("");
    }

    private void toSave() {
        LampModel lampModel = new LampModel();
        lampModel.setName(etName.getText().toString());
        DoodlePattern doodlePattern = new DoodlePattern();
        doodlePattern.setLight_status(lampView.getSaveData());
        doodlePattern.setSize(lampView.getSize());
        List<DoodlePattern> doodlePatterns = new ArrayList<>();
        doodlePatterns.add(doodlePattern);
        lampModel.setModeArr(doodlePatterns);
        lampModel.setLight(chose_light);
        lampModel.setSize(lampView.getSize());
        lampModel.setLightRow(lampView.getSize() / lampView.getColumn());
        lampModel.setColumn(lampView.getColumn());
        doodleFragmentPresenter.saveModel(new Gson().toJson(lampModel));
    }

    private void setPainter(int painter_type) {
        imgBoldPainter.setImageDrawable(this.getResources().getDrawable(R.drawable.cubimoren));
        imgThinPainter.setImageDrawable(this.getResources().getDrawable(R.drawable.xibimoren));
        imgClean.setImageDrawable(this.getResources().getDrawable(R.drawable.cachumoren));
        if (painter_type == 1) {
            imgBoldPainter.setImageDrawable(this.getResources().getDrawable(R.drawable.cubidianji));
            lampView.setChoseColor(chose_color);
            lampView.setShowColor(chose_color);
        } else if (painter_type == 2) {
            imgThinPainter.setImageDrawable(this.getResources().getDrawable(R.drawable.xibidianji));
            lampView.setChoseColor(chose_color);
            lampView.setShowColor(chose_color);
        } else if (painter_type == 3) {
            imgClean.setImageDrawable(this.getResources().getDrawable(R.drawable.cachudianji));
            lampView.setChoseColor("#000000");
            lampView.setShowColor("#000000");
        } else {
            lampView.setPaintBold(false);
        }
    }


    private void getColorIndex(int color_index) {
        circle1.setCircle_chose(false);
        circle2.setCircle_chose(false);
        circle3.setCircle_chose(false);
        circle4.setCircle_chose(false);
        circle5.setCircle_chose(false);
        circle6.setCircle_chose(false);
        circle7.setCircle_chose(false);
        switch (color_index) {
            case 1:
                chose_color = "#EA1318";
                circle1.setCircle_chose(true);
                break;
            case 2:
                circle2.setCircle_chose(true);
                chose_color = "#F69218";
                break;
            case 3:
                circle3.setCircle_chose(true);
                chose_color = "#F2E93F";
                break;
            case 4:
                circle4.setCircle_chose(true);
                chose_color = "#6BBA2B";
                break;
            case 5:
                circle5.setCircle_chose(true);
                chose_color = "#68C7DD";
                break;
            case 6:
                circle6.setCircle_chose(true);
                chose_color = "#1A489E";
                break;
            case 7:
                circle7.setCircle_chose(true);
                chose_color = "#B04F9C";
                break;
            default:
                break;
        }
        lampView.setChoseColor(chose_color);
        lampView.setShowColor(chose_color);
        if (painter_type == 0 || painter_type == 3) {
            lampView.setPaintBold(false);
            painter_type = 2;
            setPainter(painter_type);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if (isVisible()) {
            lampView.startSendUpdData();
            lampView.startTwinkle();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        lampView.stopSendUdpData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LampSetting lampSetting) {
        lampView.setSize(size);
        lampView.setColumn(column);
        lampView.requestLayout();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            lampView.startSendUpdData();
            lampView.startTwinkle();
        } else {
            lampView.toStopSendUdpData(true);
            lampView.stopTwinkle();
            toCleanChose();
        }
    }


    @Override
    public void onSaveResult() {
        toCleanChose();
        showShort(getActivity().getResources().getString(R.string.save_success));
        SaveSharedPreferences.save(getActivity(), CommonValue.TO_REFRESH, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }
}
