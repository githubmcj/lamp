package com.wya.env.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.wya.env.R;
import com.wya.env.bean.doodle.Doodle;
import com.wya.env.util.ColorUtil;
import com.wya.utils.utils.LogUtil;
import com.wya.utils.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @date: 2020/7/18 9:13
 * @author: Chunjiang Mao
 * @classname: LampView
 * @describe: 灯光界面
 */
public class LampView extends View {


    /**
     * 背景画笔
     */
    private Paint bgPaint;
    /**
     * 灯
     */
    private Paint lampPaint;

    private Context mContext;


    public LampView(Context context) {
        super(context);
    }

    public LampView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LampView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttr(attrs);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LampView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 背景画笔
        bgPaint = new Paint();
        // 消除锯齿
        bgPaint.setAntiAlias(true);
        //防抖动
        bgPaint.setDither(true);
        bgPaint.setColor(mBackground);


        // 画背景
        canvas.drawRect(0, 0, mWidth, mHeight, bgPaint);

        // 画灯
        lampPaint = new Paint();
        // 消除锯齿
        lampPaint.setAntiAlias(true);
        // 防抖动
        lampPaint.setDither(true);

        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                lampPaint.setColor(data.get(i + column * j).getLampColor());
                canvas.drawCircle((lamp_size / 2 + lamp_margin) + mWidth / column * i, (lamp_size / 2 + lamp_margin) + mWidth / column * j, lamp_size / 2, lampPaint);
            }
        }
    }

    //拖动圆的属性
    /**
     * 背景宽度
     */
    private int mWidth;

    /**
     * 背景高度
     */
    private int mHeight;
    /**
     * 背景颜色
     */
    private int mBackground;

    /**
     * 灯光颜色
     */
    private int mLampColor;


    /**
     * 灯的个数
     */
    private int size;
    /**
     * 灯的直径
     */
    private int lamp_size;
    /**
     * 灯离边上的距离
     */
    private int lamp_margin;
    /**
     * 灯的列数
     */
    private int column;

    /**
     * 选择的颜色
     */
    private int choseColor;

    /**
     * 亮度
     */
    private int choseLight;

    private boolean isPaintBold;

    private int type;

//    String has = "#";
//    String PR_transparency = "50";// this text background color 50% transparent;
//    String og_color = "FF001A";
//
//tv.setBackgroundColor(Color.parseColor(has+PR_transparency+og_color));


    public void setPaintBold(boolean paintBold) {
        isPaintBold = paintBold;
    }

    public int getChoseColor() {
        return choseColor;
    }

    public void setChoseColor(int choseColor) {
        this.choseColor = choseColor;
    }

    private List<Doodle> data = new ArrayList<>();

    public void setData(List<Doodle> data) {
        this.data = data;
        postInvalidate();
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.LampView);
        type = typedArray.getColor(R.styleable.LampView_type, 1);

        if (type == 1) {
            mWidth = (int) typedArray.getDimension(R.styleable.LampView_width, ScreenUtil.getScreenWidth(mContext) - (int) typedArray.getDimension(R.styleable.LampView_margin_left, 0) - (int) typedArray.getDimension(R.styleable.LampView_margin_right, 0));
        } else if (type == 2) {
            mWidth = (int) typedArray.getDimension(R.styleable.LampView_width, ScreenUtil.getScreenWidth(mContext) / 2 - (int) typedArray.getDimension(R.styleable.LampView_margin_left, 0) - (int) typedArray.getDimension(R.styleable.LampView_margin_right, 0) - ScreenUtil.dip2px(mContext, 20));
        }

        column = typedArray.getColor(R.styleable.LampView_column, 20);
        if (type == 1) {
            lamp_margin = typedArray.getColor(R.styleable.LampView_lamp_margin, 2);
        } else if (type == 2) {
            lamp_margin = typedArray.getColor(R.styleable.LampView_lamp_margin, 1);
        }

        size = typedArray.getColor(R.styleable.LampView_size, 600);

        // 灯的直径
        lamp_size = mWidth / column - 2 * lamp_margin;

        mHeight = (size / column) * (lamp_size + 2 * lamp_margin);

        mBackground = typedArray.getColor(R.styleable.LampView_bg_color,
                mContext.getResources().getColor(R.color.c999999));
        mLampColor = typedArray.getColor(R.styleable.LampView_lamp_color,
                mContext.getResources().getColor(R.color.black));

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }


    public void init() {
        choseLight = 255;
        choseColor = mContext.getResources().getColor(R.color.black);

        data.clear();
        for (int i = 0; i < size; i++) {
            Doodle doodle = new Doodle();
            doodle.setColor(mLampColor);
            doodle.setLight(255);
            data.add(doodle);
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    float old_x = 0;
    float old_y = 0;
    int x;
    int y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (type == 2) {
            return super.onTouchEvent(event);
        } else {
            ViewParent parent = getParent();
            if (parent != null) {
                //父控件不拦截事件，全部交给子控件处理
                parent.requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getX();
                        y = (int) event.getY();
                        if (x > 0 && x < mWidth && y > 0 && y < mHeight) {
                            LogUtil.e("x:" + x + "---y:" + y);
                            old_x = event.getX();
                            old_y = event.getY();
                            int position = (int) ((event.getX()) / (lamp_size + 2 * lamp_margin) + ((int) ((event.getY()) / (lamp_size + 2 * lamp_margin))) * column);
                            if (isPaintBold) {
                                setBoldAllChoseColor(position);
                            } else {
                                if (data.get(position).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                                    data.get(position).setColor(choseColor);
                                    data.get(position).setLight(choseLight);
                                    postInvalidate();
                                }
                            }
                        } else {
                            LogUtil.e("在外面");
                        }
                    case MotionEvent.ACTION_MOVE:
                        x = (int) event.getX();
                        y = (int) event.getY();
                        if (x > 0 && x < mWidth && y > 0 && y < mHeight) {
                            LogUtil.e("x:" + x + "---y:" + y);
                            if (old_x == 0 || old_y == 0 || Math.abs(old_x - event.getX()) > lamp_size + 2 * lamp_margin || Math.abs(old_y - event.getY()) > lamp_size + 2 * lamp_margin) {
                                old_x = event.getX();
                                old_y = event.getY();
                                int position = (int) ((event.getX()) / (lamp_size + 2 * lamp_margin) + ((int) ((event.getY()) / (lamp_size + 2 * lamp_margin))) * column);
                                if (isPaintBold) {
                                    setBoldAllChoseColor(position);
                                } else {
                                    if (data.get(position).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                                        data.get(position).setColor(choseColor);
                                        data.get(position).setLight(choseLight);
                                        postInvalidate();
                                    }
                                }
                            }
                        } else {
                            LogUtil.e("在外面");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;

                    default:
                        break;
                }
            }
            return true;
        }

    }

    int[] choseRgb;

    private int getChoseArgb(int choseColor, int choseLight) {
        choseRgb = ColorUtil.int2Rgb(choseColor);
        return Color.argb(choseLight, choseRgb[0], choseRgb[1], choseRgb[2]);
    }

    boolean toPostInvalidate;

    private void setBoldAllChoseColor(int position) {
        toPostInvalidate = false;
        if (data.get(position).getLampColor() != getChoseArgb(choseColor, choseLight)) {
            data.get(position).setColor(choseColor);
            data.get(position).setLight(choseLight);
            toPostInvalidate = true;
        }
        if (position == 0) {
            LogUtil.e("左上角落点");
            if (data.get(position + 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + 1).setColor(choseColor);
                data.get(position + 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position + column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + column).setColor(choseColor);
                data.get(position + column).setLight(choseLight);
                toPostInvalidate = true;
            }
        } else if (position > 0 && position < column - 1) {
            LogUtil.e("上边缘点");
            if (data.get(position + 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + 1).setColor(choseColor);
                data.get(position + 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position - 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - 1).setColor(choseColor);
                data.get(position - 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position + column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + column).setColor(choseColor);
                data.get(position + column).setLight(choseLight);
                toPostInvalidate = true;
            }
        } else if (position == column - 1) {
            LogUtil.e("右上角落点");
            if (data.get(position - 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - 1).setColor(choseColor);
                data.get(position - 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position + column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + column).setColor(choseColor);
                data.get(position + column).setLight(choseLight);
                toPostInvalidate = true;
            }
        } else if (position == data.size() - 1) {
            LogUtil.e("右下角落点");
            if (data.get(position - 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - 1).setColor(choseColor);
                data.get(position - 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position - column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - column).setColor(choseColor);
                data.get(position - column).setLight(choseLight);
                toPostInvalidate = true;
            }
        } else if (position == data.size() - column) {
            LogUtil.e("左下角落点");
            if (data.get(position + 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + 1).setColor(choseColor);
                data.get(position + 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position - column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - column).setColor(choseColor);
                data.get(position - column).setLight(choseLight);
                toPostInvalidate = true;
            }
        } else if (position % column == 0) {
            LogUtil.e("左边缘点");
            if (data.get(position + 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + 1).setColor(choseColor);
                data.get(position + 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position - column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - column).setColor(choseColor);
                data.get(position - column).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position + column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + column).setColor(choseColor);
                data.get(position + column).setLight(choseLight);
                toPostInvalidate = true;
            }
        } else if ((position + 1) % column == 0) {
            LogUtil.e("右边缘点");
            if (data.get(position - 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - 1).setColor(choseColor);
                data.get(position - 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position - column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - column).setColor(choseColor);
                data.get(position - column).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position + column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + column).setColor(choseColor);
                data.get(position + column).setLight(choseLight);
                toPostInvalidate = true;
            }
        } else if (position < data.size() - 1 && position > data.size() - column) {
            LogUtil.e("下边缘点");
            if (data.get(position - 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - 1).setColor(choseColor);
                data.get(position - 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position + 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + 1).setColor(choseColor);
                data.get(position + 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position - column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - column).setColor(choseColor);
                data.get(position - column).setLight(choseLight);
                toPostInvalidate = true;
            }
        } else {
            LogUtil.e("中间点");
            if (data.get(position + 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + 1).setColor(choseColor);
                data.get(position + 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position - 1).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - 1).setColor(choseColor);
                data.get(position - 1).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position - column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position - column).setColor(choseColor);
                data.get(position - column).setLight(choseLight);
                toPostInvalidate = true;
            }
            if (data.get(position + column).getLampColor() != getChoseArgb(choseColor, choseLight)) {
                data.get(position + column).setColor(choseColor);
                data.get(position + column).setLight(choseLight);
                toPostInvalidate = true;
            }
        }
        if (toPostInvalidate) {
            postInvalidate();
        }
    }

    public void clean() {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setColor(mContext.getResources().getColor(R.color.black));
            data.get(i).setLight(255);
        }
        postInvalidate();
    }


    public void setChoseLight(int chose_light) {
        this.choseLight = chose_light * 255 / 100;
    }


    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;

    }
}
