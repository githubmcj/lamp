package com.wya.env.module.mine;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.wya.env.MainActivity;
import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.manager.ActivityManager;
import com.wya.env.module.login.LoginActivity;
import com.wya.env.module.login.StartUpActivity;
import com.wya.env.util.ByteUtil;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.AvatarImageView;
import com.wya.utils.utils.LogUtil;
import com.wya.utils.utils.ScreenUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
    private int listSize = 10;
    private MyLampAdapter myLampAdapter;
    private LoginInfo loginInfo;

    @Override
    protected int getLayoutResource() {
        return R.layout.two_fragment;
    }

    private void initData() {
        initUserInfo();
        initRecyclerView();
    }

    private void initUserInfo() {
        loginInfo = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LOGIN_INFO), LoginInfo.class);
        userName.setText(loginInfo.getUserName());
        email.setText(loginInfo.getUserEmail());
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        myLampAdapter = new MyLampAdapter(getActivity(), R.layout.lamp_setting_item, lampSettings);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, ScreenUtil.dip2px(getContext(), 10), true));
        recyclerView.setAdapter(myLampAdapter);
    }


    @Override
    protected void initView() {
        Glide.with(getActivity()).load("").apply(new RequestOptions().placeholder(R.drawable.avatar).error(R.drawable.avatar)).into(avatar);
        initData();
    }


    @OnClick({R.id.tab_refresh, R.id.tab_about_us, R.id.tab_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tab_refresh:
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

    private int i;
    private void sendData() {
        showLoading();
        i = 1;
        lampSettings.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bytes = new byte[1];
                    bytes[0] = 0x00;
                    byte[] send_head_data = ByteUtil.getHeadByteData(bytes);
                    byte[] send_data = ByteUtil.byteMerger(send_head_data, bytes);
                    InetAddress inet = InetAddress.getByName("255.255.255.255");
                    DatagramPacket packet = new DatagramPacket(send_data, send_data.length, inet, CommonValue.UDP_PORT);
                    if (datagramSocket == null) {
                        datagramSocket = new DatagramSocket(null);
                        datagramSocket.setReuseAddress(true);
                        datagramSocket.bind(new InetSocketAddress(CommonValue.UDP_PORT));
                    }
                    datagramSocket.setSoTimeout(10000);
                    SocketAddress dd = packet.getSocketAddress();
                    LogUtil.d(dd+"---------------------------");
                    datagramSocket.send(packet);
                    udpReceiver();
                } catch (UnknownHostException e) {
                    hideLoading();
                    showShort(e.toString());
                    e.printStackTrace();
                } catch (SocketException e) {
                    hideLoading();
                    showShort(e.toString());
                    e.printStackTrace();
                } catch (IOException e) {
                    hideLoading();
                    showShort(e.toString());
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private DatagramSocket datagramSocket;

    private void udpReceiver() {
        try {
            if (datagramSocket == null) {
                datagramSocket = new DatagramSocket(null);
                datagramSocket.setReuseAddress(true);
                datagramSocket.bind(new InetSocketAddress(CommonValue.UDP_PORT));
            }
            byte[] buff = new byte[20];
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            datagramSocket.setSoTimeout(10000);
            datagramSocket.receive(packet);
            InetAddress ip = packet.getAddress();
            SocketAddress dd = packet.getSocketAddress();
            LogUtil.d(dd+"---------------------------");
            String ipStr = ip.toString().replace("/", "");
            if (!TextUtils.isEmpty(ipStr)) {
                Message msg = Message.obtain();
                msg.obj = ipStr;
                msg.what = 1;
                handler.sendMessage(msg);
            }
            datagramSocket.close();
            datagramSocket = null;
        } catch (SocketException e) {
            hideLoading();
            showShort(e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            hideLoading();
            showShort(e.toString());
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 1:
                    hideLoading();
                    LampSetting lampSetting = new LampSetting();
                    lampSetting.setName("设备" + i++);
                    lampSetting.setIp(msg.obj.toString());
                    lampSettings.add(lampSetting);
                    myLampAdapter.setNewData(lampSettings);
                    break;
            }
        }
    };


    private void toExit() {
        SaveSharedPreferences.save(getActivity(), CommonValue.IS_LOGIN, false);
        SaveSharedPreferences.save(getActivity(), CommonValue.TOKEN, "");
        SaveSharedPreferences.save(getActivity(), CommonValue.LOGIN_INFO, "");
        if (!ActivityManager.getInstance().leaveFirstActivity()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
        hideLoading();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            sendData();
        }
    }
}
