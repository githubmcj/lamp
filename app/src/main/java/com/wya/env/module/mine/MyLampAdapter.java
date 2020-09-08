package com.wya.env.module.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wya.env.R;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.net.tpc.CallbackIdKeyFactoryImpl;
import com.wya.env.net.tpc.EasySocket;
import com.wya.env.net.tpc.config.EasySocketOptions;
import com.wya.env.net.tpc.entity.OriginReadData;
import com.wya.env.net.tpc.entity.SocketAddress;
import com.wya.env.net.tpc.interfaces.conn.ISocketActionListener;
import com.wya.env.net.tpc.interfaces.conn.SocketActionListener;
import com.wya.env.util.ByteUtil;
import com.wya.uikit.pickerview.CustomTimePicker;
import com.wya.utils.utils.LogUtil;

import java.text.SimpleDateFormat;
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
                default:
                    break;
            }
        }
    };


    public MyLampAdapter(Context context, int layoutResId, @Nullable List<LampSetting> data) {
        super(layoutResId, data);
        this.context = context;
        this.data = data;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void convert(BaseViewHolder helper, LampSetting item) {
        helper.setText(R.id.name, item.getName());
        if (item.isOpen()) {
            helper.getView(R.id.img_open).setBackground(context.getResources().getDrawable(R.drawable.dengguang));
        } else {
            helper.getView(R.id.img_open).setBackground(context.getResources().getDrawable(R.drawable.morenshebei));
        }

        if (item.isHasTimer()) {
            helper.getView(R.id.img_time_open).setBackground(context.getResources().getDrawable(R.drawable.dengguang));
            helper.setVisible(R.id.time, true);
            helper.setText(R.id.time, "定时开：" + item.getStartTime() + "    " + "定时关：" + item.getStopTime());
        } else {
            helper.getView(R.id.img_time_open).setBackground(context.getResources().getDrawable(R.drawable.morenshebei));
            helper.setVisible(R.id.time, false);
        }

        helper.getView(R.id.img_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = item.getIp();
                position = helper.getAdapterPosition();
                bodyData = getOpenLamp(!item.isOpen());
                initEasySocket(item.getIp());
            }
        });

        helper.getView(R.id.img_time_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isHasTimer()) {
                    item.setHasTimer(!item.isHasTimer());
                    MyLampAdapter.this.notifyDataSetChanged();
                } else {
                    openChoseTime();
                }
            }
        });

        helper.getView(R.id.img_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(helper.getAdapterPosition());
                MyLampAdapter.this.notifyDataSetChanged();

//                EasySocket.getInstance().startHeartBeat(getBreathData(), new HeartManager.HeartbeatListener() {
//                    @Override
//                    public boolean isServerHeartbeat(OriginReadData originReadData) {
//                        LogUtil.d("心跳监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
//                        return false;
//                    }
//                });
            }
        });
    }


    private void openChoseTime() {
        mCustomTimePicker = new CustomTimePicker(context, new CustomTimePicker.OnTimePickerSelectedListener() {
            @Override
            public void selected(Date date) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String format = dateFormat.format(date);
//                mYmdhmsText.setText(format);
                LogUtil.e(format);
            }
        });
        mCustomTimePicker.setType(new boolean[]{false, false, false, true, true, false})
                .show();
    }

    private byte[] getBreathData() {
        byte[] bodyData = new byte[1];
        bodyData[0] = 0x06;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] breathData = ByteUtil.byteMerger(send_head_data, bodyData);
        return breathData;
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
        if(isConnected){
            EasySocket.getInstance().disconnect(false);
        }
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


    /**
     * socket行为监听
     */
    private ISocketActionListener socketActionListener = new SocketActionListener() {
        /**
         * socket连接成功
         * @param socketAddress
         */
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            super.onSocketConnSuccess(socketAddress);
            LogUtil.d("连接成功, 并发送数据：" + ByteUtil.byte2hex(bodyData));
            EasySocket.getInstance().upBytes(bodyData);
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
            if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
                LogUtil.e("成功");
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = position;
                handler.sendMessage(msg);
            } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                LogUtil.e("失败");
            }
            EasySocket.getInstance().disconnect(false);
        }
    };


}
