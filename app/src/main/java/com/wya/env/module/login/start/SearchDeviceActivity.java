package com.wya.env.module.login.start;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wya.env.MainActivity;
import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.bean.login.Lamps;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.net.udp.ICallUdp;
import com.wya.env.net.udp.UdpUtil;
import com.wya.env.util.ByteUtil;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.utils.utils.LogUtil;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * @date: 2020\9\26 0026 18:00
 * @author: Chunjiang Mao
 * @classname: SearchDeviceActivity
 * @describe: 搜索设备
 */
public class SearchDeviceActivity extends BaseActivity {

    @BindView(R.id.rv_device)
    RecyclerView recyclerView;
    @BindView(R.id.tv_searching)
    TextView tvSearching;

    private List<LampSetting> lampSettings = new ArrayList<>();
    private DeviceAdapter deviceAdapter;
    private String loc_ip;

    @Override
    protected void initView() {
        setTitle("Devices List");
        initRecyclerView();
    }


    @Override
    protected void onStart() {
        super.onStart();
        getDevices();
        tvSearching.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopSendUdpModeData();
        loadingDialog.dismiss();
    }

    ScheduledExecutorService modelExecutorService;
    private int addMode = 0;

    private void getDevices() {
        loc_ip = getIpAddressString();
        addMode = 0;
        if (modelExecutorService == null) {
            modelExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("getDivice").daemon(true).build());
        }
        modelExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("addMode----------" + addMode);
                addMode++;
                if (addMode < 15) {
                    sendData(1);
                    sendData(2);
                } else {
                    stopSendUdpModeData();
                    if (lampSettings != null && lampSettings.size() > 0) {
                        LogUtil.e("搜到设备" + lampSettings.size() + "台");
                    } else {
                        LogUtil.e("无设备");
                        startActivity(new Intent(SearchDeviceActivity.this, NoFoundDeviceActivity.class));
                    }
                }
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
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

    @Override
    protected int getLayoutID() {
        return R.layout.activity_search_device;
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceAdapter = new DeviceAdapter(this, R.layout.device_item, lampSettings);
        recyclerView.setAdapter(deviceAdapter);
        deviceAdapter.setOnItemClickListener((adapter, view, position) -> {
            for (int i = 0; i < lampSettings.size(); i++) {
                lampSettings.get(i).setChose(false);
            }
            lampSettings.get(position).setChose(true);
            saveInfoLamp(lampSettings);
            loadingDialog.show();
            // 跳转到主界面
            startActivity(new Intent(SearchDeviceActivity.this, MainActivity.class));
            ActivityManager.getInstance().exitApp();
        });
    }

    private void saveInfoLamp(List<LampSetting> lampSettings) {
        Lamps lamps = new Lamps();
        lamps.setLampSettings(lampSettings);
        for (int i = 0; i < lampSettings.size(); i++) {
            if (lampSettings.get(i).isChose()) {
                lamps.setChose_ip(lampSettings.get(i).getIp());
                lamps.setSize(lampSettings.get(i).getSize());
                lamps.setColumn(lampSettings.get(i).getColumn());
                lamps.setRow(lampSettings.get(i).getRow());
                lamps.setName(lampSettings.get(i).getName());
            }
        }
        SaveSharedPreferences.save(this, CommonValue.LAMPS, new Gson().toJson(lamps));
    }


//    private void sendData() throws IOException {
//
//        InetAddress address = InetAddress.getByName(loc_ip);
//        byte[] bytes = new byte[1];
//        bytes[0] = 0x00;
//        byte[] send_head_data = ByteUtil.getHeadByteData(bytes);
//        byte[] send_data = ByteUtil.byteMerger(send_head_data, bytes);
//        // 2.创建数据报，包含发送的数据信息
//        DatagramPacket packet = new DatagramPacket(send_data, send_data.length, address, port);
//        // 3.创建DatagramSocket对象
//        DatagramSocket socket = new DatagramSocket();
//        // 4.向服务器端发送数据报
//        socket.send(packet);
//        // 5.关闭资源
//        socket.close();
//        LogUtil.e("发送搜索广播数据成功");
//    }


    private void sendData(int type) {
        LogUtil.e("发送广播");
        byte[] send_data;
        if (type == 1) {
            byte[] bytes = new byte[1];
            bytes[0] = 0x00;
            byte[] send_head_data = ByteUtil.getHeadByteData(bytes);
            send_data = ByteUtil.byteMerger(send_head_data, bytes);
        } else {
            byte[] bytes = new byte[1];
            bytes[0] = 0x03;
            byte[] send_head_data = ByteUtil.getHeadByteData(bytes);
            send_data = ByteUtil.byteMerger(send_head_data, bytes);
        }
        UdpUtil.send(send_data, loc_ip, type, new ICallUdp() {
            @Override
            public void start() {
                LogUtil.e("start-----------");
            }

            @Override
            public void success(byte[] data, String ip, int type) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                switch (data[8]) {
                    case (byte) 0x80:
                        msg.what = 1;
                        bundle.putString("ip", ip);
                        bundle.putInt("size", Integer.parseInt(bytesToHex(data).substring(22, 24) + bytesToHex(data).substring(20, 22), 16));
                        bundle.putString("name", new String(getNameData(data)));
                        bundle.putString("deviceName", new String(getDeviceNameData(data)).trim());
                        bundle.putString("colorType", bytesToHex(data).substring(18, 20));
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        break;
                    case (byte) 0x83:
                        msg.what = 2;
                        bundle.putString("ip", ip);
                        bundle.putInt("size", Integer.parseInt(bytesToHex(data).substring(20, 22) + bytesToHex(data).substring(18, 20), 16));
                        bundle.putInt("column", Integer.parseInt(bytesToHex(data).substring(24, 26) + bytesToHex(data).substring(22, 24), 16));
                        bundle.putInt("row", Integer.parseInt(bytesToHex(data).substring(28, 30) + bytesToHex(data).substring(26, 28), 16));
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        break;
                }

            }


            @Override
            public void failure(String message) {
                Message msg = Message.obtain();
                msg.what = 0;
                handler.sendMessage(msg);
                LogUtil.e("failure-----------" + message);
            }

            @Override
            public void close() {
                LogUtil.e("close-----------");
            }
        });
    }

    private byte[] getNameData(byte[] data) {
        byte[] name = new byte[32];
        for (int i = 0; i < 32; i++) {
            name[i] = data[i + 20];
        }
        return name;
    }

    private byte[] getDeviceNameData(byte[] data) {
        byte[] deviceName = new byte[32];
        for (int i = 0; i < 32; i++) {
            deviceName[i] = data[i + 52];
        }
        return deviceName;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 1:
                    if (lampSettings != null && lampSettings.size() > 0) {
                        boolean has = false;
                        for (int i = 0; i < lampSettings.size(); i++) {
                            String ip = msg.getData().getString("ip");
                            String name = msg.getData().getString("name");
                            int size = msg.getData().getInt("size");
                            String deviceName = msg.getData().getString("deviceName");
                            String colorType = msg.getData().getString("colorType");
                            LogUtil.e(has + "========" + ip + "-----" + name + "-------" + size + "-------" + deviceName + "-------" + colorType + "-------");
                            if (lampSettings.get(i).getName().equals(name) && !TextUtils.isEmpty(deviceName) && size > 0) {
                                if (tvSearching != null) {
                                    tvSearching.setVisibility(View.GONE);
                                }
                                LogUtil.e("重复" + lampSettings.size());
                                has = true;
                                lampSettings.get(i).setName(name);
                                lampSettings.get(i).setIp(ip);
                                lampSettings.get(i).setSize(size);
                                lampSettings.get(i).setDeviceName(deviceName);
                                lampSettings.get(i).setColorType(colorType);
                                deviceAdapter.setNewData(lampSettings);
                                break;
                            }
                        }
                        if (!has) {
                            String ip = msg.getData().getString("ip");
                            String name = msg.getData().getString("name");
                            int size = msg.getData().getInt("size");
                            String deviceName = msg.getData().getString("deviceName");
                            String colorType = msg.getData().getString("colorType");
                            LogUtil.e(has + "========" + ip + "-----" + name + "-------" + size + "-------" + deviceName + "-------" + colorType + "-------");
                            if (!TextUtils.isEmpty(deviceName) && size > 0) {
                                LogUtil.e("新增" + lampSettings.size());
                                tvSearching.setVisibility(View.GONE);
                                LampSetting lampSetting = new LampSetting();
                                lampSetting.setName(name);
                                lampSetting.setIp(ip);
                                lampSetting.setSize(size);
                                lampSetting.setDeviceName(deviceName);
                                lampSetting.setColorType(colorType);
                                lampSettings.add(lampSetting);
                                deviceAdapter.setNewData(lampSettings);
                            }
                        }
                    } else {
                        String ip = msg.getData().getString("ip");
                        String name = msg.getData().getString("name");
                        int size = msg.getData().getInt("size");
                        String deviceName = msg.getData().getString("deviceName");
                        String colorType = msg.getData().getString("colorType");
                        if (!TextUtils.isEmpty(deviceName) && size > 0) {
                            tvSearching.setVisibility(View.GONE);
                            LampSetting lampSetting = new LampSetting();
                            lampSetting.setName(name);
                            lampSetting.setIp(ip);
                            lampSetting.setSize(size);
                            lampSetting.setDeviceName(deviceName);
                            lampSetting.setColorType(colorType);
                            lampSettings.add(lampSetting);
                            deviceAdapter.setNewData(lampSettings);
                        }
                    }
                    break;
                case 2:
                    if (lampSettings != null && lampSettings.size() > 0) {
                        String ip = msg.getData().getString("ip");
                        int column = msg.getData().getInt("column");
                        int row = msg.getData().getInt("row");
                        int size = msg.getData().getInt("size");
                        LogUtil.e(size + "---" + column + "-----" + row);
                        for (int i = 0; i < lampSettings.size(); i++) {
                            if (lampSettings.get(i).getIp().equals(ip)) {
                                lampSettings.get(i).setRow(row);
                                lampSettings.get(i).setSize(size);
                                lampSettings.get(i).setColumn(column);
                                deviceAdapter.setNewData(lampSettings);
                                break;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 将接收到byte数组转成String字符串
     *
     * @param bytes 接收的byte数组
     * @return string字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public void stopSendUdpModeData() {
        if (modelExecutorService != null) {
            LogUtil.e("停止发送数据");
            modelExecutorService.shutdownNow();
        }
        // 非单例模式，置空防止重复的任务
        modelExecutorService = null;
    }

}
