package com.wya.env.module.home.detail;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wya.env.R;
import com.wya.env.bean.doodle.CopyModeColor;
import com.wya.env.view.CircleColors;

import java.util.ArrayList;
import java.util.List;

/**
 * @date: 2020/12/29 13:15
 * @author: Chunjiang Mao
 * @classname: LampColorAdapter
 * @describe:
 */

public class LampColorAdapter extends BaseQuickAdapter<List<CopyModeColor>, BaseViewHolder> {

    private Context context;
    private int chose_position = 0;


    /**
     * @param context
     * @param layoutResId
     * @param data
     */
    public LampColorAdapter(Context context, int layoutResId, @Nullable List<List<CopyModeColor>> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, List<CopyModeColor> item) {
        if (helper.getAdapterPosition() == getData().size() - 1) {
            helper.setGone(R.id.add, true);
            helper.setGone(R.id.circle, false);
        } else {
            helper.setGone(R.id.circle, true);
            helper.setGone(R.id.add, false);
            if (helper.getAdapterPosition() == chose_position) {
                ((CircleColors) helper.getView(R.id.circle)).setCircle_chose(true);
            } else {
                ((CircleColors) helper.getView(R.id.circle)).setCircle_chose(false);
            }
            ((CircleColors) helper.getView(R.id.circle)).setmColors(getDataColor(item));
        }
    }

    private List<String> dataColor;

    private List<String> getDataColor(List<CopyModeColor> colors) {
        dataColor = new ArrayList<>();
        for (int i = 0; i < colors.size(); i++) {
            dataColor.add(colors.get(i).getShowColor());
        }
        return dataColor;
    }

    public void setChoseColors(int position) {
        chose_position = position;
        this.notifyDataSetChanged();
    }
}
