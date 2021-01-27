package com.wya.env.module.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.Doodle;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.bean.doodle.SaveModel;
import com.wya.env.bean.event.EventFavarite;
import com.wya.env.bean.event.EventtDeviceName;
import com.wya.env.bean.home.AddModel;
import com.wya.env.bean.home.MusicSuccess;
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.module.home.detail.DetailActivity;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.LampView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static java.lang.Math.tan;

/**
 * @date: 2018/7/3 13:55
 * @author: Chunjiang Mao
 * @classname: Fragment1
 * @describe: Example Fragment
 */

public class HomeFragment extends BaseMvpFragment<HomeFragmentPresenter> implements HomeFragmentView {

    @BindView(R.id.recyclerView_l)
    RecyclerView recyclerViewL;
    @BindView(R.id.lamp_view)
    LampView lampView;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.tv_local)
    TextView tvLocal;
    @BindView(R.id.tv_favorites)
    TextView tvFavorites;
    @BindView(R.id.tv_cloud)
    TextView tvCloud;
    @BindView(R.id.img_upload)
    ImageView imgUpload;
    Unbinder unbinder;
    @BindView(R.id.recyclerView_f)
    RecyclerView recyclerViewF;
    @BindView(R.id.recyclerView_c)
    RecyclerView recyclerViewC;
    private LampModelAdapter adapterL;
    private LampModelAdapter adapterC;
    private LampModelAdapter adapterF;

    private LoginInfo loginInfo;
    private List<DoodlePattern> doodlePatterns = new ArrayList<>();


    private List<DoodlePattern> choseModel;
    private String deviceName;
    /**
     * 0 local 1 favorite 2 cloud
     */
    private int typeLamp;


    private HomeFragmentPresenter homeFragmentPresenter = new HomeFragmentPresenter();

    private String[] snow_colors = {"#ffffff", "#B04F9C", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000"};
    private String[] fifth_colors = {"#FA0000", "#FAA500", "#00FF00"};

    private Lamps lamps;
    private List<LampModel> lampModelsL = new ArrayList<>();
    private List<LampModel> lampModelsF = new ArrayList<>();
    private List<LampModel> lampModelsF_C = new ArrayList<>();
    private List<LampModel> lampModelsC = new ArrayList<>();

    int column;
    int size;
    int row;


    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
        homeFragmentPresenter.mView = this;
        if (isVisible) {
            initData();//初始化数据
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isVisible()) {
            initSendData();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        lampView.toStopSendUdpModeData(true, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicSuccess event) {
        switch (event.getTypeLamp()) {
            case 0:
                lampModelsL.get(event.getPosition()).setMusic(1 - lampModelsL.get(event.getPosition()).isMusic());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SaveSharedPreferences.save(getActivity(), CommonValue.LOGIN_INFO, new Gson().toJson(loginInfo));
                    }
                });
                break;
            case 1:
                lampModelsF.get(event.getPosition()).setMusic(1 - lampModelsL.get(event.getPosition()).isMusic());
                break;
            case 2:
                lampModelsC.get(event.getPosition()).setMusic(1 - lampModelsL.get(event.getPosition()).isMusic());
                break;
            default:
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventtDeviceName eventtDeviceName) {
        deviceName = eventtDeviceName.getDeviceName() == null ? "device name" : eventtDeviceName.getDeviceName();
        name.setText(deviceName);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LampSetting lampSetting) {
//        getLocalData(true);
//        getNetData();
//        initRecyclerView();
    }


    private boolean changeFavorite;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventFavarite eventFavarite) {
        changeFavorite = true;
        switch (eventFavarite.getTypeLamp()) {
            case 0:
                if (eventFavarite.isFavorite()) {
                    lampModelsF.add(lampModelsL.get(eventFavarite.getPosition()));
                } else {
                    for (int i = 0; i < lampModelsF.size(); i++) {
                        if (eventFavarite.getCreatTime().equals(lampModelsF.get(i).getCreatTime())) {
                            lampModelsF.remove(i);
                            break;
                        }
                    }
                }
                break;
            case 1:
                if (eventFavarite.isFavorite()) {
                    lampModelsF.add(lampModelsL.get(eventFavarite.getPosition()));
                } else {
                    for (int i = 0; i < lampModelsF.size(); i++) {
                        if (eventFavarite.getCreatTime().equals(lampModelsF.get(i).getCreatTime())) {
                            lampModelsF.remove(i);
                            break;
                        }
                    }
                }
                adapterF.setNewData(lampModelsF);
                break;
            case 2:
                if (eventFavarite.isFavorite()) {
                    lampModelsF.add(lampModelsC.get(eventFavarite.getPosition()));
                } else {
                    for (int i = 0; i < lampModelsF.size(); i++) {
                        if (eventFavarite.getCreatTime().equals(lampModelsF.get(i).getCreatTime())) {
                            lampModelsF.remove(i);
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SaveSharedPreferences.save(getActivity(), CommonValue.FAVORITE, new Gson().toJson(lampModelsF));
            }
        }).start();
    }


    private void initData() {
        initRecyclerView();
        getLocalData(false);
        getNetData();
    }

    private void initSendData() {
        for (int i = 0; i < lampModelsL.size(); i++) {
            if (lampModelsL.get(i).isChose() == 1) {
                lampView.setMirror(lampModelsL.get(i).getMirror());
                lampView.setModel(lampModelsL.get(i).getModeArr(), lampModelsL.get(i).getLight(), true);
            }
        }
    }

    private void getNetData() {
        if (!getIpAddressString().contains("192.168.4.")) {
            homeFragmentPresenter.getSaveModels();
        }
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

    private void getLocalData(boolean refresh) {
        showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                loginInfo = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LOGIN_INFO), LoginInfo.class);
                lampModelsL = loginInfo.getLampModels();
                lamps = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LAMPS), Lamps.class);
                column = 20;
                row = 15;
                size = 300;
                handler.sendEmptyMessage(refresh ? 1 : 2);
            }
        }).start();
    }


    private void initRecyclerView() {
        recyclerViewL.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapterL = new LampModelAdapter(getActivity(), R.layout.lamp_pattern_item, lampModelsL);
        recyclerViewL.setHasFixedSize(true);
        recyclerViewL.setNestedScrollingEnabled(false);
        recyclerViewL.setAdapter(adapterL);
        // RecyclerView条目点击事件
        adapterL.setOnItemClickListener((adapter, view, position) -> {
            if (position == lampModelsL.size() - 1) {
                EventBus.getDefault().post(new AddModel());
            } else {
                startActivity(new Intent(getActivity(), DetailActivity.class)
                        .putExtra("position", position)
                        .putExtra("title", deviceName)
                        .putExtra("id", lampModelsL.get(position).getId())
                        .putExtra("typeLamp", typeLamp)
                        .putExtra("createTime", lampModelsL.get(position).getCreatTime())
                        .putExtra("music", lampModelsL.get(position).isMusic()));

//                if (lampModels.get(position).isChose() != 1) {
//                    choseModel = lampModels.get(position).getModeArr();
//                    for (int i = 0; i < lampModels.size(); i++) {
//                        lampModels.get(i).setChose(0);
//                    }
//                    lampModels.get(position).setChose(1);
//                    adapter.notifyDataSetChanged();
//
//                    lampView.setMirror(lampModels.get(position).getMirror());
//                    lampView.setModel(lampModels.get(position).getModeArr(), lampModels.get(position).getLight(), true);
//                } else {
//                    LogUtil.e("该模板已经选中");
//                }

            }
//            setTcpData(lampModels.get(position).getModeArr());
        });

        lampModelsF = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.FAVORITE), new TypeToken<List<LampModel>>() {
        }.getType());
        if (lampModelsF == null) {
            lampModelsF = new ArrayList<>();
        }
        recyclerViewF.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapterF = new LampModelAdapter(getActivity(), R.layout.lamp_pattern_item, lampModelsF);
        recyclerViewF.setHasFixedSize(true);
        recyclerViewF.setNestedScrollingEnabled(false);
        recyclerViewF.setAdapter(adapterF);
        // RecyclerView条目点击事件
        adapterF.setOnItemClickListener((adapter, view, position) -> {
            startActivity(new Intent(getActivity(), DetailActivity.class)
                    .putExtra("position", position)
                    .putExtra("id", lampModelsF.get(position).getId())
                    .putExtra("title", deviceName)
                    .putExtra("typeLamp", typeLamp)
                    .putExtra("createTime", lampModelsF.get(position).getCreatTime())
                    .putExtra("music", lampModelsF.get(position).isMusic()));
        });

        recyclerViewC.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapterC = new LampModelAdapter(getActivity(), R.layout.lamp_pattern_item, lampModelsC);
        recyclerViewC.setHasFixedSize(true);
        recyclerViewC.setNestedScrollingEnabled(false);
        recyclerViewC.setAdapter(adapterC);

        adapterC.setOnItemClickListener((adapter, view, position) -> {
            startActivity(new Intent(getActivity(), DetailActivity.class)
                    .putExtra("position", position)
                    .putExtra("id", lampModelsC.get(position).getId())
                    .putExtra("title", deviceName)
                    .putExtra("typeLamp", typeLamp)
                    .putExtra("createTime", lampModelsC.get(position).getCreatTime())
                    .putExtra("music", lampModelsC.get(position).isMusic()));
        });

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.home_fragment;
    }

    @Override
    protected void initView() {
        homeFragmentPresenter.mView = this;
        lampView.setFocusable(false);
        EventBus.getDefault().register(this);
        typeLamp = 0;
        setButton(typeLamp);
        initData();//初始化数据
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        boolean toRefresh = SaveSharedPreferences.getBoolean(getActivity(), CommonValue.TO_REFRESH);
        if (!hidden) {
            if (toRefresh) {
                SaveSharedPreferences.save(getActivity(), CommonValue.TO_REFRESH, false);
                getLocalData(false);
                getNetData();
            } else {
                initSendData();
            }
        } else {
            lampView.toStopSendUdpModeData(true, true);
            lampView.stopSendUdpData();
        }
    }

    @Override
    public void onModelsResult(List<SaveModel> data) {
        lampModelsC.clear();
        for (int i = 0; i < data.size(); i++) {
            LampModel lampModel = new Gson().fromJson(data.get(i).getContent(), LampModel.class);
            for (int j = 0; j < lampModel.getModeArr().size(); j++) {
                DoodlePattern doodlePattern = lampModel.getModeArr().get(j);
                for (int k = 0; k < doodlePattern.getSize(); k++) {
                    if (doodlePattern.getLight_status().get(String.valueOf(k)) == null) {
                        Doodle doodle = new Doodle();
                        doodle.setColor("#000000");
                        doodle.setFlash(0);
                        doodlePattern.getLight_status().put(String.valueOf(k), doodle);
                    }
                }
            }
            lampModelsC.add(lampModel);
        }
        adapterC.setNewData(lampModelsC);

        if (lampModelsL.get(lampModelsL.size() - 1).getName() == null) {
            lampModelsL.remove(lampModelsL.size() - 1);
        }
        lampModelsL.addAll(lampModelsC);
        if (lampModelsL.get(lampModelsL.size() - 1).getName() != null) {
            lampModelsL.add(new LampModel());
        }
        if (adapterL != null) {
            adapterL.setNewData(lampModelsL);
        }
    }

//
//    /**
//     * 发送tcp动画数据
//     */
//    private Lamps lamps;
//    private WYACustomDialog dialog;
//
//    public void toSendTcpData() {
//        dialog = new WYACustomDialog.Builder(getActivity())
//                .title("提示")
//                .message("是否继续播放窗帘灯？")
//                .width(ScreenUtil.getScreenWidth(getActivity()) * 3 / 4)
//                .build();
//        dialog.setNoClickListener(new WYACustomDialog.NoClickListener() {
//            @Override
//            public void onNoClick() {
//                getActivity().finish();
//                dialog.dismiss();
//            }
//        });
//        dialog.setYesClickListener(new WYACustomDialog.YesClickListener() {
//            @Override
//            public void onYesClick() {
//                lampView.toStopSendUdpModeData(true, false);
//                lamps = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LAMPS), Lamps.class);
//                size = lamps.getSize();
//                initEasySocket(lamps.getChose_ip());
//            }
//        });
//        dialog.show();
//    }
//
//
//    private boolean isConnected;
//
//    /**
//     * 初始化EasySocket
//     */
//    private void initEasySocket(String ip) {
//        if (isConnected) {
//            EasySocket.getInstance().disconnect(false);
//        }
//        // socket配置
//        EasySocketOptions options = new EasySocketOptions.Builder()
//                .setSocketAddress(new SocketAddress(ip, TCP_PORT)) // 主机地址
//                .setReaderProtocol(new DefaultMessageProtocol())
//                .build();
//
//        options.setMessageProtocol(new DefaultMessageProtocol());
//        options.setHeartbeatFreq(2000);
//        // 初始化EasySocket
//        EasySocket.getInstance()
//                .options(options) // 项目配置
//                .createConnection();// 创建一个socket连接
//
//        // 监听socket行为
//        EasySocket.getInstance().subscribeSocketAction(socketActionListener);
//    }
//
//
//    private ISocketActionListener socketActionListener = new SocketActionListener() {
//        /**
//         * socket连接成功
//         * @param socketAddress
//         */
//        @Override
//        public void onSocketConnSuccess(SocketAddress socketAddress) {
//            super.onSocketConnSuccess(socketAddress);
//            LogUtil.d("连接成功, 并发送数据：");
//            if (step == 0) {
//                toStartHeart();
////                EasySocket.getInstance().upBytes(getOpenFileData(true));
//            } else if (step == 1) {
//
//            }
//
//            isConnected = true;
//        }
//
//        /**
//         * socket连接失败
//         * @param socketAddress
//         * @param isNeedReconnect 是否需要重连
//         */
//        @Override
//        public void onSocketConnFail(SocketAddress socketAddress, Boolean isNeedReconnect) {
//            super.onSocketConnFail(socketAddress, isNeedReconnect);
//            LogUtil.d("socket连接被断开");
//            isConnected = false;
//        }
//
//        /**
//         * socket断开连接
//         * @param socketAddress
//         * @param isNeedReconnect 是否需要重连
//         */
//        @Override
//        public void onSocketDisconnect(SocketAddress socketAddress, Boolean isNeedReconnect) {
//            super.onSocketDisconnect(socketAddress, isNeedReconnect);
//            LogUtil.d("socket断开连接，是否需要重连：" + isNeedReconnect);
//            LogUtil.d("socket连接被断开");
//            isConnected = false;
//        }
//
//        /**
//         * socket接收的数据
//         * @param socketAddress
//         * @param originReadData
//         */
//        @Override
//        public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
//            super.onSocketResponse(socketAddress, originReadData);
//            LogUtil.d("socket监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
//            if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
//                LogUtil.e("成功");
//                Message msg = Message.obtain();
//                msg.what = 1;
//                handler.sendMessage(msg);
//            } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
//                LogUtil.e("失败");
//            } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0x86) {
//                LogUtil.e("心跳数据");
//            } else {
//                LogUtil.e("其他数据");
//            }
//        }
//    };
//
//    private void toStartHeart() {
//        EasySocket.getInstance().startHeartBeat(getBreathData(), new HeartManager.HeartbeatListener() {
//            @Override
//            public boolean isServerHeartbeat(OriginReadData originReadData) {
//                LogUtil.d("心跳监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
//                return false;
//            }
//        });
//    }
//
//    private byte[] getBreathData() {
//        byte[] bodyData = new byte[1];
//        bodyData[0] = 0x06;
//        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
//        byte[] breathData = ByteUtil.byteMerger(send_head_data, bodyData);
//        return breathData;
//    }
//
//
//    /**
//     * 打开文件
//     */
//
//    int step = 0;
//    int modelIndex = 0;
//    int fileIndex = 3;
//
//    private byte[] getOpenFileData(boolean isOpen) {
//        byte[] bodyData = new byte[4];
//        bodyData[0] = 0x01;
//        bodyData[1] = (byte) (0xff & step);
//        bodyData[2] = (byte) (0xff & fileIndex);
//        if (isOpen) {
//            bodyData[3] = 0x01;
//        } else {
//            bodyData[3] = 0x02;
//        }
//        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
//        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
//        LogUtil.e("openFileData:" + ByteUtil.byte2hex(openFileData));
//        return openFileData;
//    }
//
//
//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {      //判断标志位
//                case 1:
//                    switch (step) {
//                        case 0:
//                            step = 1;// 送模板数据
//                            modelIndex = 0;
//                            byte[] body_data = getTcpByteData(choseModel.get(modelIndex).getLight_status());
//                            byte[] send_head_data = ByteUtil.getHeadByteData(body_data);
//                            byte[] send_data = ByteUtil.byteMerger(send_head_data, body_data);
//                            EasySocket.getInstance().upBytes(send_data);
//                            break;
//                        case 1:
//                            modelIndex++;
//                            if (modelIndex == choseModel.size()) {
//                                step = 2;
//                                EasySocket.getInstance().upBytes(getOpenFileData(false));
//                            } else {
//                                byte[] body_data2 = getTcpByteData(choseModel.get(modelIndex).getLight_status());
//                                byte[] send_head_data2 = ByteUtil.getHeadByteData(body_data2);
//                                byte[] send_data2 = ByteUtil.byteMerger(send_head_data2, body_data2);
//                                EasySocket.getInstance().upBytes(send_data2);
//                            }
//                            break;
//                        case 2:
//                            dialog.dismiss();
//                            LogUtil.e("传输完成");
//                            break;
//
//                        default:
//                            break;
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    public byte[] getTcpByteData(HashMap<String, Doodle> data) {
//        byte[] tcp_data = new byte[1 + 2 + 2 + 3 * size];
//        tcp_data[0] = 0x01;
//        tcp_data[1] = 0x00;
//        tcp_data[2] = 0x00;
//        tcp_data[3] = ByteUtil.intToByteArray(size)[0];
//        tcp_data[4] = ByteUtil.intToByteArray(size)[1];
//        for (int i = 0; i < size; i++) {
//            String color = data.get(String.valueOf(i)).getColor();
//            tcp_data[i * 3 + 5] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
//            tcp_data[i * 3 + 6] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
//            tcp_data[i * 3 + 7] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));
//
//        }
//        return tcp_data;
//    }


    private List<LampModel> getModels() {
        List<LampModel> mLampModels = new ArrayList<>();
        mLampModels.add(getModel1());
        mLampModels.add(getModel2());
        mLampModels.add(getModel3());
        mLampModels.add(getModel4());
        mLampModels.add(getModel5());
        mLampModels.add(getModel6());
        mLampModels.add(getModel7());
        mLampModels.add(getModel8());
        mLampModels.add(getModel9());
        mLampModels.add(getModel10());
        return mLampModels;
    }

    private LampModel getModel10() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Bright Delightlux");
        lampModel.setId(9);
        lampModel.setCreatTime(System.currentTimeMillis() + "0009");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();

                int w = (int) (Math.random() * 10);
                if (w == 6) {
                    doodle.setColor("#FF00FF");
                } else if (w == 3) {
                    doodle.setColor("#FFFFFF");
                } else {
                    doodle.setColor("#000000");
                }

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel9() {
        String[] colorHexArr = {"#FF0000", "#00FF00", "#FFFFFF", "#000000", "#007FFF", "#0000FF", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setId(8);
        lampModel.setCreatTime(System.currentTimeMillis() + "0008");
        lampModel.setName("Glow");
        List<DoodlePattern> modeArr = new ArrayList<>();

        for (int k = 0; k < 2; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i % row - 0 + row + 1) / 1 % 4]);
                doodle.setFlash(0);
                int x = (int) (Math.random() * 2);
                if (x == 1) {
                    doodle.setColor("#000000");
                }
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel8() {

        String[] colorHexArr = {"#FA0000", "#FAA500", "#FAFF00", "#00FF00", "#007FFF", "#0000FF", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setId(7);
        lampModel.setCreatTime(System.currentTimeMillis() + "0007");
        lampModel.setName("Vertical");
        List<DoodlePattern> modeArr = new ArrayList<>();

        for (int k = 0; k < column; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i / row - k + column + 1) / 3 % 7]);

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;

    }

    private LampModel getModel7() {
        String[] colors = {"#FA0000", "#FAA500", "#00FF00"};
        LampModel lampModel = new LampModel();
        lampModel.setId(6);
        lampModel.setCreatTime(System.currentTimeMillis() + "0006");
        lampModel.setName("Sunset");
        List<DoodlePattern> modeArr = new ArrayList<>();

        int alpha = 14;
        int beta = 7;
        int gama = 0;
        for (int i = 0; i < 21; i++) {
            double a = tan((alpha + i) % 21 * Math.PI / 42);
            double b = tan((beta + i) % 21 * Math.PI / 42);
            double c = tan((gama + i) % 21 * Math.PI / 42);

            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int j = 0; j < size; j++) {
                Doodle doodle = new Doodle();
                doodle.setColor("#000000");

                doodle.setFlash(0);
                light_status.put(String.valueOf(j), doodle);

                double l = j;

                if (a > b && b > c) {
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= a) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < a && (double) (row - 1 - j % row) / (double) (j / row + 1) >= b) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);

                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < b && (double) (row - 1 - j % row) / (double) (j / row + 1) >= c) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < c) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);
                    }
                }

                if (a < c && b > c) {
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= b) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(1);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < b && (double) (row - 1 - j % row) / (double) (j / row + 1) >= c) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= a && (double) (row - 1 - j % row) / (double) (j / row + 1) < c) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(0);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < a) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                }

                if (a > b && b < c) {
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= c) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < c && (double) (row - 1 - j % row) / (double) (j / row + 1) >= a) {
                        doodle.setColor(colors[0]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) >= b && (double) (row - 1 - j % row) / (double) (j / row + 1) < a) {
                        doodle.setColor(colors[1]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                    if ((double) (row - 1 - j % row) / (double) (j / row + 1) < b) {
                        doodle.setColor(colors[2]);
                        doodle.setFlash(2);

                        light_status.put(String.valueOf(j), doodle);
                    }
                }

            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);

        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }


    private LampModel getModel6() {
        String[] colorHexArr = {"#FA0000", "#FAA500", "#000000", "#00FF00", "#007FFF", "#000000", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Updown");
        lampModel.setId(5);
        lampModel.setCreatTime(System.currentTimeMillis() + "0005");
        List<DoodlePattern> modeArr = new ArrayList<>();

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i % row - k + row + 1) / 3 % 7]);

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel5() {
        String[] colorHexArr = {"#FA0000", "#FAA500", "#FAFF00", "#00FF00", "#007FFF", "#0000FF", "#8B00FF"};
        LampModel lampModel = new LampModel();
        lampModel.setName("Horizontal Flag");
        lampModel.setId(4);
        lampModel.setCreatTime(System.currentTimeMillis() + "0004");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < size; i++) {
                Doodle doodle = new Doodle();
                doodle.setColor(colorHexArr[(i % row - k + row + 1) / 3 % 7]);

                doodle.setFlash(0);
                light_status.put(String.valueOf(i), doodle);
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }


    private LampModel getModel4() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Sparkles");
        lampModel.setId(3);
        lampModel.setCreatTime(System.currentTimeMillis() + "0003");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#F99601");
                    } else {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    doodle.setColor("#000000");
                    if (k != 0) {
                        int x = (int) (Math.random() * 2);
                        if (x == 1) {
                            doodle.setColor("#000000");
                        }
                    }
                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel1() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Diagonal");
        lampModel.setId(0);
        lampModel.setCreatTime(System.currentTimeMillis() + "0000");
        lampModel.setCopyModeColor("#ff0000,#ffffff,#F2E93F");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < size / column; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if (j % (size / column) == (i + k) % (size / column) || j % (size / column) == (i + k + 1) % (size / column) || j % (size / column) == (i + k + 2) % (size / column) || j % (size / column) == (i + k + 3) % (size / column)) {
                        doodle.setColor(lampModel.getCopyModeColorList().get(0));
                    } else if (j % (size / column) == (i + k + 8) % (size / column) || j % (size / column) == (i + k + 9) % (size / column) || j % (size / column) == (i + k + 10) % (size / column) || j % (size / column) == (i + k + 11) % (size / column)) {
                        doodle.setColor(lampModel.getCopyModeColorList().get(1));
                    } else {
                        doodle.setColor(lampModel.getCopyModeColorList().get(2));
                    }
                    doodle.setFlash(0);
                    light_status.put(String.valueOf(i * size / column + j), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    private LampModel getModel2() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Fireworks");
        lampModel.setId(1);
        lampModel.setCreatTime(System.currentTimeMillis() + "0001");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#ff0000");
                    } else {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);


                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#00ff00");
                    } else {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);

        }
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#0000ff");
                    } else {
                        doodle.setColor("#000000");
                    }


                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#ffffff");
                    } else {
                        doodle.setColor("#000000");
                    }


                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }


    private LampModel getModel3() {
        LampModel lampModel = new LampModel();
        lampModel.setName("Waves");
        lampModel.setId(2);
        lampModel.setCreatTime(System.currentTimeMillis() + "0002");
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#ff0000");
                    } else {
                        doodle.setColor("#000000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#000000");
                    } else {
                        doodle.setColor("#ff0000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#00ff00");
                    } else {
                        doodle.setColor("#000000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();

                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#000000");
                    } else {
                        doodle.setColor("#00ff00");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }

        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row >= (row - 1 - k)) {
                        doodle.setColor("#0000ff");
                    } else {
                        doodle.setColor("#000000");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        for (int k = 0; k < row; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if ((i * row + j) % row <= k) {
                        doodle.setColor("#000000");
                    } else {
                        doodle.setColor("#0000ff");
                    }
                    int x = (int) (Math.random() * 2);
                    if (x == 1) {
                        doodle.setColor("#000000");
                    }

                    doodle.setFlash(0);
                    int key = (i * size / column + j);
                    light_status.put(String.valueOf(key), doodle);
                }
            }
            doodlePattern.setLight_status(light_status);
            doodlePattern.setSize(size);
            modeArr.add(doodlePattern);
        }
        lampModel.setModeArr(modeArr);
        lampModel.setLight(100);
        lampModel.setSize(size);
        lampModel.setLightRow(size / column);
        lampModel.setColumn(column);
        return lampModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        loginInfo.setLampModels(lampModelsL);
        SaveSharedPreferences.save(getActivity(), CommonValue.LOGIN_INFO, new Gson().toJson(loginInfo));
    }

    @OnClick({R.id.tv_local, R.id.tv_favorites, R.id.tv_cloud, R.id.img_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_local:
                typeLamp = 0;
                setButton(typeLamp);
                break;
            case R.id.tv_favorites:
                typeLamp = 1;
                setButton(typeLamp);
                break;
            case R.id.tv_cloud:
                typeLamp = 2;
                setButton(typeLamp);
                break;
            case R.id.img_upload:
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    adapterF.setNewData(lampModelsF);
                    hideLoading();
                    break;
                case 1: // refresh == true
                    hideLoading();
                    lampModelsL = getModels();
                    loginInfo.setLampModels(lampModelsL);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            showLoading();
                            SaveSharedPreferences.save(getActivity(), CommonValue.LOGIN_INFO, new Gson().toJson(loginInfo));
                            handler.sendEmptyMessage(3);
                        }
                    }).start();
                    if (lampModelsL.get(lampModelsL.size() - 1).getName() != null) {
                        lampModelsL.add(new LampModel());
                    }
                    adapterL.setNewData(lampModelsL);
                    break;
                case 2: // refresh == false
                    hideLoading();
                    if (lampModelsL == null || lampModelsL.size() == 0) {
                        showLoading();
                        lampModelsL = getModels();
                        loginInfo.setLampModels(lampModelsL);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SaveSharedPreferences.save(getActivity(), CommonValue.LOGIN_INFO, new Gson().toJson(loginInfo));
                                handler.sendEmptyMessage(4);
                            }
                        }).start();
                    }
                    if (lampModelsL.get(lampModelsL.size() - 1).getName() != null) {
                        lampModelsL.add(new LampModel());
                    }
                    adapterL.setNewData(lampModelsL);
                    break;
                case 3:
                    hideLoading();
                    break;
                case 4:
                    hideLoading();
                    break;
            }
        }
    };

    private void setButton(int i) {
        recyclerViewL.setVisibility(View.GONE);
        recyclerViewF.setVisibility(View.GONE);
        recyclerViewC.setVisibility(View.GONE);
        tvLocal.setBackground(getResources().getDrawable(R.drawable.btn_50r_white_shape));
        tvFavorites.setBackground(getResources().getDrawable(R.drawable.btn_50r_white_shape));
        tvCloud.setBackground(getResources().getDrawable(R.drawable.btn_50r_white_shape));
        tvLocal.setTextColor(getResources().getColor(R.color.color_33));
        tvFavorites.setTextColor(getResources().getColor(R.color.color_33));
        tvCloud.setTextColor(getResources().getColor(R.color.color_33));
        switch (i) {
            case 0:
                recyclerViewL.setVisibility(View.VISIBLE);
                tvLocal.setBackground(getResources().getDrawable(R.drawable.btn_50r_shape));
                tvLocal.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                if (changeFavorite) {
                    showLoading();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            lampModelsF = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.FAVORITE), new TypeToken<List<LampModel>>() {
                            }.getType());
                            if (lampModelsF == null) {
                                lampModelsF = new ArrayList<>();
                            }
                            changeFavorite = false;
                            handler.sendEmptyMessage(0);
                        }
                    }).start();
                }
                recyclerViewF.setVisibility(View.VISIBLE);
                tvFavorites.setBackground(getResources().getDrawable(R.drawable.btn_50r_shape));
                tvFavorites.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                recyclerViewC.setVisibility(View.VISIBLE);
                tvCloud.setBackground(getResources().getDrawable(R.drawable.btn_50r_shape));
                tvCloud.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                break;

        }

    }
}
