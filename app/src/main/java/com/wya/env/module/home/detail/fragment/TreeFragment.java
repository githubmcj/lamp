package com.wya.env.module.home.detail.fragment;

import android.annotation.SuppressLint;

import com.wya.env.R;
import com.wya.env.base.BaseLazyFragment;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.view.TreeView;

import butterknife.BindView;
import butterknife.Unbinder;
@SuppressLint("ValidFragment")
public class TreeFragment extends BaseLazyFragment {
    @BindView(R.id.tree)
    TreeView tree;
    private LampModel model;


    public TreeFragment(LampModel model) {
        this.model = model;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.tree_fragment;
    }

    @Override
    protected void initView() {
        tree.setSize(model.getSize());
        tree.setColumn(model.getColumn());
        tree.setModelName(model.getName());
        tree.setMirror(model.getMirror());
        tree.setModel(model.getModeArr(), model.getLight(), false);
        tree.requestLayout();
    }
}
