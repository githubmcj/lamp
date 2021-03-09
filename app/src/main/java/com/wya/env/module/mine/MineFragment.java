package com.wya.env.module.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.wya.env.App;
import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.bean.event.Hide;
import com.wya.env.bean.event.TcpFail;
import com.wya.env.bean.home.MusicModel;
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.module.login.LoginActivity;
import com.wya.env.net.udp.ICallUdp;
import com.wya.env.net.udp.UdpUtil;
import com.wya.env.util.ByteUtil;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.AvatarImageView;
import com.wya.utils.utils.LogUtil;
import com.wya.utils.utils.ScreenUtil;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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

        getDevices();
//        sendData(1);
//        sendData(2);
    }

    ScheduledExecutorService modelExecutorService;
    private int addMode = 0;

    private void getDevices() {
        loc_ip = getIpAddressString();
        addMode = 0;
        if (modelExecutorService == null) {
            modelExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("getDivice").daemon(true).build());
        }
        modelExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("addMode----------" + addMode);
                addMode++;
                if (addMode < 30) {
                    sendData(1);
                } else {
                    stopSendUdpModeData();
                    handler.sendEmptyMessage(5);
                    if (lampSettings != null && lampSettings.size() > 1) {
                        LogUtil.e("搜到设备" + (lampSettings.size() - 1) + "台");
                    } else {
                        LogUtil.e("无设备");
//                        startActivity(new Intent(getActivity(), NoFoundDeviceActivity.class));
                    }
                }
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    public void stopSendUdpModeData() {
        if (modelExecutorService != null) {
            LogUtil.e("停止发送数据");
            modelExecutorService.shutdownNow();
        }
        // 非单例模式，置空防止重复的任务
        modelExecutorService = null;
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
        } else {
            lampSettings = new ArrayList<>();
            lampSettings.add(new LampSetting());
            saveInfoLamp(lampSettings);
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

            } else {
                for (int i = 0; i < lampSettings.size(); i++) {
                    lampSettings.get(i).setChose(false);
                }
                lampSettings.get(position).setChose(true);
                myLampAdapter.setNewData(lampSettings);
                showLoading();
                myLampAdapter.toLinkTcp(true);
                saveInfoLamp(lampSettings);
            }
        });
    }


    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
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
                getDevices();
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
    private void sendData(int type) {
        loc_ip = getIpAddressString();
        new Thread(() -> {
            byte[] send_data;
            if (type == 1) {
                byte[] bytes = new byte[1];
                bytes[0] = 0x00;
                byte[] send_head_data = ByteUtil.getHeadByteData(bytes);
                send_data = ByteUtil.byteMerger(send_head_data, bytes);
            } else {
                byte[] bytes = new byte[1];
                bytes[0] = 0x03;
                byte[] send_head_data = ByteUtil.getHeadByteData(bytes);
                send_data = ByteUtil.byteMerger(send_head_data, bytes);
            }
            UdpUtil.send(send_data, loc_ip, type, new ICallUdp() {
                @Override
                public void start() {
                    LogUtil.e("start-----------");
                }

                @Override
                public void success(byte[] data, String ip, int type) {
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    switch (data[8]) {
                        case (byte) 0x80:
                            LogUtil.e(ByteUtil.bytesToHex(data) + "----------");
                            msg.what = 1;
                            bundle.putString("ip", ip);
                            bundle.putInt("size", Integer.parseInt(bytesToHex(data).substring(22, 24) + bytesToHex(data).substring(20, 22), 16));
                            bundle.putString("name", new String(getNameData(data)));
                            bundle.putString("deviceName", new String(getDeviceNameData(data)).trim());
                            bundle.putString("colorType", bytesToHex(data).substring(18, 20));
                            LogUtil.e(bytesToHex(data).substring(18, 20) + "----------colorType");
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            break;
                        case (byte) 0x83:
                            msg.what = 2;
                            bundle.putString("ip", ip);
                            bundle.putInt("size", Integer.parseInt(bytesToHex(data).substring(20, 22) + bytesToHex(data).substring(18, 20), 16));
                            bundle.putInt("column", Integer.parseInt(bytesToHex(data).substring(24, 26) + bytesToHex(data).substring(22, 24), 16));
                            bundle.putInt("row", Integer.parseInt(bytesToHex(data).substring(28, 30) + bytesToHex(data).substring(26, 28), 16));
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            break;
                    }
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
                    for (int i = 0; i < lampSettings.size(); i++) {
                        if (lampSettings.get(i).getName() == null) {
                            lampSettings.remove(i);
                            i--;
                        }
                    }
                    if (lampSettings != null && lampSettings.size() > 0) {
                        boolean has = false;
                        for (int i = 0; i < lampSettings.size(); i++) {
                            String ip = msg.getData().getString("ip");
                            String name = msg.getData().getString("name");
                            int size = msg.getData().getInt("size");
                            String deviceName = msg.getData().getString("deviceName");
                            String colorType = msg.getData().getString("colorType");
                            LogUtil.e(lampSettings.size() + "---------size-----");
                            if (lampSettings.get(i).getName() != null && lampSettings.get(i).getName().equals(name) && !TextUtils.isEmpty(deviceName) && size > 0) {
                                has = true;
                                lampSettings.get(i).setName(name);
                                lampSettings.get(i).setIp(ip);
                                if (lampSettings.get(i).getName().substring(5, 6).equals("C")) {
                                    lampSettings.get(i).setSize(size);
                                }
                                lampSettings.get(i).setDeviceName(deviceName);
                                lampSettings.get(i).setColorType(colorType);
                                if (i == (lampSettings.size() - 1) && lampSettings.get(lampSettings.size() - 1).getName() != null) {
                                    lampSettings.add(new LampSetting());
                                }
                                myLampAdapter.setNewData(lampSettings);
                            } else {
                                if (i == (lampSettings.size() - 1) && lampSettings.get(lampSettings.size() - 1).getName() != null) {
                                    lampSettings.add(new LampSetting());
                                }
                                myLampAdapter.setNewData(lampSettings);
                            }
                        }
                        if (!has) {
                            String ip = msg.getData().getString("ip");
                            String name = msg.getData().getString("name");
                            int size = msg.getData().getInt("size");
                            String deviceName = msg.getData().getString("deviceName");
                            String colorType = msg.getData().getString("colorType");
                            if (!TextUtils.isEmpty(deviceName) && size > 0) {
                                LampSetting lampSetting = new LampSetting();
                                lampSetting.setName(name);
                                lampSetting.setIp(ip);
                                if (name.substring(5, 6).equals("C")) {
                                    lampSetting.setSize(size);
                                }
                                lampSetting.setDeviceName(deviceName);
                                lampSetting.setColorType(colorType);
                                lampSettings.add(lampSetting);
                                if (lampSettings.get(lampSettings.size() - 1).getName() != null) {
                                    lampSettings.add(new LampSetting());
                                }
                                myLampAdapter.setNewData(lampSettings);
                            } else {
                                if (lampSettings.get(lampSettings.size() - 1).getName() != null) {
                                    lampSettings.add(new LampSetting());
                                }
                                myLampAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        String ip = msg.getData().getString("ip");
                        String name = msg.getData().getString("name");
                        int size = msg.getData().getInt("size");
                        String deviceName = msg.getData().getString("deviceName");
                        String colorType = msg.getData().getString("colorType");
                        if (!TextUtils.isEmpty(deviceName) && size > 0) {
                            LampSetting lampSetting = new LampSetting();
                            lampSetting.setName(name);
                            lampSetting.setIp(ip);
                            if (name.substring(5, 6).equals("C")) {
                                lampSetting.setSize(size);
                            }
                            lampSetting.setDeviceName(deviceName);
                            lampSetting.setColorType(colorType);
                            lampSettings.add(lampSetting);
                            if (lampSettings.get(lampSettings.size() - 1).getName() != null) {
                                lampSettings.add(new LampSetting());
                            }
                            myLampAdapter.setNewData(lampSettings);
                        } else {
                            if (lampSettings.get(lampSettings.size() - 1).getName() != null) {
                                lampSettings.add(new LampSetting());
                            }
                            myLampAdapter.setNewData(lampSettings);
                        }
                    }
                    sendData(2);
//                    hideLoading();
                    break;
                case 2:
                    if (lampSettings != null && lampSettings.size() > 0) {
                        String ip = msg.getData().getString("ip");
                        int column = msg.getData().getInt("column");
                        int row = msg.getData().getInt("row");
                        int size = msg.getData().getInt("size");
                        LogUtil.e(size + "---" + column + "-----" + row);
                        for (int i = 0; i < lampSettings.size(); i++) {
                            if (lampSettings.get(i).getIp() != null && lampSettings.get(i).getIp().equals(ip)) {
                                if (lampSettings.get(i).getName().substring(5, 6).equals("C")) {
                                    if (row > 0) {
                                        lampSettings.get(i).setRow(row);
                                    }
                                    if (column > 0) {
                                        lampSettings.get(i).setColumn(column);
                                    }
                                    if (size > 0) {
                                        lampSettings.get(i).setSize(size);
                                    }
                                }
                                myLampAdapter.setNewData(lampSettings);
                                break;
                            }
                        }
                    }
                    break;
                case 0:
                    if (lampSettings != null && lampSettings.size() > 0) {
                        return;
                    }
                    LogUtil.e("未搜索到设备");
                    break;

                case 5:
                    hideLoading();
                    LogUtil.e("搜索结束");
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
                lamps.setColumn(lampSettings.get(i).getColumn());
                lamps.setRow(lampSettings.get(i).getRow());
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
        myLampAdapter.stopTcp();
        hideLoading();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            saveInfoLamp(lampSettings);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveInfoLamp(lampSettings);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicModel event) {
        if (myLampAdapter != null) {
            myLampAdapter.setMusicModel(event);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LampModel lampModel) {
        if (myLampAdapter != null) {
            myLampAdapter.apply(lampModel);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TcpFail event) {
        try {
            Toast.makeText(getActivity(), "The mobile phone and device are not in the agreed network segment, please check and then reconnect", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < lampSettings.size(); i++) {
                lampSettings.get(i).setChose(false);
            }
            myLampAdapter.setNewData(lampSettings);
            hideLoading();
        } catch (Exception e) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Hide hide) {
        try {
            if (hide.isHide()) {
                hideLoading();
            } else {
                showLoading();
            }
        } catch (Exception e) {

        }
    }

    public void toLinkTcp() {
        if (!App.getInstance().isTcpConnected() && myLampAdapter != null) {
            myLampAdapter.toLinkTcp(false);
            LogUtil.e("toLinkTcp-----------------");
        } else if (App.getInstance().isTcpConnected()) {
            LogUtil.e("Tcp is Connected");
        } else {
            LogUtil.e("myLampAdapter is null");
        }
    }
}
