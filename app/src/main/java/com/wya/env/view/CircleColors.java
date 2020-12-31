package com.wya.env.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.wya.env.R;
import com.wya.env.util.ColorUtil;

import java.util.List;

/**
 * @date: 2020/7/18 17:21
 * @author: Chunjiang Mao
 * @classname: CircleColors
 * @describe: 圆型多色块
 */
public class CircleColors extends View {

    private Context mContext;

    private int mWidth;
    private int mHeight;
    private int mColor;
    private List<String> mColors;
    private Paint mPaint;

    public List<String> getmColors() {
        return mColors;
    }

    public void setmColors(List<String> mColors) {
        this.mColors = mColors;
        postInvalidate();
    }

    public boolean isCircle_chose() {
        return circle_chose;
    }

    public void setCircle_chose(boolean circle_chose) {
        this.circle_chose = circle_chose;
        postInvalidate();
    }


    private boolean circle_chose;

    public CircleColors(Context context) {
        super(context);
    }

    public CircleColors(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleColors(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttr(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleColors(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mWidth = (int) typedArray.getDimension(R.styleable.CircleView_circle_width, 100);
        circle_chose = typedArray.getBoolean(R.styleable.CircleView_circle_chose, false);
        mHeight = mWidth;
        mColor = typedArray.getColor(R.styleable.CircleView_circle_color,
                mContext.getResources().getColor(R.color.black));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth + 8, mHeight + 8);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 背景画笔
        mPaint = new Paint();
        // 消除锯齿
        mPaint.setAntiAlias(true);
        //防抖动
        mPaint.setDither(true);

        mPaint.setStrokeWidth(1);

        RectF ova = new RectF(2, 2, mWidth + 6, mWidth + 6);
        if (circle_chose) {
            mPaint.setColor(mContext.getResources().getColor(R.color.black));
            canvas.drawArc(ova, -135, 360, false, mPaint);
        }

        int add = 180 / mColors.size();
        RectF oval = new RectF(4, 4, mWidth + 4, mWidth + 4);
        for (int i = 0; i < mColors.size(); i++) {
            mPaint.setColor(ColorUtil.hex2Int(mColors.get(i)));
            canvas.drawArc(oval, -135 + add * i, 360 - add * i * 2, false, mPaint);
        }
    }


    public String getColor(String chose_color, int chose_light) {
        mColor = Color.rgb((ColorUtil.int2Rgb(Color.parseColor(chose_color))[0]) * chose_light / 100, (ColorUtil.int2Rgb(Color.parseColor(chose_color))[1]) * chose_light / 100, (ColorUtil.int2Rgb(Color.parseColor(chose_color))[2]) * chose_light / 100);
        postInvalidate();
        return ColorUtil.int2Hex(mColor);
    }

    public String getShowColor(String chose_color, int chose_light) {
        if (chose_light < 15) {
            chose_light = 15;
        }
        return ColorUtil.int2Hex(Color.rgb((ColorUtil.int2Rgb(Color.parseColor(chose_color))[0]) * chose_light / 100, (ColorUtil.int2Rgb(Color.parseColor(chose_color))[1]) * chose_light / 100, (ColorUtil.int2Rgb(Color.parseColor(chose_color))[2]) * chose_light / 100));

    }
}