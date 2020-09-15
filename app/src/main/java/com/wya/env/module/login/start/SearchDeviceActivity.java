package com.wya.env.module.login.start;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.bean.login.Lamps;
import com.wya.env.common.CommonValue;
import com.wya.env.net.udp.ICallUdp;
import com.wya.env.net.udp.UdpUtil;
import com.wya.env.util.ByteUtil;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.utils.utils.LogUtil;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wya.env.module.mine.MineFragment.getIpAddressString;

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
    }

    ScheduledExecutorService modelExecutorService;
    private int addMode = 0;

    private void getDevices() {
        loc_ip = getIpAddressString();
        if (modelExecutorService == null) {
            modelExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("getDivice").daemon(true).build());
            modelExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("addMode----------" + addMode);
                    addMode++;
                    if (addMode < 6) {
                        sendData();
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
    }

    @Override
    protected int getLayoutId() {
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
            startActivity(new Intent(SearchDeviceActivity.this, Start4Activity.class));
        });
    }

    private void saveInfoLamp(List<LampSetting> lampSettings) {
        Lamps lamps = new Lamps();
        lamps.setLampSettings(lampSettings);
        for (int i = 0; i < lampSettings.size(); i++) {
            if (lampSettings.get(i).isChose()) {
                lamps.setChose_ip(lampSettings.get(i).getIp());
                lamps.setSize(lampSettings.get(i).getSize());
                lamps.setName(lampSettings.get(i).getName());
            }
        }
        SaveSharedPreferences.save(this, CommonValue.LAMPS, new Gson().toJson(lamps));
    }

    private void sendData() {
        LogUtil.e("发送广播");
        byte[] bytes = new byte[1];
        bytes[0] = 0x00;
        byte[] send_head_data = ByteUtil.getHeadByteData(bytes);
        byte[] send_data = ByteUtil.byteMerger(send_head_data, bytes);
        UdpUtil.send(send_data, loc_ip, new ICallUdp() {
            @Override
            public void start() {
                LogUtil.e("start-----------");
            }

            @Override
            public void success(byte[] data, String ip) {
                Message msg = Message.obtain();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("ip", ip);
                bundle.putInt("size", Integer.parseInt(bytesToHex(data).substring(22, 24) + bytesToHex(data).substring(20, 22), 16));
                bundle.putString("name", new String(getNameData(data)));
                bundle.putString("deviceName", new String(getDeviceNameData(data)).trim());
                msg.setData(bundle);
                handler.sendMessage(msg);
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
                    tvSearching.setVisibility(View.GONE);
                    if (lampSettings != null && lampSettings.size() > 0) {
                        boolean has = false;
                        for (int i = 0; i < lampSettings.size(); i++) {
                            String ip = msg.getData().getString("ip");
                            String name = msg.getData().getString("name");
                            int size = msg.getData().getInt("size");
                            String deviceName = msg.getData().getString("deviceName");
                            if (lampSettings.get(i).getName().equals(name)) {
                                has = true;
                                lampSettings.get(i).setName(name);
                                lampSettings.get(i).setIp(ip);
                                lampSettings.get(i).setSize(size);
                                lampSettings.get(i).setDeviceName(deviceName);
                                deviceAdapter.setNewData(lampSettings);
                                break;
                            }
                        }
                        if (!has) {
                            String ip = msg.getData().getString("ip");
                            String name = msg.getData().getString("name");
                            int size = msg.getData().getInt("size");
                            String deviceName = msg.getData().getString("deviceName");
                            LampSetting lampSetting = new LampSetting();
                            lampSetting.setName(name);
                            lampSetting.setIp(ip);
                            lampSetting.setSize(size);
                            lampSetting.setDeviceName(deviceName);
                            lampSettings.add(lampSetting);
                            deviceAdapter.setNewData(lampSettings);
                        }
                    } else {
                        String ip = msg.getData().getString("ip");
                        String name = msg.getData().getString("name");
                        int size = msg.getData().getInt("size");
                        String deviceName = msg.getData().getString("deviceName");
                        LampSetting lampSetting = new LampSetting();
                        lampSetting.setName(name);
                        lampSetting.setIp(ip);
                        lampSetting.setDeviceName(deviceName);
                        lampSetting.setSize(size);
                        lampSettings.add(lampSetting);
                        deviceAdapter.setNewData(lampSettings);
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
