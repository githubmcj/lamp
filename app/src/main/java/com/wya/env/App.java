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

//        // 内存检测
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
        CrashReport.initCrashReport(getApplicationContext(), "83d282ad6a", false);
    }

}
