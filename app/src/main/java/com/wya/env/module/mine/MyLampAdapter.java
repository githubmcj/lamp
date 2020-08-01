package com.wya.env.module.mine;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wya.env.R;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.uikit.pickerview.CustomTimePicker;
import com.wya.utils.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @date: 2018/6/29 13:55
 * @author: Chunjiang Mao
 * @classname: DataAdapter
 * @describe: 我的设备
 */

public class MyLampAdapter extends BaseQuickAdapter<LampSetting, BaseViewHolder> {

    private Context context;
    private List<LampSetting> data;
    private CustomTimePicker mCustomTimePicker;


    public MyLampAdapter(Context context, int layoutResId, @Nullable List<LampSetting> data) {
        super(layoutResId, data);
        this.context = context;
        this.data = data;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void convert(BaseViewHolder helper, LampSetting item) {
        helper.setText(R.id.name, item.getName());
        if (item.isOpen()) {
            helper.getView(R.id.img_open).setBackground(context.getResources().getDrawable(R.drawable.dengguang));
        } else {
            helper.getView(R.id.img_open).setBackground(context.getResources().getDrawable(R.drawable.morenshebei));
        }

        if (item.isHasTimer()) {
            helper.getView(R.id.img_time_open).setBackground(context.getResources().getDrawable(R.drawable.dengguang));
            helper.setVisible(R.id.time, true);
            helper.setText(R.id.time, "定时开：" + item.getStartTime() + "    " + "定时关：" + item.getStopTime());
        } else {
            helper.getView(R.id.img_time_open).setBackground(context.getResources().getDrawable(R.drawable.morenshebei));
            helper.setVisible(R.id.time, false);
        }

        helper.getView(R.id.img_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setOpen(!item.isOpen());
                MyLampAdapter.this.notifyDataSetChanged();
            }
        });

        helper.getView(R.id.img_time_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isHasTimer()) {
                    item.setHasTimer(!item.isHasTimer());
                    MyLampAdapter.this.notifyDataSetChanged();
                } else {
                    openChoseTime();
                }
            }
        });

        helper.getView(R.id.img_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(helper.getAdapterPosition());
                MyLampAdapter.this.notifyDataSetChanged();
            }
        });
    }

    private void openChoseTime() {
        mCustomTimePicker = new CustomTimePicker(context, new CustomTimePicker.OnTimePickerSelectedListener() {
            @Override
            public void selected(Date date) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String format = dateFormat.format(date);
//                mYmdhmsText.setText(format);
                LogUtil.e(format);
            }
        });
        mCustomTimePicker.setType(new boolean[]{false, false, false, true, true, false})
                .show();
    }
}
