package com.wya.env.module.mine;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @date: 2020/8/1 10:54
 * @author: Chunjiang Mao
 * @classname: GridSpacingItemDecoration
 * @describe:  GridLayoutManager（网格布局）设置item的间隔
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    //列数
    private int spanCount; 
    //间隔
    private int spacing; 
    //是否包含边缘
    private boolean includeEdge; 

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //这里是关键，需要根据你有几列来判断
        // item position
        int position = parent.getChildAdapterPosition(view);
        // item column
        int column = position % spanCount; 

        if (includeEdge) {
            // spacing - column * ((1f / spanCount) * spacing)
            outRect.left = spacing - column * spacing / spanCount; 
            // (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount;
            // top edge
            if (position < spanCount) { 
                outRect.top = spacing;
            }
            // item bottom
            outRect.bottom = spacing; 
        } else {
            // column * ((1f / spanCount) * spacing)
            outRect.left = column * spacing / spanCount;
            // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (position >= spanCount) {
                outRect.top = spacing; // item top
            }
        }
    }
}