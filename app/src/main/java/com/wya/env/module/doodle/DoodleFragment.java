package com.wya.env.module.doodle;

import android.text.TextUtils;
import android.view.View;
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
import com.wya.env.bean.doodle.ModeArr;
import com.wya.env.bean.doodle.UserInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.listener.PickerViewListener;
import com.wya.env.module.home.fragment.HomeFragmentPresenter;
import com.wya.env.module.home.fragment.HomeFragmentView;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.Circle;
import com.wya.env.view.ColorPickerView;
import com.wya.env.view.LampView;
import com.wya.uikit.button.WYAButton;
import com.wya.uikit.dialog.CustomListener;
import com.wya.uikit.dialog.WYACustomDialog;
import com.wya.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.img_mirror)
    ImageView imgMirror;
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

    /**
     * 镜像
     */
    private boolean isMirror;

    private WYACustomDialog choseColorDialog;

    private UserInfo userInfo;
    private List<DoodlePattern> doodlePatterns = new ArrayList<>();


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
        getData();
    }

    private void getData() {
        userInfo = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.USER_INFO), UserInfo.class);
        if (userInfo.getDoodlePatterns() == null) {
            userInfo.setDoodlePatterns(new ArrayList<>());
        }
        doodlePatterns = userInfo.getDoodlePatterns();

        ModeArr modeArr = new Gson().fromJson(data_str, ModeArr.class);
    }

//    private void dealData() {
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = new JSONObject(data_str);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        //通过迭代器获取这段json当中所有的key值
//        Iterator keys = jsonObject.keys();
//        //然后通过一个循环取出所有的key值
//        while (keys.hasNext()) {
//            String key = String.valueOf(keys.next());
//            String
//            LogUtil.e(key);
//            //最后就可以通过刚刚得到的key值去解析后面的json了
//        }
//    }

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

    @OnClick({R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4, R.id.tab5, R.id.tab6, R.id.tab7, R.id.tab8, R.id.tab_chose_color, R.id.ll_bold_paint, R.id.ll_thin_paint, R.id.ll_clean, R.id.ll_twinkle, R.id.ll_save, R.id.ll_mirror})
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
                lampView.setTwinkle(isTwinkle);
                if (isTwinkle) {
                    imgTwinkle.setImageDrawable(this.getResources().getDrawable(R.drawable.sahnshuodianji));
                } else {
                    imgTwinkle.setImageDrawable(this.getResources().getDrawable(R.drawable.sahnshuomoren));
                }
                break;
            case R.id.ll_mirror:
                isMirror = !isMirror;
                lampView.setMirror(isMirror);
                if (isMirror) {
                    imgMirror.setImageDrawable(this.getResources().getDrawable(R.drawable.sahnshuodianji_15));
                } else {
                    imgMirror.setImageDrawable(this.getResources().getDrawable(R.drawable.baocunmoren));
                }
                break;
            case R.id.ll_save:
                if (TextUtils.isEmpty(etName.getText().toString())) {
                    showShort("请输入模式名称");
                    return;
                }
                toSave();
                toCleanChose();
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

    private void toCleanChose() {
        lampView.clean();
        color_index = 0;
        getColorIndex(color_index);
        isTwinkle = false;
        lampView.setTwinkle(isTwinkle);
        if (isTwinkle) {
            imgTwinkle.setImageDrawable(this.getResources().getDrawable(R.drawable.sahnshuodianji));
        } else {
            imgTwinkle.setImageDrawable(this.getResources().getDrawable(R.drawable.sahnshuomoren));
        }
        painter_type = 0;
        setPainter(painter_type);
        etName.setText("");
    }

    private void toSave() {
        getData();
        DoodlePattern doodlePattern = new DoodlePattern();
        doodlePattern.setDoodles(lampView.getData());
        doodlePattern.setName(etName.getText().toString());
        doodlePatterns.add(doodlePattern);
        SaveSharedPreferences.save(getActivity(), CommonValue.USER_INFO, new Gson().toJson(userInfo));
        showShort("保存成功");
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


    private String data_str = "{\n" +
            "    \"modeArr\": [\n" +
            "        {\n" +
            "            \"lightRow\": 15,\n" +
            "            \"light_status\": {\n" +
            "                \"136\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"91\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"181\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"271\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"226\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"166\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"272\": {\n" +
            "                    \"color\": \"#000000\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"256\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"1\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"196\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"121\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"257\": {\n" +
            "                    \"color\": \"#000000\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"211\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"286\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"151\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"106\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"16\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"287\": {\n" +
            "                    \"color\": \"#000000\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"76\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"31\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"61\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"241\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"46\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                }\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"lightRow\": 15,\n" +
            "            \"light_status\": {\n" +
            "                \"77\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"121\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"3\": {\n" +
            "                    \"color\": \"#000000\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"226\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"167\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"122\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"31\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"227\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"136\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"32\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"271\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"16\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"137\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"17\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"272\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"61\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"181\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"106\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"62\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"286\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"241\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"46\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"107\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"182\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"287\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"47\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"196\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"242\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"91\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"151\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"256\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"197\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"92\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"211\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"152\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"76\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"257\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"1\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"166\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"2\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                },\n" +
            "                \"212\": {\n" +
            "                    \"color\": \"#69BB2B\",\n" +
            "                    \"isFlash\": 0\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "\t]\n" +
            "}";
}
