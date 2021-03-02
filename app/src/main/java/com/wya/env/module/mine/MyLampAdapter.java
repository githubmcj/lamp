package com.wya.env.module.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.easysocket.EasySocket;
import com.easysocket.config.EasySocketOptions;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.wya.env.App;
import com.wya.env.R;
import com.wya.env.bean.doodle.Doodle;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.bean.event.EventApply;
import com.wya.env.bean.event.EventtDeviceName;
import com.wya.env.bean.event.Hide;
import com.wya.env.bean.event.TcpFail;
import com.wya.env.bean.home.MusicModel;
import com.wya.env.bean.home.MusicSuccess;
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.tcp.DefaultMessageProtocol;
import com.wya.env.bean.tree.TreeData;
import com.wya.env.common.CommonValue;
import com.wya.env.module.login.start.Start1Activity;
import com.wya.env.util.ByteUtil;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.WheelView;
import com.wya.uikit.button.WYAButton;
import com.wya.uikit.dialog.WYACustomDialog;
import com.wya.uikit.pickerview.CustomTimePicker;
import com.wya.utils.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.wya.env.common.CommonValue.TCP_PORT;


/**
 * @date: 2020\9\26 0026 17:19
 * @author: Chunjiang Mao
 * @classname: MyLampAdapter
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

    private MusicModel musicModel;
    private String deviceName;
    private String name;
    private LampSetting lampSetting;
    private EventtDeviceName eventtDeviceName;

    private Hide hide;
    private EventApply eventApply;
    private boolean isClick = false;

    private TreeData treeData;

    /**
     * Sets music model.
     *
     * @param musicModel the music model
     */
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
                    if (musicModel.isClick()) {
                        MusicSuccess musicSuccess = new MusicSuccess();
                        musicSuccess.setPosition(musicModel.getPosition());
                        musicSuccess.setTypeLamp(musicModel.getTypeLamp());
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


    /**
     * @param context
     * @param layoutResId
     * @param data
     */
    public MyLampAdapter(Context context, int layoutResId, @Nullable List<LampSetting> data) {
        super(layoutResId, data);
        this.context = context;
        this.data = data;
        toLinkTcp(false);
    }


    /**
     * 连接tcp
     */
    private int lightType;

    public void toLinkTcp(boolean isClick) {
        this.isClick = isClick;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isChose()) {
                ip = data.get(i).getIp();
                position = i;
                deviceName = data.get(i).getDeviceName();
                name = data.get(i).getName();
                switch (name.substring(5, 6)) {
                    case "C":
                        lightType = 0;
                        break;
                    case "T":
                        lightType = 1;
                        break;
                    default:
                        break;
                }
                lampSetting = data.get(i);
                LogUtil.e("LinkIP:" + ip);
                try {
                    EasySocket.getInstance().destroyConnection();
                } catch (Exception e) {

                }
                initEasySocket(ip);
            }
        }
    }


    private boolean off;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void convert(BaseViewHolder helper, LampSetting item) {
        if (item.getName() == null) {
            helper.setGone(R.id.ll_add, true);
            RxView.clicks(helper.getView(R.id.ll_add))
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(Observable -> {
                        context.startActivity(new Intent(context, Start1Activity.class));
                    });

        } else {
            helper.setGone(R.id.ll_add, false);
//            helper.setText(R.id.name, item.getDeviceName() + "\n" + item.getColumn() + "*" + item.getRow() + "--" + item.getSize() + "\n" + item.getIp() + "---" + item.getColorType() + "--" + item.getName().substring(5, 6));
            helper.setText(R.id.name, item.getDeviceName());
            if (item.isChose() && App.getInstance().isTcpConnected()) {
                helper.getView(R.id.ll_item).setBackground(context.getResources().getDrawable(R.drawable.lamp_pattern_chose_bg));
                helper.getView(R.id.img_open).setEnabled(true);
                helper.getView(R.id.img_time_open).setEnabled(true);
//                if (isClick) {
//                    EventBus.getDefault().post(lampSetting);
//                    isClick = false;
//                }
                SaveSharedPreferences.save(context, CommonValue.IP, item.getIp());
            } else {
                helper.getView(R.id.ll_item).setBackground(context.getResources().getDrawable(R.drawable.lamp_pattern_normal_bg));
                helper.getView(R.id.img_open).setEnabled(false);
                helper.getView(R.id.img_time_open).setEnabled(false);
            }
            if (item.isOpen()) {
                off = false;
                helper.setImageDrawable(R.id.img_open, context.getResources().getDrawable(R.drawable.dengguang));
            } else {
                off = true;
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

            RxView.clicks(helper.getView(R.id.img_open))
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(Observable -> {
                        if (App.getInstance().isTcpConnected()) {
                            hide = new Hide();
                            hide.setHide(false);
                            EventBus.getDefault().post(hide);
                            ip = item.getIp();
                            position = helper.getAdapterPosition();
                            bodyData = getOpenLamp(!item.isOpen());
                            EasySocket.getInstance().upBytes(bodyData);
                        } else {
                            Toast.makeText(context, "Device is Disconnect", Toast.LENGTH_SHORT).show();
                        }
                    });

            RxView.clicks(helper.getView(R.id.img_time_open))
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(Observable -> {
                        if (App.getInstance().isTcpConnected()) {
                            position = helper.getAdapterPosition();
                            if (item.isHasTimer()) {
                                hide = new Hide();
                                hide.setHide(false);
                                EventBus.getDefault().post(hide);
                                bodyData = getTimerTime(false, item.getS_hour(), item.getS_min(), item.getE_hour(), item.getE_min());
                                EasySocket.getInstance().upBytes(bodyData);
                            } else {
                                toSendTime();
                                openChoseTime(item);
                            }
                        } else {
                            Toast.makeText(context, "Device is Disconnect", Toast.LENGTH_SHORT).show();
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

    /**
     * 打开时间选择
     *
     * @param item
     */
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
                            hide = new Hide();
                            hide.setHide(false);
                            EventBus.getDefault().post(hide);
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

    /**
     * 定时开关，TCP传给控制器的数据
     *
     * @param isOpen
     * @param s_hour
     * @param s_min
     * @param e_hour
     * @param e_min
     * @return
     */
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

    /**
     * 开关灯 TCP 传递给控制器数据
     *
     * @param open
     * @return
     */
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
     *
     * @param ip
     */
    private void initEasySocket(String ip) {
        LogUtil.e(ip + "==================" + getIpAddressString());
        if ((ip.split("\\.")[1].equals(getIpAddressString().split("\\.")[1])) && (ip.split("\\.")[0].equals(getIpAddressString().split("\\.")[0]))) {
            // socket配置
            EasySocketOptions options = new EasySocketOptions.Builder()
                    .setSocketAddress(new SocketAddress(ip, TCP_PORT))
                    .setReaderProtocol(new DefaultMessageProtocol())
                    .build();

            options.setMessageProtocol(new DefaultMessageProtocol());
            options.setMaxResponseDataMb(1000000);
            options.setMaxWriteBytes(1472);
            options.setHeartbeatFreq(4000);

            // 初始化EasySocket
            EasySocket.getInstance()
                    .options(options)
                    .createConnection();

            // 监听socket行为
            EasySocket.getInstance().subscribeSocketAction(socketActionListener);
        } else {
            EventBus.getDefault().post(new TcpFail());
        }
    }

    /**
     * @return 本机ip地址
     */
    private String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }


    private void toStartHeart() {
        try {
            start_count++;
            LogUtil.e("发送的心跳数据：" + ByteUtil.byte2hex(getBreathData()));
            EasySocket.getInstance().startHeartBeat(getBreathData(), originReadData -> {
                if (originReadData.getBodyData()[0] == (byte) 0x86) {
                    LogUtil.d("心跳监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
                    return true;
                } else {
                    return false;
                }
            });
        } catch (Exception e) {
            if (start_count < 5) {
                toStartHeart();
            }
        }
    }

    private byte[] getBreathData() {
        byte[] bodyData = new byte[1];
        bodyData[0] = 0x06;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] breathData = ByteUtil.byteMerger(send_head_data, bodyData);
        return breathData;
    }

    private int start_count;

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
            start_count = 0;
            toStartHeart();
            getLampOpenState();
            getLampTimerState();
            if (lightType == 1) {
                frameNum = 0;
                getConfigFile(frameNum);
            }
//            openFileRead();
            App.getInstance().setTcpConnected(true);
            eventtDeviceName = new EventtDeviceName();
            eventtDeviceName.setDeviceName(deviceName);
            eventtDeviceName.setName(name);
            EventBus.getDefault().post(eventtDeviceName);
            MyLampAdapter.this.notifyDataSetChanged();
            hide = new Hide();
            hide.setHide(true);
            EventBus.getDefault().post(hide);
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
            App.getInstance().setTcpConnected(false);
            eventtDeviceName = new EventtDeviceName();
            eventtDeviceName.setDeviceName(null);
            EventBus.getDefault().post(eventtDeviceName);
            MyLampAdapter.this.notifyDataSetChanged();
            hide = new Hide();
            hide.setHide(true);
            EventBus.getDefault().post(hide);
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
            App.getInstance().setTcpConnected(false);
            eventtDeviceName = new EventtDeviceName();
            eventtDeviceName.setDeviceName(null);
            EventBus.getDefault().post(eventtDeviceName);
            MyLampAdapter.this.notifyDataSetChanged();
            hide = new Hide();
            hide.setHide(true);
            EventBus.getDefault().post(hide);
        }

        /**
         * socket接收的数据
         * @param socketAddress
         * @param originReadData
         */
        @Override
        public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
            super.onSocketResponse(socketAddress, originReadData);
            dealSocketResponseData(originReadData);
            hide = new Hide();
            hide.setHide(true);
            EventBus.getDefault().post(hide);
        }
    };

    /**
     * 获取的数据处理
     *
     * @param originReadData TCP 接收的原始数据
     */
    private void dealSocketResponseData(OriginReadData originReadData) {
        LogUtil.d("socket监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
        switch (originReadData.getBodyData()[0]) {
            case (byte) 0x86:
                LogUtil.e("心跳数据");
                break;
            case (byte) 0x8e:
                LogUtil.e("获取灯状态数据");
                switch (originReadData.getBodyData()[1]) {
                    case (byte) 0x81:
                        LogUtil.e("获取灯状态");
                        if (originReadData.getBodyData()[2] == 1) {
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
                        break;

                    case (byte) 0x00:
                        LogUtil.e("获取灯状态");
                        if (musicModel.isClick()) {
                            try {
                                hide = new Hide();
                                hide.setHide(false);
                                EventBus.getDefault().post(hide);
                                EasySocket.getInstance().upBytes(getMusicData(musicModel, musicModel.isClick()));
                            } catch (Exception e) {
                                LogUtil.e("打开声控失败");
                            }
                        } else {
                            if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                                LogUtil.e("声控开着的");
                                if (musicModel.getMusic() == 0) {
                                    try {
                                        hide = new Hide();
                                        hide.setHide(false);
                                        EventBus.getDefault().post(hide);
                                        EasySocket.getInstance().upBytes(getMusicData(musicModel, musicModel.isClick()));
                                    } catch (Exception e) {
                                        LogUtil.e("打开声控失败");
                                    }
                                }
                            } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
                                LogUtil.e("声控关着的");
                                if (musicModel.getMusic() == 1) {
                                    try {
                                        hide = new Hide();
                                        hide.setHide(false);
                                        EventBus.getDefault().post(hide);
                                        EasySocket.getInstance().upBytes(getMusicData(musicModel, musicModel.isClick()));
                                    } catch (Exception e) {
                                        LogUtil.e("打开声控失败");
                                    }
                                }
                            } else {
                                LogUtil.e("无该功能");
                            }
                        }
                        break;
                    default:
                        break;
                }
                break;
            case (byte) 0x8f:
                LogUtil.e("获取定时器状态");
                if (originReadData.getBodyData()[2] == 1) {
                    LogUtil.e("定时器开着的");
                    Message msg = Message.obtain();
                    msg.what = 5;
                    msg.obj = position;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("hasTime", true);
                    bundle.putString("s_hour", originReadData.getBodyData()[7] + "");
                    bundle.putString("s_min", originReadData.getBodyData()[8] + "");
                    bundle.putString("e_hour", originReadData.getBodyData()[15] + "");
                    bundle.putString("e_min", originReadData.getBodyData()[16] + "");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } else if (originReadData.getBodyData()[2] == 0) {
                    LogUtil.e("定时器关着的");
                    Message msg = Message.obtain();
                    msg.what = 5;
                    msg.obj = position;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("hasTime", false);
                    bundle.putString("s_hour", originReadData.getBodyData()[7] + "");
                    bundle.putString("s_min", originReadData.getBodyData()[8] + "");
                    bundle.putString("e_hour", originReadData.getBodyData()[15] + "");
                    bundle.putString("e_min", originReadData.getBodyData()[16] + "");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
                break;
            case (byte) 0x81:
                if (originReadData.getBodyData()[1] == 0) {
                    LogUtil.e("灯光开关灯");
                    hide = new Hide();
                    hide.setHide(true);
                    EventBus.getDefault().post(hide);
                    if (originReadData.getBodyData()[3] == 0) {
                        LogUtil.e("成功");
                        Message msg = Message.obtain();
                        msg.what = 1;
                        msg.obj = position;
                        handler.sendMessage(msg);
                    } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                        LogUtil.e("失败");
                    }
                } else if (originReadData.getBodyData()[1] == 1) {
                    LogUtil.e("灯光声控开关灯");
                    hide = new Hide();
                    hide.setHide(true);
                    EventBus.getDefault().post(hide);
                    if (originReadData.getBodyData()[3] == 0) {
                        LogUtil.e("成功");
                        Message msg = Message.obtain();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    } else if (originReadData.getBodyData()[3] == 1) {
                        LogUtil.e("失败");
                    }
                } else if (originReadData.getBodyData()[1] == (byte) 0x03) {
                    LogUtil.e("打开只读文件");
                    if (originReadData.getBodyData()[3] == 0) {
                        LogUtil.e("成功");
                    } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                        LogUtil.e("失败");
                    }
                } else if (originReadData.getBodyData()[1] == (byte) 0x05) {
                    LogUtil.e("关闭只读文件");
                }
                break;
            case (byte) 0x8d:
                LogUtil.e("定时器设置");
                hide = new Hide();
                hide.setHide(true);
                EventBus.getDefault().post(hide);
                if (originReadData.getBodyData()[1] == 0) {
                    LogUtil.e("成功");
                    Message msg = Message.obtain();
                    msg.what = 3;
                    msg.obj = position;
                    handler.sendMessage(msg);
                } else {
                    LogUtil.e("失败");
                }
                break;
            case (byte) 0x8c:
                if (originReadData.getBodyData()[1] == 0) {
                    LogUtil.e("成功");
                } else if (originReadData.getBodyData()[1] == 1) {
                    LogUtil.e("失败");
                } else if (originReadData.getBodyData()[1] == 2) {
                    LogUtil.e("已被其他用户打开");
                } else if (originReadData.getBodyData()[1] == 3) {
                    LogUtil.e("APP已经打开了一个文件");
                } else {
                    LogUtil.e("失败");
                }
                break;
            case (byte) 0x8b:
                if (originReadData.getBodyData()[1] == 0) {
                    LogUtil.e("成功");
                    if (!frameDataFinish && !modeDataFinish) {
                        LogUtil.e("当前帧和模板数还没传完");
                        LogUtil.e(lampIndex + "----" + index);
                        lampIndex++;
                        eventApply = new EventApply();
                        eventApply.setStatus(1);
                        EventBus.getDefault().post(eventApply);
                        sendFrameApplyData(lampModel, index, lampIndex);
                    } else if (frameDataFinish && !modeDataFinish) {
                        LogUtil.e("当前帧传完，模板数还没传完");
                        LogUtil.e(lampIndex + "----" + index);
                        lampIndex = 0;
                        index++;
                        eventApply = new EventApply();
                        eventApply.setStatus(1);
                        EventBus.getDefault().post(eventApply);
                        sendApplyData(lampModel, index);
                    } else if (frameDataFinish && modeDataFinish) {
                        LogUtil.e("全部传完");
                        LogUtil.e(lampIndex + "----" + index);
                        eventApply = new EventApply();
                        eventApply.setStatus(2);
                        EventBus.getDefault().post(eventApply);
                    } else if (!frameDataFinish && modeDataFinish) {
                        LogUtil.e("最后一个模板的帧");
                        LogUtil.e(lampIndex + "----" + index);
                        lampIndex++;
                        eventApply = new EventApply();
                        eventApply.setStatus(1);
                        EventBus.getDefault().post(eventApply);
                        sendFrameApplyData(lampModel, index, lampIndex);
                    }
                } else {
                    hide = new Hide();
                    hide.setHide(true);
                    EventBus.getDefault().post(hide);
                    LogUtil.e("失败");
                }
                break;
            case (byte) 0x90:
                LogUtil.e("配置文件");
                if (originReadData.getBodyData()[3] == 0) {
                    if (originReadData.getBodyData().length - 6 < 1024) {
                        byte[] body = new byte[originReadData.getBodyData().length - 6];
                        System.arraycopy(originReadData.getBodyData(), 6, body, 0, originReadData.getBodyData().length - 6);
                        configBody = ByteUtil.byteMerger(configBody, body);
                        String config_str = new String(configBody, Charset.forName(EasySocket.getInstance().getOptions().getCharsetName()));
                        SaveSharedPreferences.save(context, CommonValue.CONFIGFILE, config_str);
                        treeData = new Gson().fromJson(config_str, TreeData.class);
                        setSizeRowColumn(treeData);
                    } else {
                        if (frameNum == 0) {
                            byte[] body = new byte[originReadData.getBodyData().length - 6];
                            System.arraycopy(originReadData.getBodyData(), 6, body, 0, originReadData.getBodyData().length - 6);
                            configBody = body;
                        } else {
                            byte[] body = new byte[originReadData.getBodyData().length - 6];
                            System.arraycopy(originReadData.getBodyData(), 6, body, 0, originReadData.getBodyData().length - 6);
                            configBody = ByteUtil.byteMerger(configBody, body);
                        }
                        frameNum++;
                        getConfigFile(frameNum);
                    }

//                    LogUtil.e("成功");
//                    LogUtil.d("配置文件收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
//
//                    System.arraycopy(originReadData.getBodyData(), 4, body, 0, originReadData.getBodyData().length - 6);
//                    String config_str = new String(body, Charset.forName(EasySocket.getInstance().getOptions().getCharsetName()));
//                    LogUtil.e("json：" + config_str);
                } else if (originReadData.getBodyData()[3] == 1) {
                    LogUtil.e("失败");
                } else if (originReadData.getBodyData()[3] == 3) {
                    LogUtil.e("App已经打开一个文件");
                } else {
                    LogUtil.e("失败");
                }
                break;
            default:
                break;
        }
    }

    private Lamps lamps;

    private void setSizeRowColumn(TreeData treeData) {
        lamps = new Gson().fromJson(SaveSharedPreferences.getString(context, CommonValue.LAMPS), Lamps.class);
        for (int i = 0; i < lamps.getLampSettings().size(); i++) {
            if (lamps.getLampSettings().get(i).isChose()) {
                lamps.getLampSettings().get(i).setRow(treeData.getLampYnumber());
                lamps.getLampSettings().get(i).setColumn(treeData.getLampXnumber());
                lamps.getLampSettings().get(i).setSize(treeData.getLampTotalNumber());
                break;
            }
        }
        SaveSharedPreferences.save(context, CommonValue.LAMPS, new Gson().toJson(lamps));
    }

    private void openFileRead() {
        bodyData = getOpenFileReadData();
        EasySocket.getInstance().upBytes(bodyData);
    }

    private void closeFile() {
        bodyData = getCloseFileData();
        EasySocket.getInstance().upBytes(bodyData);
    }

    private byte[] getOpenFileReadData() {
        byte[] bodyData = new byte[4];
        bodyData[0] = 0x01;
        bodyData[1] = (byte) 0x03;
        bodyData[2] = (byte) 0x02;
        bodyData[3] = 0x00;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        return openFileData;
    }

    private byte[] getCloseFileData() {
        byte[] bodyData = new byte[4];
        bodyData[0] = 0x01;
        bodyData[1] = (byte) 0x05;
        bodyData[2] = (byte) 0x02;
        bodyData[3] = 0x02;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        return openFileData;
    }


    private int frameNum;
    private byte[] configBody;

    /**
     * 获取配置文件
     *
     * @param frameNum 帧
     */
    private void getConfigFile(int frameNum) {
        bodyData = getConfigFileData(frameNum);
        EasySocket.getInstance().upBytes(bodyData);
    }


    /**
     * @param frameNum 帧
     * @return
     */
    private byte[] getConfigFileData(int frameNum) {
        byte[] bodyData = new byte[2];
        bodyData[0] = 0x10;
        bodyData[1] = (byte) frameNum;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] configFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        return configFileData;
    }

    /**
     * 获取开关灯时间状态
     */
    private void getLampTimerState() {
        bodyData = getLampTimer();
        EasySocket.getInstance().upBytes(bodyData);
    }

    /**
     * @return TCP 发个控制器的开关灯数据
     */
    private byte[] getLampTimer() {
        byte[] bodyData = new byte[1];
        bodyData[0] = 0x0f;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openStateData = ByteUtil.byteMerger(send_head_data, bodyData);
        return openStateData;
    }

    /**
     * 获取灯光声控状态
     */
    private void getLampMusicState() {
        bodyData = getLampState((byte) 0x00);
        EasySocket.getInstance().upBytes(bodyData);
    }

    private int index;
    private int lampIndex;
    /**
     * 一帧是否传完
     */
    boolean frameDataFinish = false;
    /**
     * 一个模板是否传完
     */
    boolean modeDataFinish = false;
    private LampModel lampModel;
    private int colorType;

    public void apply(LampModel lampModel) {
        colorType = getColorType();
        eventApply = new EventApply();
        eventApply.setStatus(0);
        EventBus.getDefault().post(eventApply);
        this.lampModel = lampModel;
        index = 0;
        modeDataFinish = false;
        sendApplyData(lampModel, index);
    }

    private int getColorType() {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) != null && data.get(i).getName() != null && data.get(i).isChose()) {
                colorType = Integer.valueOf(data.get(i).getColorType());
            }
        }
        return colorType;
    }


    private void sendApplyData(LampModel lampModel, int index) {
        if (index == lampModel.getModeArr().size() - 1) {
            modeDataFinish = true;
            lampIndex = 0;
            frameDataFinish = false;
            sendFrameApplyData(lampModel, index, lampIndex);
        } else {
            lampIndex = 0;
            frameDataFinish = false;
            sendFrameApplyData(lampModel, index, lampIndex);
//            bodyData = applyMode(lampModel.getModeArr().get(index), lampModel.getMirror(), lampModel.getColumn(), lampModel.getSize(), -1, lampModel.getSpeed());
        }
        LogUtil.e(frameDataFinish + "-----" + modeDataFinish + "-----" + index + "-----" + lampIndex + "-----");
    }

    private void sendFrameApplyData(LampModel lampModel, int index, int lampIndex) {
        try {
            bodyData = applyMode(lampModel.getModeArr().get(index), lampModel.getMirror(), lampModel.getColumn(), lampModel.getSize(), index, lampModel.getSpeed(), lampIndex);
            EasySocket.getInstance().upBytes(bodyData);
        } catch (Exception e) {

        }
    }

    private byte[] applyMode(DoodlePattern doodlePattern, int isMirror, int column, int size, int index, int speed, int lampIndex) {
        byte[] headData = new byte[10];
        headData[0] = 0x0b;
        headData[1] = (byte) 0xff;
        if (off) {
            modeDataFinish = true;
            frameDataFinish = true;
        }
        byte[] lampData;
        if (size % 300 == 0) {
            if (lampIndex < size / 300 - 1) {// 当前帧的前面组灯 300 * N
                lampData = getUdpByteData(isMirror == 1 ? toMirror(doodlePattern.getLight_status(), column) : doodlePattern.getLight_status(), lampIndex * 300, 300);
                byte[] start = ByteUtil.intToByteArray(lampIndex * 300);
                if (start.length == 1) {
                    headData[4] = start[0];
                    headData[5] = 0x00;
                } else if (start.length == 2) {
                    headData[4] = start[0];
                    headData[5] = start[1];
                }
                byte[] siz = ByteUtil.intToByteArray(300);
                if (siz.length == 1) {
                    headData[6] = siz[0];
                    headData[7] = 0x00;
                } else if (siz.length == 2) {
                    headData[6] = siz[0];
                    headData[7] = siz[1];
                }
            } else { // 当前帧的最后几个灯
                frameDataFinish = true;
                lampData = getUdpByteData(isMirror == 1 ? toMirror(doodlePattern.getLight_status(), column) : doodlePattern.getLight_status(), lampIndex * 300, size - lampIndex * 300);
                byte[] start = ByteUtil.intToByteArray(lampIndex * 300);
                if (start.length == 1) {
                    headData[4] = start[0];
                    headData[5] = 0x00;
                } else if (start.length == 2) {
                    headData[4] = start[0];
                    headData[5] = start[1];
                }
                byte[] siz = ByteUtil.intToByteArray(size - lampIndex * 300);
                if (siz.length == 1) {
                    headData[6] = siz[0];
                    headData[7] = 0x00;
                } else if (siz.length == 2) {
                    headData[6] = siz[0];
                    headData[7] = siz[1];
                }
            }
        } else {
            if (lampIndex < size / 300) {// 当前帧的前面组灯 300 * N
                lampData = getUdpByteData(isMirror == 1 ? toMirror(doodlePattern.getLight_status(), column) : doodlePattern.getLight_status(), lampIndex * 300, 300);
                byte[] start = ByteUtil.intToByteArray(lampIndex * 300);
                if (start.length == 1) {
                    headData[4] = start[0];
                    headData[5] = 0x00;
                } else if (start.length == 2) {
                    headData[4] = start[0];
                    headData[5] = start[1];
                }
                byte[] siz = ByteUtil.intToByteArray(300);
                if (siz.length == 1) {
                    headData[6] = siz[0];
                    headData[7] = 0x00;
                } else if (siz.length == 2) {
                    headData[6] = siz[0];
                    headData[7] = siz[1];
                }
            } else { // 当前帧的最后几个灯
                frameDataFinish = true;
                lampData = getUdpByteData(isMirror == 1 ? toMirror(doodlePattern.getLight_status(), column) : doodlePattern.getLight_status(), lampIndex * 300, size - lampIndex * 300);
                byte[] start = ByteUtil.intToByteArray(lampIndex * 300);
                if (start.length == 1) {
                    headData[4] = start[0];
                    headData[5] = 0x00;
                } else if (start.length == 2) {
                    headData[4] = start[0];
                    headData[5] = start[1];
                }
                byte[] siz = ByteUtil.intToByteArray(size - lampIndex * 300);
                if (siz.length == 1) {
                    headData[6] = siz[0];
                    headData[7] = 0x00;
                } else if (siz.length == 2) {
                    headData[6] = siz[0];
                    headData[7] = siz[1];
                }
            }
        }

        if (modeDataFinish && frameDataFinish) {
            headData[2] = (byte) 0xff;
            headData[3] = (byte) 0xff;
        } else {
            byte[] len = ByteUtil.intToByteArray(index);
            if (len.length == 1) {
                headData[2] = len[0];
                headData[3] = 0x00;
            } else if (len.length == 2) {
                headData[2] = len[0];
                headData[3] = len[1];
            }
        }
        byte[] time = ByteUtil.intToByteArray(speed);
        if (time.length == 1) {
            headData[8] = time[0];
            headData[9] = 0x00;
        } else if (time.length == 2) {
            headData[8] = time[0];
            headData[9] = time[1];
        }
        byte[] bodyData = ByteUtil.byteMerger(headData, lampData);
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] applyData = ByteUtil.byteMerger(send_head_data, bodyData);
        LogUtil.e(applyData.length + "---");
        String s = ByteUtil.bytesToHex(applyData);
        LogUtil.e(s.length() + "---");
        LogUtil.e(s);
        return applyData;
    }

    /**
     * 获取Udp实时数据
     *
     * @return
     */
    public byte[] getUdpByteData(HashMap<String, Doodle> data, int start, int size) {
        byte[] upd_data;
        if (colorType == 0x04) {
            upd_data = new byte[4 * size];
            for (int i = start; i < size + start; i++) {
                String color = data.get(String.valueOf(i)).getColor();
                boolean isTwinkle = data.get(String.valueOf(i)).isFlash() == 1;
                if (isTwinkle) {
                    if (Math.random() * 10 < 4) {
                        upd_data[i * 4 + 0] = 0x00;
                        upd_data[i * 4 + 1] = 0x00;
                        upd_data[i * 4 + 2] = 0x00;
                        upd_data[i * 4 + 3] = 0x00;
                    } else {
                        upd_data[i * 4 + 0] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
                        upd_data[i * 4 + 1] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
                        upd_data[i * 4 + 2] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));
                        upd_data[i * 4 + 3] = (byte) ByteUtil.intToByteArray(data.get(String.valueOf(i)).getW())[0];
                    }
                } else {
                    upd_data[i * 4 + 0] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
                    upd_data[i * 4 + 1] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
                    upd_data[i * 4 + 2] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));
                    upd_data[i * 4 + 3] = (byte) ByteUtil.intToByteArray(data.get(String.valueOf(i)).getW())[0];
                }
            }
        } else {
            upd_data = new byte[3 * size];
            for (int i = start; i < size + start; i++) {
                String color = data.get(String.valueOf(i)).getColor();
                boolean isTwinkle = data.get(String.valueOf(i)).isFlash() == 1;
                if (isTwinkle) {
                    if (Math.random() * 10 < 4) {
                        upd_data[i * 3 + 0] = 0x00;
                        upd_data[i * 3 + 1] = 0x00;
                        upd_data[i * 3 + 2] = 0x00;
                    } else {
                        upd_data[i * 3 + 0] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
                        upd_data[i * 3 + 1] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
                        upd_data[i * 3 + 2] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));
                    }
                } else {
                    upd_data[i * 3 + 0] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
                    upd_data[i * 3 + 1] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
                    upd_data[i * 3 + 2] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));
                }
            }
        }

        return upd_data;
    }

    HashMap<String, Doodle> mirror_doodles;

    private HashMap<String, Doodle> toMirror(HashMap<String, Doodle> doodles, int column) {
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
     * 获取灯的开关状态
     */
    private void getLampOpenState() {
        bodyData = getLampState((byte) 0x81);
        EasySocket.getInstance().upBytes(bodyData);
    }

    /**
     * @param function 0x00获取灯声控状态； 0x81 获取灯开关状态；
     * @return 传给控制器的数据
     */
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

    /**
     * @return 同步时间数据
     */
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

    /**
     * 传给控制器 声控开关数据
     *
     * @param event
     * @param isClick
     * @return
     */
    private byte[] getMusicData(MusicModel event, boolean isClick) {
        byte[] bodyData = new byte[4];
        bodyData[0] = 0x01;
        bodyData[1] = (byte) (0xff & m_step);
        bodyData[2] = (byte) 0x00;
        if (isClick) {
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

    /**
     * tcp 断开
     */
    public void stopTcp() {
        try {
            EasySocket.getInstance().destroyConnection();
        } catch (Exception e) {
            LogUtil.e("停止TCP失败");
        }
    }
}
