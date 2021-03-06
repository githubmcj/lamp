package com.wya.env.module.home.fragment;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wya.env.R;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.home.MusicModel;
import com.wya.env.view.LampView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @date: 2018/6/29 13:55
 * @author: Chunjiang Mao
 * @classname: DataAdapter
 * @describe: 灯光模式
 */

public class LampModelAdapter extends BaseQuickAdapter<LampModel, BaseViewHolder> {

    private Context context;
    private List<LampModel> data;


    public LampModelAdapter(Context context, int layoutResId, @Nullable List<LampModel> data) {
        super(layoutResId, data);
        this.context = context;
        this.data = data;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void convert(BaseViewHolder helper, LampModel item) {
        if (item.getNetModel() != null && !TextUtils.isEmpty(item.getNetModel().getName()) && !TextUtils.isEmpty(item.getNetModel().getThumbnail())) {
            helper.setVisible(R.id.ll_netmodel, true);
            helper.setText(R.id.tv_name, item.getName());
            Glide.with(context).load(item.getNetModel().getThumbnail()).into((ImageView) helper.getView(R.id.img));
        } else {
            helper.setVisible(R.id.ll_netmodel, false);
            if (item.getName() == null) {
                helper.setGone(R.id.ll_add, true);
                helper.setGone(R.id.ll_custom, false);
            } else if (item.getModeType() == 1) {
                helper.setGone(R.id.ll_custom, true);
                helper.setGone(R.id.ll_add, false);
                helper.setText(R.id.tv_custom, item.getName());
                if (item.isChose() == 1) {
                    MusicModel musicModel = new MusicModel();
                    musicModel.setPosition(helper.getAdapterPosition());
                    musicModel.setMusic(item.isMusic());
                    musicModel.setClick(false);
                    EventBus.getDefault().post(musicModel);
                }
                if (item.isChose() == 1) {
                    helper.getView(R.id.ll_custom).setBackground(context.getResources().getDrawable(R.drawable.lamp_pattern_chose_bg));
                } else {
                    helper.getView(R.id.ll_custom).setBackground(context.getResources().getDrawable(R.drawable.lamp_pattern_normal_bg));
                }
                if (item.isMusic() == 1) {
                    ((ImageView) helper.getView(R.id.img_music)).setImageDrawable(context.getResources().getDrawable(R.drawable.yinyueshibie));
                } else {
                    ((ImageView) helper.getView(R.id.img_music)).setImageDrawable(context.getResources().getDrawable(R.drawable.yinyuemoren));
                }
                helper.getView(R.id.img_music).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.isChose() == 1) {
                            MusicModel musicModel = new MusicModel();
                            musicModel.setPosition(helper.getAdapterPosition());
                            musicModel.setMusic(item.isMusic());
                            musicModel.setClick(true);
                            EventBus.getDefault().post(musicModel);
                        } else {
                            Toast.makeText(context, "Please select the mode first", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                helper.setGone(R.id.ll_add, false);
                helper.setGone(R.id.ll_custom, false);
                helper.setText(R.id.tv_mode_name, item.getName());
                ((LampView) helper.getView(R.id.lamp_view)).setSize(item.getSize());
                ((LampView) helper.getView(R.id.lamp_view)).setColumn(item.getColumn());
                ((LampView) helper.getView(R.id.lamp_view)).setShape(0);
                ((LampView) helper.getView(R.id.lamp_view)).requestLayout();
                ((LampView) helper.getView(R.id.lamp_view)).setMirror(item.getMirror());
                ((LampView) helper.getView(R.id.lamp_view)).setModel(item.getModeArr(), item.getLight(), false);
                ((LampView) helper.getView(R.id.lamp_view)).setModelName(item.getName());
                if (item.isChose() == 1) {
                    MusicModel musicModel = new MusicModel();
                    musicModel.setPosition(helper.getAdapterPosition());
                    musicModel.setMusic(item.isMusic());
                    musicModel.setClick(false);
                    EventBus.getDefault().post(musicModel);
                }
                if (item.isChose() == 1) {
                    helper.getView(R.id.ll_item).setBackground(context.getResources().getDrawable(R.drawable.lamp_pattern_chose_bg));
                } else {
                    helper.getView(R.id.ll_item).setBackground(context.getResources().getDrawable(R.drawable.lamp_pattern_normal_bg));
                }
                if (item.isMusic() == 1) {
                    ((ImageView) helper.getView(R.id.img_music)).setImageDrawable(context.getResources().getDrawable(R.drawable.yinyueshibie));
                } else {
                    ((ImageView) helper.getView(R.id.img_music)).setImageDrawable(context.getResources().getDrawable(R.drawable.yinyuemoren));
                }
                helper.getView(R.id.img_music).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.isChose() == 1) {
                            MusicModel musicModel = new MusicModel();
                            musicModel.setPosition(helper.getAdapterPosition());
                            musicModel.setMusic(item.isMusic());
                            musicModel.setClick(true);
                            EventBus.getDefault().post(musicModel);
                        } else {
                            Toast.makeText(context, "Please select the mode first", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
