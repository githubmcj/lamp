package com.wya.env.module.login.start;

import android.Manifest;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.wya.env.R;
import com.wya.env.base.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
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

    @Override
    protected void initView() {
        showToolBar(false);
        next.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        checkBlueStatus();
    }

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
    protected int getLayoutId() {
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
