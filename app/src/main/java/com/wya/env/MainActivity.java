package com.wya.env;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.wya.env.base.BaseActivity;
import com.wya.env.bean.home.AddModel;
import com.wya.env.module.doodle.DoodleFragment;
import com.wya.env.module.home.fragment.HomeFragment;
import com.wya.env.module.mine.MineFragment;
import com.wya.env.module.netdata.NetDataFragment;
import com.wya.uikit.tabbar.WYATabBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * @date: 2019/1/9 14:04
 * @author: Chunjiang Mao
 * @classname: MainActivity
 * @describe: MainActivity
 */

public class MainActivity extends BaseActivity {

    @BindView(R.id.tab)
    WYATabBar tab;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment fragment1;
    private NetDataFragment netDataFragment;
    private MineFragment fragment2;
    private DoodleFragment doodleFragment;

    @Override
    protected void initView() {
        initShowToolBar(false);
        initFragment();
        setToolBar();
        getSwipeBackLayout().setEnableGesture(false);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.main_activity;
    }


    private void toLinkTcp() {
        fragment2.toLinkTcp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        toLinkTcp();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddModel event) {
        tab.setSelectedItemId(R.id.navigation_doodle);
    }

    private void initFragment() {
        fragment1 = new HomeFragment();
        fragment2 = new MineFragment();
        netDataFragment = new NetDataFragment();
        doodleFragment = new DoodleFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment1);
        fragmentTransaction.add(R.id.content, netDataFragment);
        fragmentTransaction.add(R.id.content, doodleFragment);
        fragmentTransaction.add(R.id.content, fragment2);
        fragmentTransaction.show(fragment1).hide(netDataFragment).hide(fragment2).hide(doodleFragment).commit();
    }

    private void setToolBar() {
        //取消偏移
        tab.disableShiftMode();
        //取消item动画
        tab.enableAnimation(false);
        //item点击监听
        tab.setOnNavigationItemSelectedListener((MenuItem item) -> {
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.show(fragment1).hide(netDataFragment).hide(fragment2).hide(doodleFragment).commit();
                    break;
                case R.id.navigation_more:
                    fragmentTransaction.show(netDataFragment).hide(doodleFragment).hide(fragment2).hide(fragment1).commit();
                    break;
                case R.id.navigation_doodle:
                    fragmentTransaction.show(doodleFragment).hide(netDataFragment).hide(fragment2).hide(fragment1).commit();
                    break;
                case R.id.navigation_equipment:
                    fragmentTransaction.show(fragment2).hide(netDataFragment).hide(fragment1).hide(doodleFragment).commit();
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    /**
     * 双击返回键退出app
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static boolean isExit = false;

    private void exit() {
        if (!isExit) {
            isExit = true;
            showShort("Press again to exit");
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            this.finish();
//            fragment1.toSendTcpData();
        }
    }

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

}
