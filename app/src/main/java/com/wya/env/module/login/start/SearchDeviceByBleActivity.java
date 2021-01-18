package com.wya.env.module.login.start;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.BindView;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

/**
 * @date: 2020\9\26 0026 18:00
 * @author: Chunjiang Mao
 * @classname: SearchDeviceActivity
 * @describe: 搜索蓝牙设备
 */
public class SearchDeviceByBleActivity extends BaseActivity {

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
    }


    @Override
    protected void onStart() {
        super.onStart();
        //断开所有设备
        Ble.getInstance().disconnectAll();
        lampSettings.clear();
        Ble.getInstance().startScan(scanCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Ble.getInstance().cancelCallback(scanCallback);
    }

    ScheduledExecutorService modelExecutorService;
    private int addMode = 0;

    private void getDevices() {
//        loc_ip = getIpAddressString();
//        addMode = 0;
//        if (modelExecutorService == null) {
//            modelExecutorService = new ScheduledThreadPoolExecutor(1,
//                    new BasicThreadFactory.Builder().namingPattern("getDivice").daemon(true).build());
//        }
//        modelExecutorService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                LogUtil.e("addMode----------" + addMode);
//                addMode++;
//                if (addMode < 15) {
//                    sendData(1);
//                    sendData(2);
//                } else {
//                    stopSendUdpModeData();
//                    if (lampSettings != null && lampSettings.size() > 0) {
//                        LogUtil.e("搜到设备" + lampSettings.size() + "台");
//                    } else {
//                        LogUtil.e("无设备");
//                        startActivity(new Intent(SearchDeviceActivity.this, NoFoundDeviceActivity.class));
//                    }
//                }
//            }
//        }, 0, 200, TimeUnit.MILLISECONDS);


    }


    private BleScanCallback<BleDevice> scanCallback = new BleScanCallback<BleDevice>() {
        @Override
        public void onLeScan(final BleDevice device, int rssi, byte[] scanRecord) {
            LogUtil.e("onLeScan");
            if (device.getBleName() != null && device.getBleName().contains("Delight")) {
//                    synchronized (Ble.getInstance().getLocker()) {
                LogUtil.e(device.getBleAddress() + "------" + device.getBleName() + "--" + device.getBleAlias());
                boolean add = true;
                for (int i = 0; i < lampSettings.size(); i++) {
                    if (TextUtils.equals(lampSettings.get(i).getAddress(), device.getBleAddress())) {
                        add = false;
                        break;
                    }
                }
                LogUtil.e(lampSettings.size() + "-------------" + add);
                if (add) {
                    //连接设备
                    LampSetting lampSetting = new LampSetting();
                    lampSetting.setAddress(device.getBleAddress());
                    lampSetting.setName(device.getBleName());
                    lampSetting.setDeviceName(device.getBleName());
                    lampSettings.add(lampSetting);
                    Ble<BleDevice> ble = Ble.getInstance();
                    if (ble.isScanning()) {
                        ble.stopScan();
                    }
                    startActivity(new Intent(SearchDeviceByBleActivity.this, Start5Activity.class).putExtra("device", device));
//                        deviceAdapter.notifyDataSetChanged();
                }
//                    }
            }

        }

        @Override
        public void onStart() {
            super.onStart();
            tvSearching.setVisibility(View.VISIBLE);
            LogUtil.e("onStart");
        }

        @Override
        public void onStop() {
            super.onStop();
            tvSearching.setVisibility(View.GONE);
            if (lampSettings != null && lampSettings.size() > 0) {
                LogUtil.e("搜到设备" + lampSettings.size() + "台");
            } else {
                LogUtil.e("无设备");
                startActivity(new Intent(SearchDeviceByBleActivity.this, NoFoundDeviceActivity.class));
            }
            LogUtil.e("onStop");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            LogUtil.e("onScanFailed: " + errorCode);
        }
    };

    private BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            LogUtil.e("onConnectionChanged: " + device.getConnectionState());
            if (device.isConnected()) {
                LogUtil.e("连接成功");
                // 连接成功
                startActivity(new Intent(SearchDeviceByBleActivity.this, Start5Activity.class));
            } else if (device.isConnecting()) {
                LogUtil.e("连接中...");
            } else if (device.isDisconnected()) {
                LogUtil.e("未连接...");
            }
        }

        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
        }

        //        @Override
//        public void onConnectFailed(BleDevice device, int errorCode) {
//            super.onConnectFailed(device, errorCode);
//            showShort("连接异常，异常状态码:" + errorCode);
//        }

        @Override
        public void onConnectCancel(BleDevice device) {
            super.onConnectCancel(device);
            LogUtil.e("onConnectCancel: " + device.getBleName());
        }

        @Override
        public void onServicesDiscovered(BleDevice device, BluetoothGatt gatt) {
            super.onServicesDiscovered(device, gatt);

        }

        @Override
        public void onReady(BleDevice device) {
            super.onReady(device);
        }
    };


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
            startActivity(new Intent(SearchDeviceByBleActivity.this, Start4Activity.class).putExtra("name", lampSettings.get(position).getDeviceName()));
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
                            if (lampSettings.get(i).getName().equals(name) && !TextUtils.isEmpty(deviceName) && size > 0) {
                                tvSearching.setVisibility(View.GONE);
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
                            if (!TextUtils.isEmpty(deviceName) && size > 0) {
                                tvSearching.setVisibility(View.GONE);
                                LampSetting lampSetting = new LampSetting();
                                lampSetting.setName(name);
                                lampSetting.setIp(ip);
                                lampSetting.setSize(size);
                                lampSetting.setDeviceName(deviceName);
                                lampSettings.add(lampSetting);
                                deviceAdapter.setNewData(lampSettings);
                            }
                        }
                    } else {
                        String ip = msg.getData().getString("ip");
                        String name = msg.getData().getString("name");
                        int size = msg.getData().getInt("size");
                        String deviceName = msg.getData().getString("deviceName");
                        if (!TextUtils.isEmpty(deviceName) && size > 0) {
                            tvSearching.setVisibility(View.GONE);
                            LampSetting lampSetting = new LampSetting();
                            lampSetting.setName(name);
                            lampSetting.setIp(ip);
                            lampSetting.setSize(size);
                            lampSetting.setDeviceName(deviceName);
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
