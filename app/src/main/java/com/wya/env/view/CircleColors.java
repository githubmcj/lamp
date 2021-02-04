package com.wya.env.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
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
    private Paint mFramePaint;

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

//        int add = 180 / mColors.size();
//        RectF oval = new RectF(4, 4, mWidth + 4, mWidth + 4);

//        mColors.clear();
//        mColors.add("ff0000");
//        mColors.add("00ff00");
//        mColors.add("0000ff");

        if (mColors.size() == 1) {
            mPaint.setColor(ColorUtil.hex2Int(mColors.get(0)));
            RectF r = new RectF();
            r.left = 4;
            r.right = 4 + mWidth;
            r.top = 4;
            r.bottom = 4 + mWidth;
            canvas.drawRoundRect(r, 10, 10, mPaint);
        } else {
            for (int i = 0; i < mColors.size(); i++) {
                mPaint.setColor(ColorUtil.hex2Int(mColors.get(i)));
                RectF r = new RectF();
                r.left = 4 + mWidth / mColors.size() * i;
                r.right = 4 + mWidth / mColors.size() * (i + 1);
                r.top = 4;
                r.bottom = 4 + mWidth;
                canvas.drawRoundRect(r, 0, 0, mPaint);
            }

            // 画边框
            mFramePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
            // 消除锯齿
            mFramePaint.setAntiAlias(true);
            mFramePaint.setStrokeWidth(6);
            mFramePaint.setStyle(Paint.Style.STROKE);        // 防抖动
            mFramePaint.setDither(true);
            RectF ova = new RectF(2, 2, mWidth + 6, mWidth + 6);
            if (circle_chose) {
                mFramePaint.setColor(mContext.getResources().getColor(R.color.black));
            } else {
                mFramePaint.setColor(mContext.getResources().getColor(R.color.white));
            }
            canvas.drawRoundRect(ova, 10, 10, mFramePaint);
//            canvas.drawRoundRect(oval, -135 + add * i, 360 - add * i * 2, false, mPaint);
        }
    }

    private Path getPath(RectF rectF, float radius, boolean topLeft, boolean topRight,
                         boolean bottomRight, boolean bottomLeft) {

        final Path path = new Path();
        final float[] radii = new float[8];

        if (topLeft) {
            radii[0] = radius;
            radii[1] = radius;
        }

        if (topRight) {
            radii[2] = radius;
            radii[3] = radius;
        }

        if (bottomRight) {
            radii[4] = radius;
            radii[5] = radius;
        }

        if (bottomLeft) {
            radii[6] = radius;
            radii[7] = radius;
        }

        path.addRoundRect(rectF, radii, Path.Direction.CW);

        return path;
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