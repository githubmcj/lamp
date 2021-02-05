package com.wya.env.module.login.start;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.wya.env.MainActivity;
import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.manager.ActivityManager;
import com.wya.env.util.ByteUtil;
import com.wya.uikit.button.WYAButton;
import com.wya.utils.utils.LogUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotifyCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import cn.com.heaton.blelibrary.ble.utils.ByteUtils;

/**
 * @date: 2020\9\28 0028 14:16
 * @author: Chunjiang Mao
 * @classname: Start5Activity
 * @describe: 
 */
public class Start5Activity extends BaseActivity {

    private static final String TAG = "Start5Activity";

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.use)
    WYAButton use;
    @BindView(R.id.add)
    WYAButton add;
    @BindView(R.id.tv_content)
    TextView tvContent;

    private String address;
    private BleDevice bleDevice;

    @Override
    protected void initView() {
        showToolBar(false);
        address = getIntent().getStringExtra("address");

        bleDevice = getIntent().getParcelableExtra("device");

        RxView.clicks(use)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(Observable -> {
                    Start5Activity.this.startActivity(new Intent(Start5Activity.this, LinkActivity.class).putExtra("device", bleDevice));
                });

        RxView.clicks(add)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(Observable -> {
                    toConnectBle();
//                    startActivity(new Intent(Start5Activity.this, MainActivity.class));
//                    ActivityManager.getInstance().exitApp();
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void toConnectBle() {
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
            if(errorCode == 2030){
                Ble.getInstance().enableNotify(bleDevice, true, new BleNotifyCallback<BleDevice>() {
                    @Override
                    public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                        UUID uuid = characteristic.getUuid();
                        BleLog.e(TAG, "onChanged==uuid:" + uuid.toString());
                        BleLog.e(TAG, "onChanged==data:" + ByteUtils.toHexString(characteristic.getValue()));
                        switch (characteristic.getValue()[8]) {
                            case (byte) 0x82:
                                if (characteristic.getValue()[9] == 0) {
                                    LogUtil.e("打开热点成功");
                                    startActivity(new Intent(Start5Activity.this, SearchDeviceActivity.class));
                                    Start5Activity.this.finish();
                                } else {
                                    LogUtil.e("打开热点失败");
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
                openWifi();
            }
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
            Ble.getInstance().enableNotify(bleDevice, true, new BleNotifyCallback<BleDevice>() {
                @Override
                public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                    UUID uuid = characteristic.getUuid();
                    BleLog.e(TAG, "onChanged==uuid:" + uuid.toString());
                    BleLog.e(TAG, "onChanged==data:" + ByteUtils.toHexString(characteristic.getValue()));
                    switch (characteristic.getValue()[8]) {
                        case (byte) 0x82:
                            if (characteristic.getValue()[9] == 0) {
                                LogUtil.e("打开热点成功");
                                startActivity(new Intent(Start5Activity.this, SearchDeviceActivity.class));
                                Start5Activity.this.finish();
                            } else {
                                LogUtil.e("打开热点失败");
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

            openWifi();
        }
    };




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



    @Override
    protected void onStop() {
        super.onStop();
        use.setEnabled(true);
    }



    @Override
    protected int getLayoutId() {
        return R.layout.activity_start5;
    }

}
