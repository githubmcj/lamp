package com.wya.env.module.netdata;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wya.env.R;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.doodle.NetModel;
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

public class NetDataModelAdapter extends BaseQuickAdapter<NetModel, BaseViewHolder> {

    private Context context;
    private List<NetModel> data;


    public NetDataModelAdapter(Context context, int layoutResId, @Nullable List<NetModel> data) {
        super(layoutResId, data);
        this.context = context;
        this.data = data;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void convert(BaseViewHolder helper, NetModel item) {
        helper.setText(R.id.tv_mode_name, item.getName());
        Glide.with(context).load(item.getThumbnail()).into((ImageView) helper.getView(R.id.img));
    }
}
