package com.wya.uikit.gallery;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * author : XuDonglin
 * e-mail : 465715784@qq.com
 * time   : 2018/11/29
 * desc   :
 * version: 1.0
 */
public class PreviewViewPager extends ViewPager {
	public PreviewViewPager(Context context) {
		super(context);
	}

	public PreviewViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
			return super.onTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
