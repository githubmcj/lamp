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
import com.wya.uikit.button.WYAButton;
import com.wya.utils.utils.LogUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotifyCallback;
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


                    // 跳转到主界面
                    startActivity(new Intent(Start5Activity.this, MainActivity.class));
                    ActivityManager.getInstance().exitApp();
                });
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
