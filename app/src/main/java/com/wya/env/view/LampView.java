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
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.common.CommonValue;
import com.wya.env.util.ByteUtil;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.utils.utils.LogUtil;
import com.wya.utils.utils.ScreenUtil;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    /**
     * 边框
     */
    private Paint framePaint;
    private Paint whitePaint;

    private Context mContext;

    private int isMirror;

    private boolean isStopSendUdpData;
    private boolean isStopSendUdpModeData;
    private boolean toClean;
    /**
     * 1 方形， 0 圆形，默认
     */
    private int shape;


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

        // 画边框
        framePaint = new Paint();
        // 消除锯齿
        framePaint.setAntiAlias(true);
        // 防抖动
        framePaint.setDither(true);
        framePaint.setColor(Color.parseColor("#ffffff"));

        // 画边框
        whitePaint = new Paint();
        // 消除锯齿
        whitePaint.setAntiAlias(true);
        // 防抖动
        whitePaint.setDither(true);
        whitePaint.setColor(Color.parseColor("#ffffff"));

        for (int i = 0; i < column; i++) {
            for (int j = 0; j < size / column; j++) {
                if (data.get(String.valueOf(j + size / column * i)) != null) {
                    if (data.get(String.valueOf(j + size / column * i)).isFlash() == 1) {
//                        data.get(String.valueOf(j + size / column * i)).setLight(light);
                        lampPaint.setColor(data.get(String.valueOf(j + size / column * i)).getLampColor(light * 255 / 100));
                    } else {
                        lampPaint.setColor(data.get(String.valueOf(j + size / column * i)).getLampColor(255));
                    }
                } else {
                    lampPaint.setColor(Color.argb(0, 0, 0, 0));
                }

                if (shape == 0) {
                    canvas.drawCircle((lamp_size / 2 + lamp_margin) + mWidth / column * i, (lamp_size / 2 + lamp_margin) + mWidth / column * j, lamp_size / 2 + 1, framePaint);
                    canvas.drawCircle((lamp_size / 2 + lamp_margin) + mWidth / column * i, (lamp_size / 2 + lamp_margin) + mWidth / column * j, lamp_size / 2, whitePaint);
                    canvas.drawCircle((lamp_size / 2 + lamp_margin) + mWidth / column * i, (lamp_size / 2 + lamp_margin) + mWidth / column * j, lamp_size / 2, lampPaint);
                } else {
                    canvas.drawRect(mWidth / column * i, mWidth / column * j, mWidth / column * i + lamp_size + 2, mWidth / column * j + lamp_size + 2, framePaint);
                    canvas.drawRect(mWidth / column * i, mWidth / column * j, mWidth / column * i + lamp_size, mWidth / column * j + lamp_size, whitePaint);
                    canvas.drawRect(mWidth / column * i, mWidth / column * j, mWidth / column * i + lamp_size, mWidth / column * j + lamp_size, lampPaint);
                }
            }
        }
    }


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
    private String choseColor;

    /**
     * w值
     */
    private int w;

    /**
     * 展示的颜色
     */
    private String showColor;

//    /**
//     * 亮度
//     */
//    private int choseLight;


    private int light;

    private boolean isPaintBold;

    private int type;

    /**
     * 是否闪烁
     */
    private boolean isTwinkle;

    /**
     * 是否存在闪烁
     */
    private boolean hasTwinkle;

    /**
     * 闪烁一次时间  ms
     */
    private int period;

    /**
     * 每一帧时间
     */
    private int frameTime;

    /**
     * 速度
     */
    private int speed;

    /**
     * 模板每一帧时间
     */
    private int modelFrameTime;

    /**
     * 模板每一帧时间
     */
    private int sendDataTime;

    /**
     * 模板名字
     */
    private String modelName;

    /**
     * 实时画板
     */
    private boolean isOnline;

    /**
     * 0x00 RGB; 0x04 RGBW
     */
    private int colorType = 0;

    public int getColorType() {
        return colorType;
    }

    public void setColorType(int colorType) {
        this.colorType = colorType;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * @return 灯的数量
     */
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
        // 灯的直径
        lamp_size = mWidth / column - 2 * lamp_margin;
        mHeight = (size / column) * (lamp_size + 2 * lamp_margin);
    }

    public void setTwinkle(boolean twinkle) {
        isTwinkle = twinkle;
        if (hasTwinkle()) {
            toTwinkle();
        }
    }

    private boolean hasTwinkle() {
        hasTwinkle = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(String.valueOf(i)).isFlash() == 1) {
                hasTwinkle = true;
                break;
            }
        }
        return hasTwinkle;
    }


    /**
     * 闪烁线程开启
     */
    private int add;
    private ScheduledExecutorService twinkleExecutorService;
    private Runnable twinkleTask;
    private boolean isAdd = false;

    private void toTwinkle() {
        stopTwinkle();
        add = 0;
        if (twinkleExecutorService == null) {
            LogUtil.e("启动闪烁");
            twinkleExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("twinkleExecutorService").daemon(true).build());
        }
        if (twinkleTask == null) {
            twinkleTask = new Runnable() {
                @Override
                public void run() {
                    if (add != -1) {
                        if (light >= 255) {
                            isAdd = false;
                        } else if (light <= 0) {
                            isAdd = true;
                        }
                        if (isAdd) {
                            if (light > 250) {
                                light = 255;
                            } else {
                                light = light + 5;
                            }
                        } else {
                            if (light < 5) {
                                light = 0;
                            } else {
                                light = light - 5;
                            }
                        }
                        add = 0;
                        postInvalidate();
                    }
                }
            };
        }
        if (twinkleExecutorService != null) {
            twinkleExecutorService.scheduleAtFixedRate(twinkleTask, 0, frameTime / 10, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 开始闪烁
     */
    public void startTwinkle() {
        if (isOnline) {
            if (hasTwinkle()) {
                LogUtil.e("启动闪烁");
                toTwinkle();
            }
        }
    }

    /**
     * 停止闪烁
     */
    public void stopTwinkle() {
        LogUtil.e("停止闪烁");
        if (twinkleExecutorService != null) {
            LogUtil.e("停止闪烁 twinkleExecutorService != null");
            twinkleExecutorService.shutdownNow();
        }
        // 非单例模式，置空防止重复的任务
        twinkleExecutorService = null;
    }

    public void setPaintBold(boolean paintBold) {
        isPaintBold = paintBold;
    }

    public String getChoseColor() {
        return choseColor;
    }

    public void setChoseColor(String choseColor, int w) {
        if (choseColor == null) {
            choseColor = "#000000";
        }
        this.w = w;
        this.choseColor = choseColor;
    }

    public void setShowColor(String showColor) {
        if (showColor == null) {
            showColor = "#000000";
        }
        this.showColor = showColor;
    }

    private HashMap<String, Doodle> data = new HashMap<>();
    private HashMap<String, Doodle> clean_data = new HashMap<>();

    public void setModel(List<DoodlePattern> modeArr, int light, boolean toShow) {
        LogUtil.e("toShow=============" + toShow);
        LogUtil.e("toShow=============" + modeArr.size());
        this.size = modeArr.get(0).getSize();
        this.light = light;
        mHeight = (size / column) * (lamp_size + 2 * lamp_margin);
        setMeasuredDimension(mWidth, mHeight);
        if (modeArr.size() == 1) {
            addMode = -1;
            this.data = modeArr.get(0).getLight_status();
            if (hasTwinkle()) {
                toTwinkle();
                if (toShow) {
                    isStopSendUdpData = false;
                    sendUdpMessage();
                    LogUtil.e("=============1");
                    stopSendUdpModeData();
                }
            } else {
                stopTwinkle();
                postInvalidate();
                if (toShow) {
                    isStopSendUdpData = false;
                    sendUdpMessage();
                    LogUtil.e("=============2");
                    stopSendUdpModeData();
                }
            }
        } else {
            add = -1;
            this.modeArr = modeArr;
            toShowModel(toShow);
            stopSendUdpData();
        }
    }

    private int addMode;
    ScheduledExecutorService modelExecutorService;
    private List<DoodlePattern> modeArr;

    private void toShowModel(boolean toShow) {
        addMode = 0;
        isStopSendUdpModeData = false;
        LogUtil.e("开始发送模板数据");
        LogUtil.e(toShow + "=--------------" + isStopSendUdpModeData + "---" + (modelExecutorService == null));
        if (modelExecutorService == null) {
            modelExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("toShowModel").daemon(true).build());
            modelExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (addMode != -1) {

                        data = modeArr.get(addMode % modeArr.size()).getLight_status();
                        addMode++;
                        postInvalidate();
                        if (toShow) {
                            try {
                                LogUtil.e("isStopSendUdpModeData:" + isStopSendUdpModeData);
                                if (isStopSendUdpModeData) {
                                    if (toClean) {
                                        send(SaveSharedPreferences.getString(mContext, CommonValue.IP, "255.255.255.255"), CommonValue.UDP_PORT, getUdpByteData(cleanData(data)), "模板");
                                        LogUtil.e("清除灯数据成功");
                                    }
                                    LogUtil.e("=============3");
                                    stopSendUdpModeData();
                                } else {
                                    send(SaveSharedPreferences.getString(mContext, CommonValue.IP, "255.255.255.255"), CommonValue.UDP_PORT, getUdpByteData(isMirror == 1 ? toMirror(modeArr.get(addMode % modeArr.size()).getLight_status()) : modeArr.get(addMode % modeArr.size()).getLight_status()), "模板");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                LogUtil.e("发送UDP数据失败");
                            }
                        }
                    }
                }
            }, 0, modelFrameTime, TimeUnit.MILLISECONDS);
        }
    }

    private HashMap<String, Doodle> cleanData(HashMap<String, Doodle> data) {
        clean_data.clear();
        for (int i = 0; i < data.size(); i++) {
            Doodle doodle = new Doodle();
            doodle.setColor("#000000");
            doodle.setFlash(0);
            clean_data.put(String.valueOf(i), doodle);
        }
        return clean_data;
    }


    private HashMap<String, Doodle> save_data = new HashMap<>();

    public HashMap<String, Doodle> getSaveData() {
        save_data.clear();
        for (int i = 0; i < data.size(); i++) {
            if (!data.get(String.valueOf(i)).getColor().equals("#000000")) {
                save_data.put(String.valueOf(i), data.get(String.valueOf(i)));
            }
        }
        return save_data;
    }


    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.LampView);
        type = typedArray.getInt(R.styleable.LampView_type, 1);
        isOnline = typedArray.getBoolean(R.styleable.LampView_online, false);

        if (type == 1) {
            mWidth = (int) typedArray.getDimension(R.styleable.LampView_width, ScreenUtil.getScreenWidth(mContext) - (int) typedArray.getDimension(R.styleable.LampView_margin_left, 0) - (int) typedArray.getDimension(R.styleable.LampView_margin_right, 0));
        } else if (type == 2) {
            mWidth = (int) typedArray.getDimension(R.styleable.LampView_width, ScreenUtil.getScreenWidth(mContext) / 2 - (int) typedArray.getDimension(R.styleable.LampView_margin_left, 0) - (int) typedArray.getDimension(R.styleable.LampView_margin_right, 0) - ScreenUtil.dip2px(mContext, 20));
        }

        column = typedArray.getColor(R.styleable.LampView_column, 15);
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
//        choseLight = 255;
        choseColor = "#000000";
        showColor = "#000000";
        light = 255;

        hasTwinkle = false;
        period = 2000;
        frameTime = 200;
        modelFrameTime = 200;
        sendDataTime = 200;


        data.clear();
        for (int i = 0; i < size; i++) {
            Doodle doodle = new Doodle();
            doodle.setColor("#000000");
            doodle.setFlash(0);
//            doodle.setLight(255);
            data.put(String.valueOf(i), doodle);
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
    long createTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isOnline) {
            return super.onTouchEvent(event);
        } else {
            ViewParent parent = getParent();
            if (parent != null) {
                //父控件不拦截事件，全部交给子控件处理
                parent.requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        createTime = System.currentTimeMillis();
                        x = (int) event.getX();
                        y = (int) event.getY();
                        if (x > 0 && x < column * (lamp_size + 2 * lamp_margin) && y > 0 && y < (size / column) * (lamp_size + 2 * lamp_margin)) {
                            old_x = event.getX();
                            old_y = event.getY();
                            int position = ((int) ((event.getX()) / (lamp_size + 2 * lamp_margin)) * (size / column) + ((int) (event.getY() / (lamp_size + 2 * lamp_margin))));
                            LogUtil.e("i----" + (int) ((event.getX()) / (lamp_size + 2 * lamp_margin)) + "j----" + ((int) (event.getY() / (lamp_size + 2 * lamp_margin))));
                            LogUtil.e("postion----" + position);
                            if (isPaintBold) {
                                setBoldAllChoseColor(position);
                            } else {
                                if (!data.get(String.valueOf(position)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position)).isFlash() == 1) != isTwinkle) {
                                    data.get(String.valueOf(position)).setColor(choseColor);
                                    data.get(String.valueOf(position)).setShowColor(showColor);
                                    data.get(String.valueOf(position)).setW(w);
//                                    data.get(String.valueOf(position)).setLight(choseLight);
                                    if (isTwinkle && choseColor != "#000000") {
                                        data.get(String.valueOf(position)).setFlash(1);
                                    } else {
                                        data.get(String.valueOf(position)).setFlash(0);
                                    }
                                    postInvalidate();
                                    sendUdpDataAdd = 0;
                                }
                            }
                            if (!hasTwinkle) {
                                startTwinkle();
                            }
                        } else {
                            LogUtil.e("在外面");
                        }
                    case MotionEvent.ACTION_MOVE:
                        x = (int) event.getX();
                        y = (int) event.getY();
                        if (x > 0 && x < column * (lamp_size + 2 * lamp_margin) && y > 0 && y < (size / column) * (lamp_size + 2 * lamp_margin)) {
                            if (old_x == 0 || old_y == 0 || Math.abs(old_x - event.getX()) > lamp_size + 2 * lamp_margin || Math.abs(old_y - event.getY()) > lamp_size + 2 * lamp_margin) {
                                old_x = event.getX();
                                old_y = event.getY();
                                int position = ((int) ((event.getX()) / (lamp_size + 2 * lamp_margin)) * (size / column) + ((int) (event.getY() / (lamp_size + 2 * lamp_margin))));
                                LogUtil.e("i----" + (int) ((event.getX()) / (lamp_size + 2 * lamp_margin)) + "j----" + ((int) (event.getY() / (lamp_size + 2 * lamp_margin))));
                                LogUtil.e("postion----" + position);
                                if (isPaintBold) {
                                    setBoldAllChoseColor(position);
                                } else {
                                    if (!data.get(String.valueOf(position)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position)).isFlash() == 1) != isTwinkle) {
                                        data.get(String.valueOf(position)).setColor(choseColor);
                                        data.get(String.valueOf(position)).setShowColor(showColor);
                                        data.get(String.valueOf(position)).setW(w);
//                                        data.get(String.valueOf(position)).setLight(choseLight);
                                        if (isTwinkle && choseColor != "#000000") {
                                            data.get(String.valueOf(position)).setFlash(1);
                                        } else {
                                            data.get(String.valueOf(position)).setFlash(0);
                                        }
                                        postInvalidate();
                                        sendUdpDataAdd = 0;
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

    boolean toPostInvalidate;

    private void setBoldAllChoseColor(int position) {
        toPostInvalidate = false;
        if (!data.get(String.valueOf(position)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position)).isFlash() == 1) != isTwinkle) {
            data.get(String.valueOf(position)).setColor(choseColor);
            data.get(String.valueOf(position)).setShowColor(showColor);
            data.get(String.valueOf(position)).setW(w);
//            data.get(String.valueOf(position)).setLight(choseLight);
            if (isTwinkle) {
                data.get(String.valueOf(position)).setFlash(1);
            } else {
                data.get(String.valueOf(position)).setFlash(0);
            }
            toPostInvalidate = true;
        }
        if (position == 0) {
            LogUtil.e("左上角落点");
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
                data.get(String.valueOf(position + 1)).setW(w);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
                data.get(String.valueOf(position + size / column)).setW(w);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position > 0 && position < size / column - 1) {
            LogUtil.e("左边缘点");
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
                data.get(String.valueOf(position + 1)).setW(w);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
                data.get(String.valueOf(position - 1)).setW(w);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
                data.get(String.valueOf(position + size / column)).setW(w);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position == size / column - 1) {
            LogUtil.e("左下角落点");
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
                data.get(String.valueOf(position - 1)).setW(w);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
                data.get(String.valueOf(position + size / column)).setW(w);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position == data.size() - 1) {
            LogUtil.e("右下角落点");
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
                data.get(String.valueOf(position - 1)).setW(w);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
                data.get(String.valueOf(position - size / column)).setW(w);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position == data.size() - size / column) {
            LogUtil.e("右上角落点");
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
                data.get(String.valueOf(position + 1)).setW(w);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
                data.get(String.valueOf(position - size / column)).setW(w);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position % (size / column) == 0) {
            LogUtil.e("上边缘点");
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
                data.get(String.valueOf(position + 1)).setW(w);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
                data.get(String.valueOf(position - size / column)).setW(w);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
                data.get(String.valueOf(position + size / column)).setW(w);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if ((position + 1) % (size / column) == 0) {
            LogUtil.e("下边缘点");
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
                data.get(String.valueOf(position - 1)).setW(w);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
                data.get(String.valueOf(position - size / column)).setW(w);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
                data.get(String.valueOf(position + size / column)).setW(w);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else if (position < data.size() - 1 && position > data.size() - size / column) {
            LogUtil.e("右边缘点");
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
                data.get(String.valueOf(position - 1)).setW(w);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
                data.get(String.valueOf(position + 1)).setW(w);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
                data.get(String.valueOf(position - size / column)).setW(w);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        } else {
            LogUtil.e("中间点");
            if (!data.get(String.valueOf(position + 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position + 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position + 1)).setColor(choseColor);
                data.get(String.valueOf(position + 1)).setShowColor(showColor);
                data.get(String.valueOf(position + 1)).setW(w);
//                data.get(String.valueOf(position + 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - 1)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - 1)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - 1)).setColor(choseColor);
                data.get(String.valueOf(position - 1)).setShowColor(showColor);
                data.get(String.valueOf(position - 1)).setW(w);
//                data.get(String.valueOf(position - 1)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - 1)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - 1)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position - size / column)).getColor().equalsIgnoreCase(choseColor) || (data.get(String.valueOf(position - size / column)).isFlash() == 1) != isTwinkle) {
                data.get(String.valueOf(position - size / column)).setColor(choseColor);
                data.get(String.valueOf(position - size / column)).setShowColor(showColor);
                data.get(String.valueOf(position - size / column)).setW(w);
//                data.get(String.valueOf(position - size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position - size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position - size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
            if (!data.get(String.valueOf(position + size / column)).getColor().equalsIgnoreCase(choseColor)) {
                data.get(String.valueOf(position + size / column)).setColor(choseColor);
                data.get(String.valueOf(position + size / column)).setShowColor(showColor);
                data.get(String.valueOf(position + size / column)).setW(w);
//                data.get(String.valueOf(position + size / column)).setLight(choseLight);
                if (isTwinkle) {
                    data.get(String.valueOf(position + size / column)).setFlash(1);
                } else {
                    data.get(String.valueOf(position + size / column)).setFlash(0);
                }
                toPostInvalidate = true;
            }
        }
        if (toPostInvalidate) {
            postInvalidate();
            sendUdpDataAdd = 0;
        }
    }

    public void clean() {
        for (int i = 0; i < data.size(); i++) {
            data.get(String.valueOf(i)).setColor("#000000");
            data.get(String.valueOf(i)).setShowColor("#000000");
            data.get(String.valueOf(i)).setFlash(0);
        }
        postInvalidate();
    }


    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    HashMap<String, Doodle> mirror_doodles;

    public void setMirror(int isMirror) {
        this.isMirror = isMirror;
    }

    private HashMap<String, Doodle> toMirror(HashMap<String, Doodle> doodles) {
        mirror_doodles = depCopy(doodles);
        for (int i = 0; i < mirror_doodles.size() / 2; i++) {
            for (int j = 0; j < column / 2; j++) {
                if (i >= mirror_doodles.size() / column * j && i < mirror_doodles.size() / column + mirror_doodles.size() / column * j) {
                    toChangeValue(mirror_doodles, i, (mirror_doodles.size() - (mirror_doodles.size() / column)) - j * 2 * (mirror_doodles.size() / column) + i);
                }
            }
        }
        return mirror_doodles;
    }

    private void toChangeValue(HashMap<String, Doodle> mirror_doodles, int i, int i1) {
        Doodle emp = mirror_doodles.get(String.valueOf(i));
        mirror_doodles.put(String.valueOf(i), mirror_doodles.get(String.valueOf(i1)));
        mirror_doodles.put(String.valueOf(i1), emp);
    }

    /**
     * 深拷贝
     *
     * @param doodles
     * @return
     */
    public HashMap<String, Doodle> depCopy(HashMap<String, Doodle> doodles) {
        HashMap<String, Doodle> destList = new HashMap<String, Doodle>();
        for (Iterator keyIt = doodles.keySet().iterator(); keyIt.hasNext(); ) {
            String key = (String) keyIt.next();
            destList.put(key, doodles.get(key));
        }
        return destList;
    }


    /**
     * 获取Udp实时数据
     *
     * @return
     */
    public byte[] getUdpByteData(HashMap<String, Doodle> data) {
        byte[] upd_data;
        LogUtil.e("colorType:" + colorType);
        if (colorType == 0x04) {
            upd_data = new byte[1 + 2 + 2 + 4 * data.size()];
            upd_data[0] = 0x01;
            upd_data[1] = 0x00;
            upd_data[2] = 0x00;
            byte[] len = ByteUtil.intToByteArray(data.size());
            if (len.length == 1) {
                upd_data[3] = ByteUtil.intToByteArray(data.size())[0];
                upd_data[4] = 0x00;
            } else if (len.length == 2) {
                upd_data[3] = ByteUtil.intToByteArray(data.size())[0];
                upd_data[4] = ByteUtil.intToByteArray(data.size())[1];
            }
            for (int i = 0; i < data.size(); i++) {
                String color = data.get(String.valueOf(i)).getColor();
                boolean isTwinkle = data.get(String.valueOf(i)).isFlash() == 1;
                if (isTwinkle) {
                    if (Math.random() * 10 < 4) {
                        upd_data[i * 4 + 5] = 0x00;
                        upd_data[i * 4 + 4] = 0x00;
                        upd_data[i * 4 + 7] = 0x00;
                        upd_data[i * 4 + 8] = 0x00;
                    } else {
                        upd_data[i * 4 + 5] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
                        upd_data[i * 4 + 6] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
                        upd_data[i * 4 + 7] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));
                        upd_data[i * 4 + 8] = (byte) ByteUtil.intToByteArray(data.get(String.valueOf(i)).getW())[0];
                    }
                } else {
                    upd_data[i * 4 + 5] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
                    upd_data[i * 4 + 6] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
                    upd_data[i * 4 + 7] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));
                    upd_data[i * 4 + 8] = (byte) ByteUtil.intToByteArray(data.get(String.valueOf(i)).getW())[0];
                }
            }

        } else {
            upd_data = new byte[1 + 2 + 2 + 3 * data.size()];
            upd_data[0] = 0x01;
            upd_data[1] = 0x00;
            upd_data[2] = 0x00;
            byte[] len = ByteUtil.intToByteArray(data.size());
            if (len.length == 1) {
                upd_data[3] = ByteUtil.intToByteArray(data.size())[0];
                upd_data[4] = 0x00;
            } else if (len.length == 2) {
                upd_data[3] = ByteUtil.intToByteArray(data.size())[0];
                upd_data[4] = ByteUtil.intToByteArray(data.size())[1];
            }
            for (int i = 0; i < data.size(); i++) {
                String color = data.get(String.valueOf(i)).getColor();
                boolean isTwinkle = data.get(String.valueOf(i)).isFlash() == 1;
                if (isTwinkle) {
                    if (Math.random() * 10 < 4) {
                        upd_data[i * 3 + 5] = 0x00;
                        upd_data[i * 3 + 6] = 0x00;
                        upd_data[i * 3 + 7] = 0x00;
                    } else {
                        upd_data[i * 3 + 5] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
                        upd_data[i * 3 + 6] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
                        upd_data[i * 3 + 7] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));
                    }
                } else {
                    upd_data[i * 3 + 5] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
                    upd_data[i * 3 + 6] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
                    upd_data[i * 3 + 7] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));
                }
            }
        }
        return upd_data;
    }


    public void startSendUpdData() {
        if (isOnline) {
            LogUtil.e("启动发送UDP数据");
            isStopSendUdpData = false;
            sendUdpMessage();
        }
    }

    private int sendUdpDataAdd;
    private ScheduledExecutorService udpExecutorService;
    private ScheduledFuture<?> udpScheduledFuture;
    private Runnable udpTask;

    private void sendUdpMessage() {
        stopSendUdpData();
        sendUdpDataAdd = 0;
        LogUtil.e("开始发送数据");
        if (udpExecutorService == null) {
            udpExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("udpExecutorService").daemon(true).build());
        }
        if (udpTask == null) {
            udpTask = new Runnable() {
                @Override
                public void run() {
                    if (sendUdpDataAdd != -1) {
                        addMode++;
                        try {
                            if (isStopSendUdpData) {
                                send(SaveSharedPreferences.getString(mContext, CommonValue.IP, "255.255.255.255"), CommonValue.UDP_PORT, getUdpByteData(cleanData(data)), "画板");
                                stopSendUdpData();
                            } else {
                                send(SaveSharedPreferences.getString(mContext, CommonValue.IP, "255.255.255.255"), CommonValue.UDP_PORT, getUdpByteData(isMirror == 1 ? toMirror(data) : data), "画板");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogUtil.e("发送UDP数据失败");
                        }
                    }
                }
            };
        }
        if (udpExecutorService != null) {
            udpScheduledFuture = udpExecutorService.scheduleAtFixedRate(udpTask, 0, sendDataTime, TimeUnit.MILLISECONDS);
        }
    }


    private void send(String destip, int port, byte[] udpByteData, String type) throws IOException {
        LogUtil.e(destip + "----------------------");
        InetAddress address = InetAddress.getByName(destip);
        byte[] send_head_data = ByteUtil.getHeadByteData(udpByteData);
//        LogUtil.e("udpByteData:" + byte2hex(udpByteData));
//        LogUtil.e("send_head_data:" + byte2hex(send_head_data));
        byte[] send_data = ByteUtil.byteMerger(send_head_data, udpByteData);
        LogUtil.e("send_data:" + byte2hex(send_data));
        // 2.创建数据报，包含发送的数据信息
        DatagramPacket packet = new DatagramPacket(send_data, send_data.length, address, port);
        // 3.创建DatagramSocket对象
        DatagramSocket socket = new DatagramSocket();
        // 4.向服务器端发送数据报
        socket.send(packet);
        // 5.关闭资源
        socket.close();
        LogUtil.e(type + "发送UDP数据成功");
    }

    public String byte2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            //将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp + " ");
        }
        return sb.toString();
    }

    public void toStopSendUdpData(boolean isStopSendUdpData) {
        this.isStopSendUdpData = isStopSendUdpData;
    }

    public void stopSendUdpData() {
        if (udpExecutorService != null) {
            LogUtil.e("停止发送数据");
            udpExecutorService.shutdownNow();
        }
        // 非单例模式，置空防止重复的任务
        udpExecutorService = null;
    }

    public void toStopSendUdpModeData(boolean isStopSendUdpModeData, boolean toClean) {
        this.isStopSendUdpModeData = isStopSendUdpModeData;
        this.toClean = toClean;
    }

    public void stopSendUdpModeData() {
        if (modelExecutorService != null) {
            LogUtil.e("停止发送数据");
            modelExecutorService.shutdownNow();
        }
        // 非单例模式，置空防止重复的任务
        modelExecutorService = null;
    }

    public void setAllColor(String chose_color) {
        for (int i = 0; i < data.size(); i++) {
            data.get(String.valueOf(i)).setColor(chose_color);
            data.get(String.valueOf(i)).setShowColor(chose_color);
            data.get(String.valueOf(i)).setFlash(0);
        }
        postInvalidate();
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        modelFrameTime = 200 * speed;
        LogUtil.e("=============4");
        stopSendUdpModeData();
//        toStopSendUdpModeData(true, false);
    }

    public void setClean(boolean clean) {
        clean = true;
    }
}

