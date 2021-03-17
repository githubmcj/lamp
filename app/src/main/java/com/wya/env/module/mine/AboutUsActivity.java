package com.wya.env.module.mine;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.uikit.dialog.WYACustomDialog;
import com.wya.utils.utils.ScreenUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @date: 2020/8/1 15:55
 * @author: Chunjiang Mao
 * @classname: AboutUsActivity
 * @describe: 关于我们
 */
public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.img_hone)
    ImageView imgHone;
    @BindView(R.id.tv_hone)
    TextView tvHone;

    private static final int REQUEST_CALL_PERMISSION = 100;
    private String phone = "123456789";

    @Override
    protected void initView() {
        setTitle(getResources().getString(R.string.about_us));
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_about_us;
    }

    @OnClick({R.id.img_hone, R.id.tv_hone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_hone:
            case R.id.tv_hone:
//                showPhone();
                break;
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                callPhone();
            } else {
                // 不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
            }
        } else {
            callPhone();
        }
    }


    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                // 读写内存权限
                boolean call = writeResult == PackageManager.PERMISSION_DENIED;
                if (call) {
                    callPhone();
                } else {
                    // TODO 跳转权限设置页面
                    Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void showPhone() {
        WYACustomDialog callDialog = new WYACustomDialog.Builder(this)
                .title("提示")
                .message("拨打" + phone)
                .width(ScreenUtil.getScreenWidth(this) * 3 / 4)
                .build();
        callDialog.setNoClickListener(new WYACustomDialog.NoClickListener() {
            @Override
            public void onNoClick() {
                callDialog.dismiss();
            }
        });
        callDialog.setYesClickListener(new WYACustomDialog.YesClickListener() {
            @Override
            public void onYesClick() {
                callDialog.dismiss();
//                checkPermission();
            }
        });
        callDialog.show();

    }

    /**
     * 拨打电话（直接拨打电话）
     */
    public void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        startActivity(intent);
    }
}