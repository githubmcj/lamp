package com.wya.env.module.home.fragment;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wya.env.R;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.view.LampView;

import java.util.List;

/**
 * @date: 2018/6/29 13:55
 * @author: Chunjiang Mao
 * @classname: DataAdapter
 * @describe: 灯光模式
 */

public class LampModelAdapter extends BaseQuickAdapter<LampModel, BaseViewHolder> {

    private Context context;


    public LampModelAdapter(Context context, int layoutResId, @Nullable List<LampModel> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void convert(BaseViewHolder helper, LampModel item) {
        helper.setText(R.id.tv_mode_name, item.getName());
        ((LampView) helper.getView(R.id.lamp_view)).setModel(item.getModeArr());
        ((LampView) helper.getView(R.id.lamp_view)).setModelName(item.getName());
        if (item.isChose()) {
            helper.getView(R.id.ll_item).setBackground(context.getResources().getDrawable(R.drawable.lamp_pattern_chose_bg));
        } else {
            helper.getView(R.id.ll_item).setBackground(context.getResources().getDrawable(R.drawable.lamp_pattern_normal_bg));
        }
        if (item.isMusic()) {
            helper.getView(R.id.img_music).setBackground(context.getResources().getDrawable(R.drawable.yinyueshibie));
        } else {
            helper.getView(R.id.img_music).setBackground(context.getResources().getDrawable(R.drawable.yinyuemoren));
        }
        helper.getView(R.id.img_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isChose()) {
                    item.setMusic(!item.isMusic());
                    LampModelAdapter.this.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "请先选中该模式", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
