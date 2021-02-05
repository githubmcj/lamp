package com.wya.env.module.home.detail;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wya.env.R;
import com.wya.env.bean.doodle.CopyModeColor;
import com.wya.env.util.ColorUtil;
import com.wya.env.view.Circle;

import org.w3c.dom.Text;

import java.util.List;

/**
 * @date: 2020/12/29 13:15
 * @author: Chunjiang Mao
 * @classname: LampColorAdapter
 * @describe:
 */

public class AddColorAdapter extends BaseQuickAdapter<CopyModeColor, BaseViewHolder> {

    private Context context;
    private int chose_position = 0;


    /**
     * @param context
     * @param layoutResId
     * @param data
     */
    public AddColorAdapter(Context context, int layoutResId, @Nullable List<CopyModeColor> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, CopyModeColor item) {
        if (TextUtils.isEmpty(item.getShowColor())) {
            helper.setGone(R.id.add, true);
            helper.setGone(R.id.circle, false);
        } else {
            helper.setGone(R.id.circle, true);
            helper.setGone(R.id.add, false);
            ((Circle) helper.getView(R.id.circle)).setmColor(ColorUtil.hex2Int(item.getShowColor()));
        }
    }

    public void setChoseColors(int position) {
        chose_position = position;
    }
}
