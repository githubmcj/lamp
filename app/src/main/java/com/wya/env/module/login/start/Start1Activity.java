package com.wya.env.module.login.start;

import android.Manifest;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.utils.utils.LogUtil;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @date: 2020\9\28 0028 14:15
 * @author: Chunjiang Mao
 * @classname: Start1Activity
 * @describe:
 */
public class Start1Activity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.next)
    TextView next;
    @BindView(R.id.img)
    ImageView img;

    private boolean isBlue;
    private List<LampSetting> lampSettings = new ArrayList<>();

    @Override
    protected void initView() {
        initShowToolBar(false);
        next.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        checkBlueStatus();
        isBlue = false;
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("show_blue").daemon(true).build());
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                isBlue = !isBlue;
                if (isBlue) {
                    img.setImageDrawable(Start1Activity.this.getResources().getDrawable(R.drawable.start2));
                } else {
                    img.setImageDrawable(Start1Activity.this.getResources().getDrawable(R.drawable.start2c));
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

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
                    startActivity(new Intent(Start1Activity.this, Start5Activity.class).putExtra("device", device));
//                        deviceAdapter.notifyDataSetChanged();
                }
//                    }
            }

        }

        @Override
        public void onStart() {
            super.onStart();
            LogUtil.e("onStart");
        }

        @Override
        public void onStop() {
            super.onStop();
            if (lampSettings != null && lampSettings.size() > 0) {
                LogUtil.e("搜到设备" + lampSettings.size() + "台");
            } else {
                LogUtil.e("无设备");
                startActivity(new Intent(Start1Activity.this, NoFoundDeviceActivity.class));
            }
            LogUtil.e("onStop");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            LogUtil.e("onScanFailed: " + errorCode);
        }
    };

    //检查蓝牙是否支持及打开
    private void checkBlueStatus() {
        String[] perms = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "In order to scan Bluetooth Le device more accurately, please turn on GPS positioning", 1, perms);
        }
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_start1;
    }

    @OnClick(R.id.next)
    public void onViewClicked() {
        startActivity(new Intent(Start1Activity.this, Start2Activity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
