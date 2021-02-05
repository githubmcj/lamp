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

/**
 * @date: 2020/7/18 17:21
 * @author: Chunjiang Mao
 * @classname: Circle
 * @describe: 原型色块
 */
public class Circle extends View {

    private Context mContext;

    private int mWidth;
    private int mHeight;
    private int mColor;
    private Paint mPaint;

    public boolean isCircle_chose() {
        return circle_chose;
    }

    public void setCircle_chose(boolean circle_chose) {
        this.circle_chose = circle_chose;
        postInvalidate();
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
        invalidate();
    }

    private boolean circle_chose;

    public Circle(Context context) {
        super(context);
    }

    public Circle(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Circle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttr(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Circle(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

        if (circle_chose) {
            mPaint.setColor(mContext.getResources().getColor(R.color.c9396B7));
            RectF r = new RectF(4, 4, 4 + mWidth, 4 + mWidth);
            canvas.drawRoundRect(r, 10, 10, mPaint);

//            canvas.drawCircle(mWidth / 2 + 4, mWidth / 2 + 4, mWidth / 2 + 4, mPaint);
            mPaint.setColor(mContext.getResources().getColor(R.color.white));
            RectF r2 = new RectF(6, 6, 2 + mWidth, 2 + mWidth);
            canvas.drawRoundRect(r2, 10, 10, mPaint);

//            canvas.drawCircle(mWidth / 2 + 4, mWidth / 2 + 4, mWidth / 2 + 2, mPaint);
        }

        mPaint.setColor(mContext.getResources().getColor(R.color.cEDEDED));
        RectF r = new RectF(7, 7, 1 + mWidth, 1 + mWidth);
        canvas.drawRoundRect(r, 10, 10, mPaint);
//        canvas.drawCircle(mWidth / 2 + 4, mWidth / 2 + 4, mWidth / 2 + 1, mPaint);
        mPaint.setColor(mColor);
        RectF r2 = new RectF(8, 8, mWidth, mWidth);
        canvas.drawRoundRect(r2, 10, 10, mPaint);
//        canvas.drawCircle(mWidth / 2 + 4, mWidth / 2 + 4, mWidth / 2, mPaint);


    }

    public String getColor(String chose_color, int chose_light) {
        mColor = Color.rgb((ColorUtil.int2Rgb(Color.parseColor(chose_color))[0]) * chose_light / 100, (ColorUtil.int2Rgb(Color.parseColor(chose_color))[1]) * chose_light / 100, (ColorUtil.int2Rgb(Color.parseColor(chose_color))[2]) * chose_light / 100);
        postInvalidate();
        return ColorUtil.int2Hex(mColor);
    }


    public void setColor(String chose_color) {
        mColor = ColorUtil.hex2Int(chose_color);
        postInvalidate();
    }


    public String getShowColor(String chose_color, int chose_light) {
        if (chose_light < 15) {
            chose_light = 15;
        }
        return ColorUtil.int2Hex(Color.rgb((ColorUtil.int2Rgb(Color.parseColor(chose_color))[0]) * chose_light / 100, (ColorUtil.int2Rgb(Color.parseColor(chose_color))[1]) * chose_light / 100, (ColorUtil.int2Rgb(Color.parseColor(chose_color))[2]) * chose_light / 100));

    }
}