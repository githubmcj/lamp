package com.wya.env.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.wya.env.R;

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
            canvas.drawCircle(mWidth / 2 + 4, mWidth / 2 + 4, mWidth / 2 + 4, mPaint);
            mPaint.setColor(mContext.getResources().getColor(R.color.white));
            canvas.drawCircle(mWidth / 2 + 4, mWidth / 2 + 4, mWidth / 2 + 2, mPaint);
        }

        mPaint.setColor(mContext.getResources().getColor(R.color.cEDEDED));
        canvas.drawCircle(mWidth / 2 + 4, mWidth / 2 + 4, mWidth / 2 + 1, mPaint);
        mPaint.setColor(mColor);
        canvas.drawCircle(mWidth / 2 + 4, mWidth / 2 + 4, mWidth / 2, mPaint);


    }
}