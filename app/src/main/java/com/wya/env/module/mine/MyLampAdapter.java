package com.wya.env.module.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wya.env.R;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.bean.home.MusicModel;
import com.wya.env.bean.home.MusicSuccess;
import com.wya.env.net.tpc.CallbackIdKeyFactoryImpl;
import com.wya.env.net.tpc.EasySocket;
import com.wya.env.net.tpc.config.EasySocketOptions;
import com.wya.env.net.tpc.connection.heartbeat.HeartManager;
import com.wya.env.net.tpc.entity.OriginReadData;
import com.wya.env.net.tpc.entity.SocketAddress;
import com.wya.env.net.tpc.interfaces.conn.ISocketActionListener;
import com.wya.env.net.tpc.interfaces.conn.SocketActionListener;
import com.wya.env.util.ByteUtil;
import com.wya.env.view.WheelView;
import com.wya.uikit.button.WYAButton;
import com.wya.uikit.dialog.WYACustomDialog;
import com.wya.uikit.pickerview.CustomTimePicker;
import com.wya.utils.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.wya.env.common.CommonValue.TCP_PORT;

/**
 * @date: 2018/6/29 13:55
 * @author: Chunjiang Mao
 * @classname: DataAdapter
 * @describe: 我的设备
 */

public class MyLampAdapter extends BaseQuickAdapter<LampSetting, BaseViewHolder> {

    private Context context;
    private List<LampSetting> data;
    private CustomTimePicker mCustomTimePicker;
    private Message msg;
    private String ip;
    private byte[] bodyData;
    private int position;
    private boolean isConnected;

    private MusicModel musicModel;

    public void setMusicModel(MusicModel musicModel) {
        this.musicModel = musicModel;
        getLampMusicState();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 1:
                    data.get((Integer) msg.obj).setOpen(!data.get((Integer) msg.obj).isOpen());
                    MyLampAdapter.this.notifyDataSetChanged();
                    break;
                case 2:
                    if(musicModel.isClick()){
                        MusicSuccess musicSuccess = new MusicSuccess();
                        musicSuccess.setPosition(musicModel.getPosition());
                        EventBus.getDefault().post(musicSuccess);
                    } else {
                        LogUtil.e("声控开关和模型同步");
                    }
                    break;
                case 3:
                    data.get((Integer) msg.obj).setHasTimer(!data.get((Integer) msg.obj).isHasTimer());
                    MyLampAdapter.this.notifyDataSetChanged();
                    break;
                case 4:
                    data.get((Integer) msg.obj).setOpen(msg.getData().getBoolean("isOpen"));
                    MyLampAdapter.this.notifyDataSetChanged();
                    break;
                case 5:
                    data.get((Integer) msg.obj).setHasTimer(msg.getData().getBoolean("hasTime"));
                    data.get((Integer) msg.obj).setS_hour(msg.getData().getString("s_hour"));
                    data.get((Integer) msg.obj).setS_min(msg.getData().getString("s_min"));
                    data.get((Integer) msg.obj).setE_hour(msg.getData().getString("e_hour"));
                    data.get((Integer) msg.obj).setE_min(msg.getData().getString("e_min"));
                    MyLampAdapter.this.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };


    public MyLampAdapter(Context context, int layoutResId, @Nullable List<LampSetting> data) {
        super(layoutResId, data);
        this.context = context;
        this.data = data;
        toLinkTcp();
        LogUtil.e("MyLampAdapter-----------------");
    }

    public void toLinkTcp() {
        for (int i = 0; i < data.size(); i++) {
            if(data.get(i).isChose()){
                ip = data.get(i).getIp();
                position = i;
                initEasySocket(ip);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void convert(BaseViewHolder helper, LampSetting item) {
        if (item.getName() == null) {
            helper.setGone(R.id.ll_add, true);
        } else {
            helper.setGone(R.id.ll_add, false);
            helper.setText(R.id.name, item.getDeviceName());
            if (item.isChose()) {
                helper.getView(R.id.ll_item).setBackground(context.getResources().getDrawable(R.drawable.lamp_pattern_chose_bg));
//                position = helper.getAdapterPosition();
//                initEasySocket(item.getIp());
                helper.getView(R.id.img_open).setEnabled(true);
                helper.getView(R.id.img_time_open).setEnabled(true);
            } else {
                helper.getView(R.id.ll_item).setBackground(context.getResources().getDrawable(R.drawable.lamp_pattern_normal_bg));
                helper.getView(R.id.img_open).setEnabled(false);
                helper.getView(R.id.img_time_open).setEnabled(false);
            }
            if (item.isOpen()) {
                helper.setImageDrawable(R.id.img_open, context.getResources().getDrawable(R.drawable.dengguang));
            } else {
                helper.setImageDrawable(R.id.img_open, context.getResources().getDrawable(R.drawable.morenshebei));
            }

            if (item.isHasTimer()) {
                helper.setImageDrawable(R.id.img_time_open, context.getResources().getDrawable(R.drawable.dengguang));
                helper.setVisible(R.id.time, true);
                helper.setText(R.id.time, "turn on:" + item.getS_hour() + ":" + item.getS_min() + "    " + "\nturn off:" + item.getE_hour() + ":" + item.getE_min());
            } else {
                helper.setImageDrawable(R.id.img_time_open, context.getResources().getDrawable(R.drawable.morenshebei));
                helper.setVisible(R.id.time, false);
            }

            helper.getView(R.id.img_open).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ip = item.getIp();
                    position = helper.getAdapterPosition();
                    bodyData = getOpenLamp(!item.isOpen());
                    EasySocket.getInstance().upBytes(bodyData);
                }
            });

            helper.getView(R.id.img_time_open).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = helper.getAdapterPosition();
                    if (item.isHasTimer()) {
                        bodyData = getTimerTime(false, item.getS_hour(), item.getS_min(), item.getE_hour(), item.getE_min());
                        EasySocket.getInstance().upBytes(bodyData);
                    } else {
                        toSendTime();
                        openChoseTime(item);
                    }
                }
            });

            helper.getView(R.id.img_del).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.remove(helper.getAdapterPosition());
                    stopTcp();
                    MyLampAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    private WYACustomDialog wyaCustomDialog;
    private String s_hour = "0";
    private String s_min = "0";
    private String e_hour = "0";
    private String e_min = "0";

    private void openChoseTime(LampSetting item) {
        s_hour = item.getS_hour();
        s_min = item.getS_min();
        e_hour = item.getE_hour();
        e_min = item.getE_min();
        wyaCustomDialog = new WYACustomDialog.Builder(context).setLayoutId(
                R.layout.chose_time_layout, v -> {
                    ArrayList<String> data_min = new ArrayList<>();
                    for (int i = 0; i < 60; i++) {
                        data_min.add(i + "");
                    }

                    ArrayList<String> data_hour = new ArrayList<>();
                    for (int i = 0; i < 24; i++) {
                        data_hour.add(i + "");
                    }


                    WheelView start_hour = v.findViewById(R.id.start_hour);
                    start_hour.setData(data_hour);
                    start_hour.setDefault(Integer.valueOf(s_hour).intValue());
                    start_hour.setOnSelectListener(new WheelView.OnSelectListener() {
                        @Override
                        public void endSelect(int id, String text) {
                            s_hour = text;
                        }

                        @Override
                        public void selecting(int id, String text) {

                        }
                    });

                    WheelView start_min = v.findViewById(R.id.start_min);
                    start_min.setData(data_min);
                    start_min.setDefault(Integer.valueOf(s_min).intValue());
                    start_min.setOnSelectListener(new WheelView.OnSelectListener() {
                        @Override
                        public void endSelect(int id, String text) {
                            s_min = text;
                        }

                        @Override
                        public void selecting(int id, String text) {

                        }
                    });

                    WheelView end_hour = v.findViewById(R.id.end_hour);
                    end_hour.setData(data_hour);
                    end_hour.setDefault(Integer.valueOf(e_hour).intValue());
                    end_hour.setOnSelectListener(new WheelView.OnSelectListener() {
                        @Override
                        public void endSelect(int id, String text) {
                            e_hour = text;
                        }

                        @Override
                        public void selecting(int id, String text) {

                        }
                    });

                    WheelView end_min = v.findViewById(R.id.end_min);
                    end_min.setData(data_min);
                    end_min.setDefault(Integer.valueOf(e_min).intValue());
                    end_min.setOnSelectListener(new WheelView.OnSelectListener() {
                        @Override
                        public void endSelect(int id, String text) {
                            e_min = text;
                        }

                        @Override
                        public void selecting(int id, String text) {

                        }
                    });

                    WYAButton sure = v.findViewById(R.id.sure);
                    WYAButton cancel = v.findViewById(R.id.cancel);

                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            item.setS_hour(s_hour);
                            item.setS_min(s_min);
                            item.setE_hour(e_hour);
                            item.setE_min(e_min);
                            bodyData = getTimerTime(true, s_hour, s_min, e_hour, e_min);
                            EasySocket.getInstance().upBytes(bodyData);
                            wyaCustomDialog.dismiss();
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            wyaCustomDialog.dismiss();
                        }
                    });

                }
        ).cancelable(false)
                .cancelTouchout(false)
                .build();
        wyaCustomDialog.show();
    }

    private byte[] getTimerTime(boolean isOpen, String s_hour, String s_min, String e_hour, String e_min) {
        byte[] bodyData = new byte[19];
        bodyData[0] = 0x0d;
        bodyData[1] = 0x00;
        if (isOpen) {
            bodyData[2] = 0x01;
        } else {
            bodyData[2] = 0x00;
        }
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        if (ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900).length == 2) {
            bodyData[3] = ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900)[0];
            bodyData[4] = ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900)[1];
        } else {
            bodyData[3] = ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900)[0];
            bodyData[4] = 0x00;
        }
        bodyData[5] = (byte) (0xff & c.get(Calendar.MONTH));
        bodyData[6] = (byte) (0xff & c.get(Calendar.DATE));
        bodyData[7] = (byte) (0xff & Integer.valueOf(s_hour));
        bodyData[8] = (byte) (0xff & Integer.valueOf(s_min));
        bodyData[9] = (byte) (0xff & 0);
        bodyData[10] = (byte) (0xff & (c.get(Calendar.DAY_OF_WEEK) - 1));

        if (ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900).length == 2) {
            bodyData[11] = ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900)[0];
            bodyData[12] = ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900)[1];
        } else {
            bodyData[11] = ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900)[0];
            bodyData[12] = 0x00;
        }
        bodyData[13] = (byte) (0xff & c.get(Calendar.MONTH));
        bodyData[14] = (byte) (0xff & c.get(Calendar.DATE));
        bodyData[15] = (byte) (0xff & Integer.valueOf(e_hour));
        bodyData[16] = (byte) (0xff & Integer.valueOf(e_min));
        bodyData[17] = (byte) (0xff & 0);
        bodyData[18] = (byte) (0xff & (c.get(Calendar.DAY_OF_WEEK) - 1));

        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        return openFileData;
    }

    int step = 0;

    private byte[] getOpenLamp(boolean open) {
        byte[] bodyData = new byte[4];
        bodyData[0] = 0x01;
        bodyData[1] = (byte) (0xff & step);
        bodyData[2] = (byte) 0x81;
        if (open) {
            bodyData[3] = 0x01;
        } else {
            bodyData[3] = 0x00;
        }
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        return openFileData;
    }


    /**
     * 初始化EasySocket
     */
    private void initEasySocket(String ip) {
        // socket配置
        EasySocketOptions options = new EasySocketOptions.Builder()
                .setSocketAddress(new SocketAddress(ip, TCP_PORT)) // 主机地址
                .setCallbackIdKeyFactory(new CallbackIdKeyFactoryImpl())
                .setReaderProtocol(null)
                .build();

        options.setMessageProtocol(null);
        options.setMaxResponseDataMb(1000000);
        options.setHeartbeatFreq(4000);
        // 初始化EasySocket
        EasySocket.getInstance()
                .options(options) // 项目配置
                .createConnection();// 创建一个socket连接

        // 监听socket行为
        EasySocket.getInstance().subscribeSocketAction(socketActionListener);
    }

    private void toStartHeart() {
        EasySocket.getInstance().startHeartBeat(getBreathData(), new HeartManager.HeartbeatListener() {
            @Override
            public boolean isServerHeartbeat(OriginReadData originReadData) {
                if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == -122) {
                    LogUtil.d("心跳监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private byte[] getBreathData() {
        byte[] bodyData = new byte[1];
        bodyData[0] = 0x06;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] breathData = ByteUtil.byteMerger(send_head_data, bodyData);
        return breathData;
    }

    /**
     * socket行为监听
     */
    private final ISocketActionListener socketActionListener = new SocketActionListener() {
        /**
         * socket连接成功
         * @param socketAddress
         */
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            super.onSocketConnSuccess(socketAddress);
            LogUtil.d("连接成功");
            toStartHeart();
            getLampOpenState();
            getLampTimerState();
            isConnected = true;
        }

        /**
         * socket连接失败
         * @param socketAddress
         * @param isNeedReconnect 是否需要重连
         */
        @Override
        public void onSocketConnFail(SocketAddress socketAddress, Boolean isNeedReconnect) {
            super.onSocketConnFail(socketAddress, isNeedReconnect);
            LogUtil.d("socket连接被断开");
            isConnected = false;
        }

        /**
         * socket断开连接
         * @param socketAddress
         * @param isNeedReconnect 是否需要重连
         */
        @Override
        public void onSocketDisconnect(SocketAddress socketAddress, Boolean isNeedReconnect) {
            super.onSocketDisconnect(socketAddress, isNeedReconnect);
            LogUtil.d("socket断开连接，是否需要重连：" + isNeedReconnect);
            LogUtil.d("socket连接被断开");
            Toast.makeText(context, "socket连接被断开", Toast.LENGTH_SHORT).show();
            isConnected = false;
        }

        /**
         * socket接收的数据
         * @param socketAddress
         * @param originReadData
         */
        @Override
        public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
            super.onSocketResponse(socketAddress, originReadData);
            LogUtil.d("socket监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
            LogUtil.e(originReadData.getBodyData()[originReadData.getBodyData().length - 2] + "-----------");
            LogUtil.e(originReadData.getBodyData()[originReadData.getBodyData().length - 3] + "-----------");
            if (originReadData.getBodyData()[originReadData.getBodyData().length - 2] == -116) {
                LogUtil.e("时间同步");
                if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
                    LogUtil.e("成功");
                } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 3) {
                    LogUtil.e("以公网时间为准");
                } else {
                    LogUtil.e("失败");
                }
            } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 2] == -115) {
                LogUtil.e("定时器设置");
                if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
                    LogUtil.e("成功");
                    Message msg = Message.obtain();
                    msg.what = 3;
                    msg.obj = position;
                    handler.sendMessage(msg);
                } else {
                    LogUtil.e("失败");
                }
            } else if (originReadData.getBodyData().length > 3 && originReadData.getBodyData()[originReadData.getBodyData().length - 3] == (byte) 0x8e) {
                LogUtil.e("获取灯状态");
                if (originReadData.getBodyData()[originReadData.getBodyData().length - 2] == (byte) 0x81) {
                    LogUtil.e("获取灯开关");
                    if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                        LogUtil.e("灯开着的");
                        Message msg = Message.obtain();
                        msg.what = 4;
                        msg.obj = position;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isOpen", true);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
                        LogUtil.e("灯关着的");
                        Message msg = Message.obtain();
                        msg.what = 4;
                        msg.obj = position;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isOpen", false);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    } else {
                        LogUtil.e("无该功能");
                    }
                }
                if (originReadData.getBodyData()[originReadData.getBodyData().length - 2] == (byte) 0x00) {
                    if(musicModel.isClick()){
                        try {
                            EasySocket.getInstance().upBytes(getMusicData(musicModel,  musicModel.isClick()));
                        } catch (Exception e) {
                            LogUtil.e("打开灯光失败");
                        }
                    } else {
                        if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                            LogUtil.e("声控开着的");
                            if(musicModel.getMusic() == 0){
                                try {
                                    EasySocket.getInstance().upBytes(getMusicData(musicModel, musicModel.isClick()));
                                } catch (Exception e) {
                                    LogUtil.e("打开灯光失败");
                                }
                            }
                        } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
                            LogUtil.e("声控关着的");
                            if(musicModel.getMusic() == 1){
                                try {
                                    EasySocket.getInstance().upBytes(getMusicData(musicModel, musicModel.isClick()));
                                } catch (Exception e) {
                                    LogUtil.e("打开灯光失败");
                                }
                            }
                        } else {
                            LogUtil.e("无该功能");
                        }
                    }
                }
            } else if (originReadData.getBodyData().length > 28 && originReadData.getBodyData()[originReadData.getBodyData().length - 28] == (byte) 0x8f) {
                LogUtil.e("获取灯定时状态");
                if (originReadData.getBodyData()[originReadData.getBodyData().length - 26] == 1) {
                    LogUtil.e("灯定时开着的");
                    Message msg = Message.obtain();
                    msg.what = 5;
                    msg.obj = position;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("hasTime", true);
                    bundle.putString("s_hour", originReadData.getBodyData()[originReadData.getBodyData().length - 21] + "");
                    bundle.putString("s_min", originReadData.getBodyData()[originReadData.getBodyData().length - 20] + "");
                    bundle.putString("e_hour", originReadData.getBodyData()[originReadData.getBodyData().length - 13] + "");
                    bundle.putString("e_min", originReadData.getBodyData()[originReadData.getBodyData().length - 12] + "");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 26] == 0) {
                    LogUtil.e("灯定时关着的");
                    Message msg = Message.obtain();
                    msg.what = 5;
                    msg.obj = position;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("hasTime", false);
                    bundle.putString("s_hour", originReadData.getBodyData()[originReadData.getBodyData().length - 21] + "");
                    bundle.putString("s_min", originReadData.getBodyData()[originReadData.getBodyData().length - 20] + "");
                    bundle.putString("e_hour", originReadData.getBodyData()[originReadData.getBodyData().length - 13] + "");
                    bundle.putString("e_min", originReadData.getBodyData()[originReadData.getBodyData().length - 12] + "");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            } else {
                switch (originReadData.getBodyData()[originReadData.getBodyData().length - 3]) {
                    case 0:
                        LogUtil.e("开灯关灯");
                        if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
                            LogUtil.e("成功");
                            Message msg = Message.obtain();
                            msg.what = 1;
                            msg.obj = position;
                            handler.sendMessage(msg);
                        } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                            LogUtil.e("失败");
                        }
                        break;
                    case 1:
                        LogUtil.e("音乐关灯");
                        if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
                            LogUtil.e("成功");
                            Message msg = Message.obtain();
                            msg.what = 2;
                            handler.sendMessage(msg);
                        } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                            LogUtil.e("失败");
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private void getLampTimerState() {
        bodyData = getLampTimer();
        EasySocket.getInstance().upBytes(bodyData);
    }

    private byte[] getLampTimer() {
        byte[] bodyData = new byte[1];
        bodyData[0] = 0x0f;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openStateData = ByteUtil.byteMerger(send_head_data, bodyData);
        return openStateData;
    }

    private void getLampMusicState() {
        bodyData = getLampState((byte) 0x00);
        EasySocket.getInstance().upBytes(bodyData);
    }


    private void getLampOpenState() {
        bodyData = getLampState((byte) 0x81);
        EasySocket.getInstance().upBytes(bodyData);
    }

    private byte[] getLampState(byte function) {
        byte[] bodyData = new byte[2];
        bodyData[0] = 0x0e;
        bodyData[1] = function;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openStateData = ByteUtil.byteMerger(send_head_data, bodyData);
        return openStateData;
    }

    /**
     * 同步时间
     */
    private void toSendTime() {
        bodyData = getTime();
        EasySocket.getInstance().upBytes(bodyData);
    }

    private byte[] getTime() {
        byte[] bodyData = new byte[13];
        bodyData[0] = 0x0c;
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        LogUtil.e(c.get(Calendar.YEAR) + "---" + c.get(Calendar.MONTH) + "---" + c.get(Calendar.DATE) + "---" + c.get(Calendar.HOUR) + "---" + c.get(Calendar.MINUTE) + "---" + c.get(Calendar.SECOND) + "---" + c.get(Calendar.DAY_OF_WEEK));
        if (ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900).length == 2) {
            bodyData[1] = ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900)[0];
            bodyData[2] = ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900)[1];
        } else {
            bodyData[1] = ByteUtil.intToByteArray(c.get(Calendar.YEAR) - 1900)[0];
            bodyData[2] = 0x00;
        }
        bodyData[3] = (byte) (0xff & c.get(Calendar.MONTH));
        bodyData[4] = (byte) (0xff & c.get(Calendar.DATE));
        bodyData[5] = (byte) (0xff & c.get(Calendar.HOUR));
        bodyData[6] = (byte) (0xff & c.get(Calendar.MINUTE));
        bodyData[7] = (byte) (0xff & c.get(Calendar.SECOND));
        bodyData[8] = (byte) (0xff & (c.get(Calendar.DAY_OF_WEEK) - 1));
        //1、取得本地时间：
        java.util.Calendar cal = java.util.Calendar.getInstance();
        //2、取得时间偏移量：
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET) / 1000;
        for (int i = 0; i < 4; i++) {
            if (ByteUtil.intToByteArray(zoneOffset).length > i) {
                bodyData[9 + i] = ByteUtil.intToByteArray(zoneOffset)[i];
            } else {
                bodyData[9 + i] = 0x00;
            }
        }
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        return openFileData;
    }


    int m_step = 1;

    private byte[] getMusicData(MusicModel event, boolean isClick) {
        byte[] bodyData = new byte[4];
        bodyData[0] = 0x01;
        bodyData[1] = (byte) (0xff & m_step);
        bodyData[2] = (byte) 0x00;
        if(isClick){
            if (event.getMusic() == 0) {
                bodyData[3] = 0x01;
            } else {
                bodyData[3] = 0x00;
            }
        } else {
            if (event.getMusic() == 0) {
                bodyData[3] = 0x00;
            } else {
                bodyData[3] = 0x01;
            }
        }
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        return openFileData;
    }

    public void stopTcp() {
        try {
            EasySocket.getInstance().destroyConnection();
        } catch (Exception e) {

        }
    }
}
