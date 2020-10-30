package com.wya.env.module.home.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.Doodle;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.bean.doodle.SaveModel;
import com.wya.env.bean.event.EventtDeviceName;
import com.wya.env.bean.home.AddModel;
import com.wya.env.bean.home.MusicSuccess;
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.LampView;
import com.wya.utils.utils.LogUtil;

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

import static java.lang.Math.tan;

/**
 * @date: 2018/7/3 13:55
 * @author: Chunjiang Mao
 * @classname: Fragment1
 * @describe: Example Fragment
 */

public class HomeFragment extends BaseMvpFragment<HomeFragmentPresenter> implements HomeFragmentView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.lamp_view)
    LampView lampView;
    @BindView(R.id.name)
    TextView name;
    private LampModelAdapter adapter;

    private LoginInfo loginInfo;
    private List<DoodlePattern> doodlePatterns = new ArrayList<>();

    private List<LampModel> netLampModels = new ArrayList<>();
    private List<DoodlePattern> choseModel;


    private HomeFragmentPresenter homeFragmentPresenter = new HomeFragmentPresenter();

    private String[] snow_colors = {"#ffffff", "#B04F9C", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000"};
    private String[] fifth_colors = {"#FA0000", "#FAA500", "#00FF00"};

    private Lamps lamps;
    private List<LampModel> lampModels = new ArrayList<>();

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
        EventBus.getDefault().register(this);
        if (isVisible()) {
            initSendData();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        lampView.toStopSendUdpModeData(true, false);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicSuccess event) {
        if (adapter != null) {
            lampModels.get(event.getPosition()).setMusic(1 - lampModels.get(event.getPosition()).isMusic());
            adapter.setNewData(lampModels);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventtDeviceName eventtDeviceName) {
        name.setText(eventtDeviceName.getDeviceName() == null ? "device name" : eventtDeviceName.getDeviceName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LampSetting lampSetting) {
        getLocalData(true);
        getNetData();
        initRecyclerView();
    }


    private void initData() {
        getLocalData(false);
        getNetData();
        initRecyclerView();
    }

    private void initSendData() {
        for (int i = 0; i < lampModels.size(); i++) {
            if (lampModels.get(i).isChose() == 1) {
                lampView.setMirror(lampModels.get(i).getMirror());
                lampView.setModel(lampModels.get(i).getModeArr(), lampModels.get(i).getLight(), true);
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
        loginInfo = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LOGIN_INFO), LoginInfo.class);
        lampModels = loginInfo.getLampModels();
        lamps = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LAMPS), Lamps.class);
        if (lamps != null) {
            column = lamps.getColumn();
            row = lamps.getRow();
            size = lamps.getSize();
        }
        if (refresh) {
            lampModels = getModels();
            loginInfo.setLampModels(lampModels);
            SaveSharedPreferences.save(getActivity(), CommonValue.LOGIN_INFO, new Gson().toJson(loginInfo));
            hideLoading();
        } else {
            if (lampModels == null || lampModels.size() == 0) {
                showLoading();
                lampModels = getModels();
                loginInfo.setLampModels(lampModels);
                SaveSharedPreferences.save(getActivity(), CommonValue.LOGIN_INFO, new Gson().toJson(loginInfo));
                hideLoading();
            }
        }
        if (lampModels.get(lampModels.size() - 1).getName() != null) {
            lampModels.add(new LampModel());
        }
    }


    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new LampModelAdapter(getActivity(), R.layout.lamp_pattern_item, lampModels);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        // RecyclerView条目点击事件
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == lampModels.size() - 1) {
                EventBus.getDefault().post(new AddModel());
            } else {
                if (lampModels.get(position).isChose() != 1) {
                    choseModel = lampModels.get(position).getModeArr();
                    for (int i = 0; i < lampModels.size(); i++) {
                        lampModels.get(i).setChose(0);
                    }
                    lampModels.get(position).setChose(1);
                    adapter.notifyDataSetChanged();

                    lampView.setMirror(lampModels.get(position).getMirror());
                    lampView.setModel(lampModels.get(position).getModeArr(), lampModels.get(position).getLight(), true);
                } else {
                    LogUtil.e("该模板已经选中");
                }

            }
//            setTcpData(lampModels.get(position).getModeArr());
        });
    }

//    private void setTcpData(List<DoodlePattern> modeArr) {
//        TaskCenter.sharedCenter().setDisconnectedCallback(new TaskCenter.OnServerDisconnectedCallbackBlock() {
//            @Override
//            public void callback(IOException e) {
//                showShort("连接失败：" + e.getMessage());
//            }
//        });
//        TaskCenter.sharedCenter().setConnectedCallback(new TaskCenter.OnServerConnectedCallbackBlock() {
//            @Override
//            public void callback() {
//                LogUtil.e("连接成功， 打开文件");
//                TaskCenter.sharedCenter().send(getOpenFileData());
////                LogUtil.e("连接成功");
//
//            }
//        });
//        TaskCenter.sharedCenter().setReceivedCallback(new TaskCenter.OnReceiveCallbackBlock() {
//            @Override
//            public void callback(byte[] receiceData) {
//                showShort("返回数据：" + ByteUtil.byte2hex(receiceData));
//            }
//        });
//        //连接
//        TaskCenter.sharedCenter().connect("192.168.4.1", 6600);
////        //发送
////
////        // 断开连接
////        TaskCenter.sharedCenter().disconnect();
//    }

    @Override
    protected int getLayoutResource() {
        return R.layout.home_fragment;
    }

    @Override
    protected void initView() {
        homeFragmentPresenter.mView = this;
        lampView.setFocusable(false);
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
        netLampModels.clear();
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
            netLampModels.add(lampModel);
        }
        if (lampModels.get(lampModels.size() - 1).getName() == null) {
            lampModels.remove(lampModels.size() - 1);
        }
        lampModels.addAll(netLampModels);
        if (lampModels.get(lampModels.size() - 1).getName() != null) {
            lampModels.add(new LampModel());
        }
        if (adapter != null) {
            adapter.setNewData(lampModels);
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
        List<DoodlePattern> modeArr = new ArrayList<>();
        for (int k = 0; k < size / column; k++) {
            DoodlePattern doodlePattern = new DoodlePattern();
            HashMap<String, Doodle> light_status = new HashMap<>();
            for (int i = 0; i < column; i++) {
                for (int j = 0; j < size / column; j++) {
                    Doodle doodle = new Doodle();
                    if (j % (size / column) == (i + k) % (size / column) || j % (size / column) == (i + k + 1) % (size / column) || j % (size / column) == (i + k + 2) % (size / column) || j % (size / column) == (i + k + 3) % (size / column)) {
                        doodle.setColor("#ff0000");
                    } else if (j % (size / column) == (i + k + 8) % (size / column) || j % (size / column) == (i + k + 9) % (size / column) || j % (size / column) == (i + k + 10) % (size / column) || j % (size / column) == (i + k + 11) % (size / column)) {
                        doodle.setColor("#ffffff");
                    } else {
                        doodle.setColor("#F2E93F");
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

}
