package com.wya.env.module.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.LampSetting;
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
    }

    private void initLampInfo() {
        lamps = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LAMPS), Lamps.class);
        lampSettings = lamps.getLampSettings();
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
        myLampAdapter.setOnItemClickListener((adapter, view, position) -> {
            for (int i = 0; i < lampSettings.size(); i++) {
                lampSettings.get(i).setChose(false);
            }
            lampSettings.get(position).setChose(true);
            adapter.notifyDataSetChanged();
        });
    }


    @Override
    protected void initView() {
        Glide.with(getActivity()).load("").apply(new RequestOptions().placeholder(R.drawable.avatar).error(R.drawable.avatar)).into(avatar);
        initData();
    }


    public static String getIpAddressString() {
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


    private void sendData() {
        lampSettings.clear();
        if(myLampAdapter != null){
            myLampAdapter.setNewData(lampSettings);
        }
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
//                    Message msg = Message.obtain();
//                    msg.what = 1;
//                    msg.obj = ip;
//                    msg.arg1 = Integer.parseInt(data.substring(22, 24) + data.substring(20, 22), 16);
//                    handler.sendMessage(msg);
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


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 1:
                    hideLoading();
                    LampSetting lampSetting = new LampSetting();
                    lampSetting.setName("设备");
                    lampSetting.setIp(msg.obj.toString());
                    lampSetting.setSize(msg.arg1);
                    lampSettings.add(lampSetting);
                    myLampAdapter.setNewData(lampSettings);
                    saveInfoLamp(lampSettings);
                    break;
                case 0:
                    if (lampSettings != null && lampSettings.size() > 0) {
                        return;
                    }
                    showShort("未搜索到设备");
                    hideLoading();
                    break;
                default:
                    break;
            }
        }
    };


    private void saveInfoLamp(List<LampSetting> lampSettings) {
        Lamps lamps = new Lamps();
        lamps.setLampSettings(lampSettings);
        if (lampSettings.size() == 1) {
            lamps.setChose_ip(lampSettings.get(0).getIp());
            lamps.setSize(lampSettings.get(0).getSize());
        }
        SaveSharedPreferences.save(getActivity(), CommonValue.LAMPS, new Gson().toJson(lamps));
    }


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
//            showLoading();
//            sendData();
        } else {
            myLampAdapter.stopTcp();
        }
    }
}
