package com.wya.env.module.doodle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.listener.PickerViewListener;
import com.wya.env.module.home.fragment.HomeFragmentPresenter;
import com.wya.env.module.home.fragment.HomeFragmentView;
import com.wya.env.view.Circle;
import com.wya.env.view.ColorPickerView;
import com.wya.env.view.LampView;
import com.wya.uikit.button.WYAButton;
import com.wya.uikit.dialog.CustomListener;
import com.wya.uikit.dialog.WYACustomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @date: 2018/7/3 13:55
 * @author: Chunjiang Mao
 * @classname: Fragment1
 * @describe: Example Fragment
 */

public class DoodleFragment extends BaseMvpFragment<HomeFragmentPresenter> implements HomeFragmentView {


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
    @BindView(R.id.circle8)
    Circle circle8;
    @BindView(R.id.tab8)
    TableRow tab8;
    @BindView(R.id.tab_chose_color)
    TableRow tabChoseColor;
    Unbinder unbinder;
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
    private HomeFragmentPresenter fp = new HomeFragmentPresenter();

    private int color_index;
    private int chose_color;
    private int chose_light;
    /**
     * 0 不选中， 1 粗笔， 2 细笔
     */
    private int painter_type;

    /**
     * 是否闪烁
     */
    private boolean isTwinkle;

    private WYACustomDialog choseColorDialog;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick({R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4, R.id.tab5, R.id.tab6, R.id.tab7, R.id.tab8, R.id.tab_chose_color, R.id.ll_bold_paint, R.id.ll_thin_paint, R.id.ll_clean, R.id.ll_twinkle, R.id.ll_save})
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
            case R.id.tab8:
                color_index = 8;
                getColorIndex(color_index);
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
                lampView.clean();
                break;
            case R.id.ll_twinkle:
                isTwinkle = !isTwinkle;
                if (isTwinkle) {
                    imgTwinkle.setImageDrawable(this.getResources().getDrawable(R.drawable.sahnshuodianji));
                } else {
                    imgTwinkle.setImageDrawable(this.getResources().getDrawable(R.drawable.sahnshuomoren));
                }
                break;
            case R.id.ll_save:

                break;
            case R.id.tab_chose_color:
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
                                colorPickerView.setOnColorPickListener(new PickerViewListener() {
                                    @Override
                                    public void onPickerColor(int color) {
                                        chose_color = color;
                                        circle.setColor(chose_color);
                                    }
                                });
                                sure.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        lampView.setChoseColor(chose_color);
                                        color_index = 0;
                                        getColorIndex(color_index);
                                        choseColorDialog.dismiss();
                                    }
                                });

                                SeekBar mSeekBar = (SeekBar) v.findViewById(R.id.seekbar);

                                mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        chose_light = progress;
                                        tvLight.setText(chose_light + "");
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {
                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        lampView.setChoseLight(chose_light);
                                    }
                                });
                            }
                        })
                        .build();
                choseColorDialog.show();
                break;
            default:
                break;
        }
    }

    private void setPainter(int painter_type) {
        imgBoldPainter.setImageDrawable(this.getResources().getDrawable(R.drawable.cubimoren));
        imgThinPainter.setImageDrawable(this.getResources().getDrawable(R.drawable.xibimoren));
        if (painter_type == 1) {
            imgBoldPainter.setImageDrawable(this.getResources().getDrawable(R.drawable.cubidianji));
        } else if (painter_type == 2) {
            imgThinPainter.setImageDrawable(this.getResources().getDrawable(R.drawable.xibidianji));
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
        circle8.setCircle_chose(false);
        switch (color_index) {
            case 1:
                chose_color = getActivity().getResources().getColor(R.color.cEA1318);
                circle1.setCircle_chose(true);
                break;
            case 2:
                circle2.setCircle_chose(true);
                chose_color = getActivity().getResources().getColor(R.color.cF69218);
                break;
            case 3:
                circle3.setCircle_chose(true);
                chose_color = getActivity().getResources().getColor(R.color.cF2E93F);
                break;
            case 4:
                circle4.setCircle_chose(true);
                chose_color = getActivity().getResources().getColor(R.color.c6BBA2B);
                break;
            case 5:
                circle5.setCircle_chose(true);
                chose_color = getActivity().getResources().getColor(R.color.c68C7DD);
                break;
            case 6:
                circle6.setCircle_chose(true);
                chose_color = getActivity().getResources().getColor(R.color.c1A489E);
                break;
            case 7:
                circle7.setCircle_chose(true);
                chose_color = getActivity().getResources().getColor(R.color.cB04F9C);
                break;
            case 8:
                circle8.setCircle_chose(true);
                chose_color = getActivity().getResources().getColor(R.color.white);
                break;
            default:
                break;
        }
        lampView.setChoseColor(chose_color);
    }
}
