package com.wya.env.module.login.start;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easysocket.EasySocket;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.google.gson.Gson;
import com.wya.env.App;
import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.bean.login.Lamps;
import com.wya.env.common.CommonValue;
import com.wya.env.util.ByteUtil;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.uikit.button.WYAButton;
import com.wya.utils.utils.LogUtil;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotifyCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteEntityCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import cn.com.heaton.blelibrary.ble.utils.ByteUtils;
import cn.com.heaton.blelibrary.ble.utils.UuidUtils;

public class LinkActivity extends BaseActivity {
    private static final String TAG = "LinkActivity";


    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_rename)
    EditText etRename;
    @BindView(R.id.but_submit)
    WYAButton butSubmit;
    @BindView(R.id.open)
    WYAButton open;
    private Lamps lamps;

    private boolean isConnected;
    private BleDevice bleDevice;

    @Override
    protected void initView() {
        setTitle("Please type your local WiFi information");
//        initLampInfo();
//        initEasySocket(lamps.getChose_ip());
        setEnableButton(true);
//        getData();
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWifi();
            }
        });
        toConnectBle();
    }

    private void toConnectBle() {
        bleDevice = getIntent().getParcelableExtra("device");
        if (bleDevice == null) {
            return;
        }
        Ble.getInstance().connect(bleDevice, connectCallback);
    }

    private BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            Log.e(TAG, "onConnectionChanged: " + device.getConnectionState());
            if (device.isConnected()) {
                LogUtil.e("已连接");
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

        @Override
        public void onConnectTimeOut(BleDevice device) {
            super.onConnectTimeOut(device);
        }

        @Override
        public void onConnectCancel(BleDevice device) {
            super.onConnectCancel(device);
            Log.e(TAG, "onConnectCancel: " + device.getBleName());
        }

        @Override
        public void onServicesDiscovered(BleDevice device, BluetoothGatt gatt) {
            super.onServicesDiscovered(device, gatt);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    gattServices.addAll(gatt.getServices());
//                    adapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public void onReady(BleDevice device) {
            super.onReady(device);
            //连接成功后，设置通知
            Ble.getInstance().enableNotify(device, true, new BleNotifyCallback<BleDevice>() {
                @Override
                public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                    UUID uuid = characteristic.getUuid();
                    BleLog.e(TAG, "onChanged==uuid:" + uuid.toString());
                    BleLog.e(TAG, "onChanged==data:" + ByteUtils.toHexString(characteristic.getValue()));
                    switch (characteristic.getValue()[8]) {
                        case -128:
                            if (characteristic.getValue()[9] == 0) {
                                connectWifi();
                            }
                            break;
                        case (byte) 0x82:
                            if (characteristic.getValue()[9] == 0) {
                                LogUtil.e("打开热点成功");
                            } else {
                                LogUtil.e("打开热点失败");
                            }
                            break;
                        case (byte) 0x83:
                            if (characteristic.getValue()[9] == 0) {
                                LogUtil.e("连接wifi成功");
                                startActivity(new Intent(LinkActivity.this, SearchDeviceActivity.class));
                                LinkActivity.this.finish();
                            } else {
                                LogUtil.e("连接wifi失败");
                            }
                            break;
                        default:
                            break;
                    }

                }

                @Override
                public void onNotifySuccess(BleDevice device) {
                    super.onNotifySuccess(device);
                    BleLog.e(TAG, "onNotifySuccess: " + device.getBleName());
                }
            });
        }
    };

    private void connectWifi() {
        Ble.getInstance().write(Ble.getInstance().getConnectedDevices().get(0), getConnectWifi(), new BleWriteCallback<BleDevice>() {
            @Override
            public void onWriteSuccess(BleDevice device, BluetoothGattCharacteristic characteristic) {
                byte[] data = characteristic.getValue();
                LogUtil.e("onWriteSuccess: " + ByteUtil.byte2hex(data));
            }

            @Override
            public void onWriteFailed(BleDevice device, int failedCode) {
                LogUtil.e("onWriteFailed: " + failedCode);
            }

        });
    }

    private void openWifi() {
        Ble.getInstance().write(Ble.getInstance().getConnectedDevices().get(0), getOpenWifi(), new BleWriteCallback<BleDevice>() {
            @Override
            public void onWriteSuccess(BleDevice device, BluetoothGattCharacteristic characteristic) {
                byte[] data = characteristic.getValue();
                LogUtil.e("onWriteSuccess: " + ByteUtil.byte2hex(data));
            }

            @Override
            public void onWriteFailed(BleDevice device, int failedCode) {
                LogUtil.e("onWriteFailed: " + failedCode);
            }

        });
    }

    /**
     * 打开wifi
     *
     * @return
     */
    private byte[] getOpenWifi() {
        byte[] bodyData = new byte[1];
        bodyData[0] = 0x02;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        return openFileData;
    }

    /**
     * 连接wifi
     *
     * @return
     */
    private byte[] getConnectWifi() {
        byte[] bodyData = new byte[1];
        bodyData[0] = 0x03;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        return openFileData;
    }

    private void getData() {
        LogUtil.e(Ble.getInstance().getConnectedDevices().size() + "---size------");
//        Ble.getInstance().enableNotify(Ble.getInstance().getConnectedDevices().get(0), true, new BleNotifyCallback<BleDevice>() {
//            @Override
//            public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
//                LogUtil.e("onReadSuccess1: " + ByteUtil.byte2hex(characteristic.getValue()));
//            }
//        });
//
//        Ble.getInstance().read(Ble.getInstance().getConnectedDevices().get(0), new BleReadCallback<BleDevice>() {
//            @Override
//            public void onReadSuccess(BleDevice dedvice, BluetoothGattCharacteristic characteristic) {
//                super.onReadSuccess(dedvice, characteristic);
//                LogUtil.e("onReadSuccess3: " + ByteUtil.byte2hex(characteristic.getValue()));
//            }
//
//            @Override
//            public void onReadFailed(BleDevice device, int failedCode) {
//                super.onReadFailed(device, failedCode);
//                LogUtil.e("onReadFailed: " + failedCode);
//            }
//        });


        Ble.getInstance().enableNotifyByUuid(
                Ble.getInstance().getConnectedDevices().get(0),
                true,
                UUID.fromString(UuidUtils.uuid16To128("ABF0")),
                UUID.fromString(UuidUtils.uuid16To128("ABF1")),
                new BleNotifyCallback<BleDevice>() {
                    @Override
                    public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                        LogUtil.e("onReadSuccess: " + ByteUtil.byte2hex(characteristic.getValue()));
                    }

                    @Override
                    public void onNotifySuccess(BleDevice device) {
                        super.onNotifySuccess(device);
                        LogUtil.e("onNotifySuccess: ");
                    }

                    @Override
                    public void onNotifyCanceled(BleDevice device) {
                        super.onNotifyCanceled(device);
                        LogUtil.e("onNotifyCanceled: ");
                    }
                });
    }

    private void setEnableButton(boolean isConnected) {
        if (butSubmit != null) {
            butSubmit.setEnabled(isConnected);
            if (isConnected) {
                butSubmit.setBackGroundColor(getResources().getColor(R.color.app_blue));
                butSubmit.setBackGroundColorPress(getResources().getColor(R.color.app_blue));
            } else {
                butSubmit.setBackGroundColor(getResources().getColor(R.color.c999999));
                butSubmit.setBackGroundColorPress(getResources().getColor(R.color.c999999));
            }
        }
    }

    private void initLampInfo() {
        lamps = new Gson().fromJson(SaveSharedPreferences.getString(this, CommonValue.LAMPS), Lamps.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_link;
    }


    @OnClick(R.id.but_submit)
    public void onViewClicked() {
//        openWifi();
        if (TextUtils.isEmpty(etAccount.getText().toString())) {
            Toast.makeText(this, etAccount.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] wifi = etAccount.getText().toString().trim().getBytes();
        byte[] data2 = new byte[31];
        for (int i = 0; i < 31; i++) {
            if (wifi.length > i) {
                data2[i] = wifi[i];
            } else {
                data2[i] = 0x00;
            }
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            Toast.makeText(this, etPassword.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] password = etPassword.getText().toString().trim().getBytes();
        byte[] data3 = new byte[31];
        for (int i = 0; i < 31; i++) {
            if (password.length > i) {
                data3[i] = password[i];
            } else {
                data3[i] = 0x00;
            }
        }

        if (TextUtils.isEmpty(etRename.getText().toString())) {
            Toast.makeText(this, etRename.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] name = etRename.getText().toString().trim().getBytes();
        byte[] data4 = new byte[31];
        for (int i = 0; i < 31; i++) {
            if (name.length > i) {
                data4[i] = name[i];
            } else {
                data4[i] = 0x00;
            }
        }

        byte[] initData = getIntData(data2, data3, data4);
        LogUtil.d("initData-----------=" + ByteUtil.byte2hex(initData));


        Ble.getInstance().writeEntity(Ble.getInstance().getConnectedDevices().get(0), initData, 20, 50, new BleWriteEntityCallback<BleDevice>() {
            @Override
            public void onWriteSuccess() {

            }

            @Override
            public void onWriteFailed() {

            }

            @Override
            public void onWriteProgress(double progress) {

            }

            @Override
            public void onWriteCancel() {

            }
        });


//
//        Ble.getInstance().write(Ble.getInstance().getConnectedDevices().get(0), initData, new BleWriteCallback<BleDevice>() {
//            @Override
//            public void onWriteSuccess(BleDevice device, BluetoothGattCharacteristic characteristic) {
//                byte[] data = characteristic.getValue();
//                LogUtil.e("onWriteSuccess: " + ByteUtil.byte2hex(data));
//            }
//
//            @Override
//            public void onWriteFailed(BleDevice device, int failedCode) {
//                LogUtil.e("onWriteFailed: " + failedCode);
//            }
//
//        });


//        EasySocket.getInstance().upBytes(initData);
    }

    private byte[] getIntData(byte[] data2, byte[] data3, byte[] data4) {
        byte[] bodyData = new byte[94];
        bodyData[0] = 0x00;
        for (int i = 0; i < 31; i++) {
            bodyData[i + 1] = data2[i];
        }
        for (int i = 0; i < 31; i++) {
            bodyData[i + 32] = data3[i];
        }
        for (int i = 0; i < 31; i++) {
            bodyData[i + 63] = data4[i];
        }
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] initData = ByteUtil.byteMerger(send_head_data, bodyData);
        return initData;
    }


    /**
     * 初始化EasySocket
     */
    private void initEasySocket(String ip) {
//        // socket配置
//        EasySocketOptions options = new EasySocketOptions.Builder()
//                .setSocketAddress(new SocketAddress(ip, TCP_PORT)) // 主机地址
//                .setReaderProtocol(new DefaultMessageProtocol())
//                .build();
//
//        options.setMessageProtocol(new DefaultMessageProtocol());
//        options.setMaxResponseDataMb(1000000);
//        options.setHeartbeatFreq(4000);
//        // 初始化EasySocket
//        EasySocket.getInstance()
//                .options(options) // 项目配置
//                .createConnection();// 创建一个socket连接
//
//        // 监听socket行为
//        EasySocket.getInstance().subscribeSocketAction(socketActionListener);
    }


    private int start_count;

    private void toStartHeart() {
        try {
            start_count++;
            LogUtil.e("发送的心跳数据：" + ByteUtil.byte2hex(getBreathData()));
            EasySocket.getInstance().startHeartBeat(getBreathData(), originReadData -> {
                if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == -122) {
                    LogUtil.d("心跳监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
                    return true;
                } else {
                    return false;
                }
            });
        } catch (Exception e) {
            LogUtil.e("start_count:" + start_count);
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

    /**
     * socket行为监听
     */
    private ISocketActionListener socketActionListener = new SocketActionListener() {
        /**
         * socket连接成功
         *
         * @param socketAddress
         */
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            super.onSocketConnSuccess(socketAddress);
            LogUtil.d("连接成功");
            getWifiInfo();
            start_count = 0;
            toStartHeart();
            isConnected = true;
            setEnableButton(isConnected);
            App.getInstance().setTcpConnected(true);
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
            setEnableButton(isConnected);
            App.getInstance().setTcpConnected(false);
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
            setEnableButton(isConnected);
            App.getInstance().setTcpConnected(false);
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
            dealSocketResponseData(originReadData);
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
            case (byte) 0x8a:
                LogUtil.e("配置路由器");
                if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
                    LogUtil.e("成功");
                    // 跳转到主界面
                    startActivity(new Intent(LinkActivity.this, Start4Activity.class).putExtra("name", etRename.getText().toString()));
                    finish();
                } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                    LogUtil.e("失败");
                }
                break;
            case (byte) 0x86:
                LogUtil.e("心跳数据");
                break;
            case (byte) 0x89:
                LogUtil.e("获取控制器wifi数据");
                String wifi = new String(getWifiData(originReadData.getBodyData()));
                String password = new String(getPasswordData(originReadData.getBodyData()));
                String name = new String(getNameData(originReadData.getBodyData()));
                if (!TextUtils.isEmpty(wifi)) {
                    etAccount.setText(wifi);
                }
                if (!TextUtils.isEmpty(password)) {
                    etPassword.setText(password);
                }
                if (!TextUtils.isEmpty(name)) {
                    etRename.setText(name);
                }
                break;
            default:
                break;
        }
    }

    private byte[] getWifiData(byte[] data) {
        byte[] name = new byte[31];
        for (int i = 0; i < 31; i++) {
            name[i] = data[i + 1];
        }
        return name;
    }

    private byte[] getPasswordData(byte[] data) {
        byte[] name = new byte[31];
        for (int i = 0; i < 31; i++) {
            name[i] = data[i + 32];
        }
        return name;
    }

    private byte[] getNameData(byte[] data) {
        byte[] name = new byte[31];
        for (int i = 0; i < 31; i++) {
            name[i] = data[i + 63];
        }
        return name;
    }


    private void getWifiInfo() {
        byte[] bodyData = new byte[1];
        bodyData[0] = 0x09;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        EasySocket.getInstance().upBytes(openFileData);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            EasySocket.getInstance().destroyConnection();
        } catch (Exception e) {

        }
    }
}
