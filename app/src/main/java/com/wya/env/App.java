package com.wya.env;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.util.DynamicTimeFormatUtil;
import com.wya.env.util.SaveSharedPreferences;

import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.utils.UuidUtils;

/**
 * @date: 2019/1/3 16:19
 * @author: Chunjiang Mao
 * @classname: App
 * @describe:
 */

public class App extends Application {

    private static App INSTANCE;

    public static String TOKEN;

    /**
     * tcp是否连接
     */
    private boolean isTcpConnected = false;


    public boolean isTcpConnected() {
        return isTcpConnected;
    }


    public void setTcpConnected(boolean tcpConnected) {
        isTcpConnected = tcpConnected;
    }


    public static App getInstance() {
        return INSTANCE;
    }

    static {
        //启用矢量图兼容
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            //第一个参数是刷新的背景色，第二个参数是涮选的字体颜色
            layout.setPrimaryColorsId(R.color.refresh_bg, R.color.colorPrimary);
            return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormatUtil("更新于 %s"));
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;

        TOKEN = SaveSharedPreferences.getString(getApplicationContext(), CommonValue.TOKEN);

        // activity manager
        ActivityManager.init(this);

        initBle();

//        // 内存检测
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
        CrashReport.initCrashReport(getApplicationContext(), "83d282ad6a", false);


    }


    private void initBle() {
        Ble.options()//开启配置
                .setLogBleEnable(true)//设置是否输出打印蓝牙日志（非正式打包请设置为true，以便于调试）
                .setThrowBleException(true)//设置是否抛出蓝牙异常 （默认true）
                .setAutoConnect(false)//设置是否自动连接 （默认false）
                .setIgnoreRepeat(false)//设置是否过滤扫描到的设备(已扫描到的不会再次扫描)
                .setConnectTimeout(10 * 1000)//设置连接超时时长（默认10*1000 ms）
                .setMaxConnectNum(7)//最大连接数量
                .setScanPeriod(12 * 1000)//设置扫描时长（默认10*1000 ms）
                .setUuidService(UUID.fromString(UuidUtils.uuid16To128("ABF0")))//设置主服务的uuid（必填）
                .setUuidWriteCha(UUID.fromString(UuidUtils.uuid16To128("ABF1")))//设置可写特征的uuid （必填,否则写入失败）
                .setUuidReadCha(UUID.fromString(UuidUtils.uuid16To128("ABF2")))//设置可读特征的uuid （选填）
//                .setUuidNotifyCha(UUID.fromString(UuidUtils.uuid16To128("ABF2")))//设置可通知特征的uuid （选填，库中默认已匹配可通知特征的uuid）
                .create(this, new Ble.InitCallback() {
                    @Override
                    public void success() {
                        BleLog.e("MainApplication", "初始化成功");
                    }

                    @Override
                    public void failed(int failedCode) {
                        BleLog.e("MainApplication", "初始化失败：" + failedCode);
                    }
                });
    }

}
