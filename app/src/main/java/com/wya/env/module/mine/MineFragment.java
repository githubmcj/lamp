package com.wya.env.module.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.wya.env.App;
import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.bean.event.Hide;
import com.wya.env.bean.home.MusicModel;
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.module.login.LoginActivity;
import com.wya.env.module.login.start.Start1Activity;
import com.wya.env.net.udp.ICallUdp;
import com.wya.env.net.udp.UdpUtil;
import com.wya.env.util.ByteUtil;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.AvatarImageView;
import com.wya.utils.utils.LogUtil;
import com.wya.utils.utils.ScreenUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @date: 2018/7/3 13:55
 * @author: Chunjiang Mao
 * @classname: Fragment2
 * @describe: Example Fragment
 */
public class MineFragment extends BaseMvpFragment<MineFragmentPresenter> implements MineFragmentView {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.avatar)
    AvatarImageView avatar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tab_refresh)
    TableRow tabRefresh;
    @BindView(R.id.tab_about_us)
    TableRow tabAboutUs;
    @BindView(R.id.tab_exit)
    TableRow tabExit;

    private List<LampSetting> lampSettings = new ArrayList<>();
    private MyLampAdapter myLampAdapter;
    private LoginInfo loginInfo;
    private String loc_ip;
    private Lamps lamps;

    @Override
    protected int getLayoutResource() {
        return R.layout.two_fragment;
    }

    private void initData() {
        initUserInfo();
        initLampInfo();
        initRecyclerView();
        showLoading();
        sendData();
    }

    /**
     * 初始化灯数据
     */
    private void initLampInfo() {
        lamps = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LAMPS), Lamps.class);
        if (lamps != null) {
            lampSettings = lamps.getLampSettings();
            if (lampSettings.get(lampSettings.size() - 1).getName() != null) {
                lampSettings.add(new LampSetting());
            }
        }
    }

    /**
     * 初始化个人信息数据
     */
    private void initUserInfo() {
        loginInfo = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LOGIN_INFO), LoginInfo.class);
        userName.setText(loginInfo.getUserName());
        email.setText(loginInfo.getUserEmail());
    }

    /**
     * 初始化设备列表
     */
    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        myLampAdapter = new MyLampAdapter(getActivity(), R.layout.lamp_setting_item, lampSettings);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, ScreenUtil.dip2px(getContext(), 10), true));
        recyclerView.setAdapter(myLampAdapter);
        myLampAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == lampSettings.size() - 1) {
                startActivity(new Intent(getActivity(), Start1Activity.class));
            } else {
                for (int i = 0; i < lampSettings.size(); i++) {
                    lampSettings.get(i).setChose(false);
                }
                lampSettings.get(position).setChose(true);
                myLampAdapter.notifyDataSetChanged();
                showLoading();
                myLampAdapter.toLinkTcp();
                saveInfoLamp(lampSettings);
            }
        });
    }


    @Override
    protected void initView() {
        Glide.with(getActivity()).load("").apply(new RequestOptions().placeholder(R.drawable.avatar).error(R.drawable.avatar)).into(avatar);
        initData();
    }


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


    @OnClick({R.id.tab_refresh, R.id.tab_about_us, R.id.tab_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tab_refresh:
                showLoading();
                sendData();
                break;
            case R.id.tab_exit:
                showLoading();
                Observable.just(1).delay(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                            toExit();
                        });
                break;
            case R.id.tab_about_us:
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
            default:
                break;
        }
    }


    /**
     * 发送UPD数据，搜搜灯设备
     */
    private void sendData() {
        loc_ip = getIpAddressString();
        new Thread(() -> {
            byte[] bytes = new byte[1];
            bytes[0] = 0x00;
            byte[] send_head_data = ByteUtil.getHeadByteData(bytes);
            byte[] send_data = ByteUtil.byteMerger(send_head_data, bytes);
            UdpUtil.send(send_data, loc_ip, new ICallUdp() {
                @Override
                public void start() {
                    LogUtil.e("start-----------");
                }

                @Override
                public void success(byte[] data, String ip) {
                    Message msg = Message.obtain();
                    msg.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("ip", ip);
                    bundle.putInt("size", Integer.parseInt(bytesToHex(data).substring(22, 24) + bytesToHex(data).substring(20, 22), 16));
                    bundle.putString("name", new String(getNameData(data)));
                    bundle.putString("deviceName", new String(getDeviceNameData(data)).trim());
                    msg.setData(bundle);
                    handler.sendMessage(msg);
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
        }).start();
    }

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
            switch (msg.what) {
                case 1://判断标志位
                    if (lampSettings.get(lampSettings.size() - 1).getName() == null) {
                        lampSettings.remove(lampSettings.size() - 1);
                    }
                    if (lampSettings != null && lampSettings.size() > 0) {
                        boolean has = false;
                        for (int i = 0; i < lampSettings.size(); i++) {
                            String ip = msg.getData().getString("ip");
                            String name = msg.getData().getString("name");
                            int size = msg.getData().getInt("size");
                            String deviceName = msg.getData().getString("deviceName");
                            if (lampSettings.get(i).getName().equals(name)) {
                                has = true;
                                lampSettings.get(i).setName(name);
                                lampSettings.get(i).setIp(ip);
                                lampSettings.get(i).setSize(size);
                                lampSettings.get(i).setDeviceName(deviceName);
                                if (lampSettings.get(lampSettings.size() - 1).getName() != null) {
                                    lampSettings.add(new LampSetting());
                                }
                                myLampAdapter.setNewData(lampSettings);
                                break;
                            }
                        }
                        if (!has) {
                            String ip = msg.getData().getString("ip");
                            String name = msg.getData().getString("name");
                            int size = msg.getData().getInt("size");
                            String deviceName = msg.getData().getString("deviceName");
                            LampSetting lampSetting = new LampSetting();
                            lampSetting.setName(name);
                            lampSetting.setIp(ip);
                            lampSetting.setSize(size);
                            lampSetting.setDeviceName(deviceName);
                            lampSettings.add(lampSetting);
                            if (lampSettings.get(lampSettings.size() - 1).getName() != null) {
                                lampSettings.add(new LampSetting());
                            }
                            myLampAdapter.setNewData(lampSettings);
                        }
                    } else {
                        String ip = msg.getData().getString("ip");
                        String name = msg.getData().getString("name");
                        int size = msg.getData().getInt("size");
                        String deviceName = msg.getData().getString("deviceName");
                        LampSetting lampSetting = new LampSetting();
                        lampSetting.setName(name);
                        lampSetting.setIp(ip);
                        lampSetting.setSize(size);
                        lampSetting.setDeviceName(deviceName);
                        lampSettings.add(lampSetting);
                        if (lampSettings.get(lampSettings.size() - 1).getName() != null) {
                            lampSettings.add(new LampSetting());
                        }
                        myLampAdapter.setNewData(lampSettings);
                    }
                    hideLoading();
                    break;
                case 0:
                    hideLoading();
                    if (lampSettings != null && lampSettings.size() > 0) {
                        return;
                    }
                    LogUtil.e("未搜索到设备");
                    break;
                default:
                    hideLoading();
                    break;
            }
        }
    };


    /**
     * 保存灯数据
     *
     * @param lampSettings
     */
    private void saveInfoLamp(List<LampSetting> lampSettings) {
        Lamps lamps = new Lamps();
        lamps.setLampSettings(lampSettings);
        for (int i = 0; i < lampSettings.size(); i++) {
            if (lampSettings.get(i).isChose()) {
                lamps.setChose_ip(lampSettings.get(i).getIp());
                lamps.setSize(lampSettings.get(i).getSize());
                lamps.setName(lampSettings.get(i).getName());
            }
        }
        SaveSharedPreferences.save(getActivity(), CommonValue.LAMPS, new Gson().toJson(lamps));
    }

    /**
     * 退出
     */
    private void toExit() {
        SaveSharedPreferences.save(getActivity(), CommonValue.IS_LOGIN, false);
        SaveSharedPreferences.save(getActivity(), CommonValue.TOKEN, "");
        SaveSharedPreferences.save(getActivity(), CommonValue.LOGIN_INFO, "");
        startActivity(new Intent(getActivity(), LoginActivity.class));
        ActivityManager.getInstance().exitApp();
        hideLoading();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoading();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (!hidden) {
//            showLoading();
//            sendData();
//        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        myLampAdapter.stopTcp();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicModel event) {
        if (myLampAdapter != null) {
            myLampAdapter.setMusicModel(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Hide hide) {
        try {
            hideLoading();
        } catch (Exception e) {

        }
    }

    public void toLinkTcp() {
        if (!App.getInstance().isTcpConnected() && myLampAdapter != null) {
            myLampAdapter.toLinkTcp();
            LogUtil.e("toLinkTcp-----------------");
        } else if (App.getInstance().isTcpConnected()) {
            LogUtil.e("Tcp is Connected");
        } else {
            LogUtil.e("myLampAdapter is null");
        }
    }
}
