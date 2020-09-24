package com.wya.env.module.home.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wya.env.R;
import com.wya.env.base.BaseMvpFragment;
import com.wya.env.bean.doodle.Doodle;
import com.wya.env.bean.doodle.DoodlePattern;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.doodle.SaveModel;
import com.wya.env.bean.event.EventtDeviceName;
import com.wya.env.bean.home.AddModel;
import com.wya.env.bean.home.MusicSuccess;
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.net.tpc.CallbackIdKeyFactoryImpl;
import com.wya.env.net.tpc.EasySocket;
import com.wya.env.net.tpc.config.EasySocketOptions;
import com.wya.env.net.tpc.connection.heartbeat.HeartManager;
import com.wya.env.net.tpc.entity.OriginReadData;
import com.wya.env.net.tpc.entity.SocketAddress;
import com.wya.env.net.tpc.interfaces.conn.ISocketActionListener;
import com.wya.env.net.tpc.interfaces.conn.SocketActionListener;
import com.wya.env.util.ByteUtil;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.LampView;
import com.wya.uikit.dialog.WYACustomDialog;
import com.wya.utils.utils.LogUtil;
import com.wya.utils.utils.ScreenUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

import static com.wya.env.common.CommonValue.TCP_PORT;

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
    private List<LampModel> lampModels = new ArrayList<>();
    private List<LampModel> netLampModels = new ArrayList<>();
    private List<DoodlePattern> choseModel;
    private int size;


    private HomeFragmentPresenter homeFragmentPresenter = new HomeFragmentPresenter();

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
        initSendData();
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


    private void initData() {
        getLocalData();
        getNetData();
        initRecyclerView();
    }

    private void initSendData() {
        for (int i = 0; i < lampModels.size(); i++) {
            if(lampModels.get(i).isChose() == 1){
                lampView.setMirror(lampModels.get(i).getMirror());
                lampView.setModel(lampModels.get(i).getModeArr(), lampModels.get(i).getLight(), true);
            }
        }
    }

    private void getNetData() {
        homeFragmentPresenter.getSaveModels();
    }

    private void getLocalData() {
        loginInfo = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LOGIN_INFO), LoginInfo.class);
        lampModels = loginInfo.getLampModels();
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
            if(toRefresh){
                SaveSharedPreferences.save(getActivity(), CommonValue.TO_REFRESH, false);
                getLocalData();
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
        lampModels.addAll(netLampModels);
        if (lampModels.get(lampModels.size() - 1) != null) {
            lampModels.add(new LampModel());
        }
        if (adapter != null) {
            adapter.setNewData(lampModels);
        }
    }


    /**
     * 发送tcp动画数据
     */
    private Lamps lamps;
    private WYACustomDialog dialog;

    public void toSendTcpData() {
        dialog = new WYACustomDialog.Builder(getActivity())
                .title("提示")
                .message("是否继续播放窗帘灯？")
                .width(ScreenUtil.getScreenWidth(getActivity()) * 3 / 4)
                .build();
        dialog.setNoClickListener(new WYACustomDialog.NoClickListener() {
            @Override
            public void onNoClick() {
                getActivity().finish();
                dialog.dismiss();
            }
        });
        dialog.setYesClickListener(new WYACustomDialog.YesClickListener() {
            @Override
            public void onYesClick() {
                lampView.toStopSendUdpModeData(true, false);
                lamps = new Gson().fromJson(SaveSharedPreferences.getString(getActivity(), CommonValue.LAMPS), Lamps.class);
                size = lamps.getSize();
                initEasySocket(lamps.getChose_ip());
            }
        });
        dialog.show();
    }


    private boolean isConnected;

    /**
     * 初始化EasySocket
     */
    private void initEasySocket(String ip) {
        if (isConnected) {
            EasySocket.getInstance().disconnect(false);
        }
        // socket配置
        EasySocketOptions options = new EasySocketOptions.Builder()
                .setSocketAddress(new SocketAddress(ip, TCP_PORT)) // 主机地址
                .setCallbackIdKeyFactory(new CallbackIdKeyFactoryImpl())
                .setReaderProtocol(null)
                .build();

        options.setMessageProtocol(null);
        options.setHeartbeatFreq(2000);
        // 初始化EasySocket
        EasySocket.getInstance()
                .options(options) // 项目配置
                .createConnection();// 创建一个socket连接

        // 监听socket行为
        EasySocket.getInstance().subscribeSocketAction(socketActionListener);
    }


    private ISocketActionListener socketActionListener = new SocketActionListener() {
        /**
         * socket连接成功
         * @param socketAddress
         */
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            super.onSocketConnSuccess(socketAddress);
            LogUtil.d("连接成功, 并发送数据：");
            if (step == 0) {
                toStartHeart();
//                EasySocket.getInstance().upBytes(getOpenFileData(true));
            } else if (step == 1) {

            }

            isConnected = true;
        }

        /**
         * socket连接失败
         * @param socketAddress
         * @param isNeedReconnect 是否需要重连
         */
        @Override
        public void onSocketConnFail(SocketAddress socketAddress, Boolean isNeedReconnect) {
            super.onSocketConnFail(socketAddress, isNeedReconnect);
            LogUtil.d("socket连接被断开");
            isConnected = false;
        }

        /**
         * socket断开连接
         * @param socketAddress
         * @param isNeedReconnect 是否需要重连
         */
        @Override
        public void onSocketDisconnect(SocketAddress socketAddress, Boolean isNeedReconnect) {
            super.onSocketDisconnect(socketAddress, isNeedReconnect);
            LogUtil.d("socket断开连接，是否需要重连：" + isNeedReconnect);
            LogUtil.d("socket连接被断开");
            isConnected = false;
        }

        /**
         * socket接收的数据
         * @param socketAddress
         * @param originReadData
         */
        @Override
        public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
            super.onSocketResponse(socketAddress, originReadData);
            LogUtil.d("socket监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
            if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
                LogUtil.e("成功");
                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessage(msg);
            } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                LogUtil.e("失败");
            } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0x86) {
                LogUtil.e("心跳数据");
            } else {
                LogUtil.e("其他数据");
            }
        }
    };

    private void toStartHeart() {
        EasySocket.getInstance().startHeartBeat(getBreathData(), new HeartManager.HeartbeatListener() {
            @Override
            public boolean isServerHeartbeat(OriginReadData originReadData) {
                LogUtil.d("心跳监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
                return false;
            }
        });
    }

    private byte[] getBreathData() {
        byte[] bodyData = new byte[1];
        bodyData[0] = 0x06;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] breathData = ByteUtil.byteMerger(send_head_data, bodyData);
        return breathData;
    }


    /**
     * 打开文件
     */

    int step = 0;
    int modelIndex = 0;
    int fileIndex = 3;

    private byte[] getOpenFileData(boolean isOpen) {
        byte[] bodyData = new byte[4];
        bodyData[0] = 0x01;
        bodyData[1] = (byte) (0xff & step);
        bodyData[2] = (byte) (0xff & fileIndex);
        if (isOpen) {
            bodyData[3] = 0x01;
        } else {
            bodyData[3] = 0x02;
        }
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        LogUtil.e("openFileData:" + ByteUtil.byte2hex(openFileData));
        return openFileData;
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 1:
                    switch (step) {
                        case 0:
                            step = 1;// 送模板数据
                            modelIndex = 0;
                            byte[] body_data = getTcpByteData(choseModel.get(modelIndex).getLight_status());
                            byte[] send_head_data = ByteUtil.getHeadByteData(body_data);
                            byte[] send_data = ByteUtil.byteMerger(send_head_data, body_data);
                            EasySocket.getInstance().upBytes(send_data);
                            break;
                        case 1:
                            modelIndex++;
                            if (modelIndex == choseModel.size()) {
                                step = 2;
                                EasySocket.getInstance().upBytes(getOpenFileData(false));
                            } else {
                                byte[] body_data2 = getTcpByteData(choseModel.get(modelIndex).getLight_status());
                                byte[] send_head_data2 = ByteUtil.getHeadByteData(body_data2);
                                byte[] send_data2 = ByteUtil.byteMerger(send_head_data2, body_data2);
                                EasySocket.getInstance().upBytes(send_data2);
                            }
                            break;
                        case 2:
                            dialog.dismiss();
                            LogUtil.e("传输完成");
                            break;

                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public byte[] getTcpByteData(HashMap<String, Doodle> data) {
        byte[] tcp_data = new byte[1 + 2 + 2 + 3 * size];
        tcp_data[0] = 0x01;
        tcp_data[1] = 0x00;
        tcp_data[2] = 0x00;
        tcp_data[3] = ByteUtil.intToByteArray(size)[0];
        tcp_data[4] = ByteUtil.intToByteArray(size)[1];
        for (int i = 0; i < size; i++) {
            String color = data.get(String.valueOf(i)).getColor();
            tcp_data[i * 3 + 5] = (byte) (0xff & Integer.parseInt(color.substring(1, 3), 16));
            tcp_data[i * 3 + 6] = (byte) (0xff & Integer.parseInt(color.substring(3, 5), 16));
            tcp_data[i * 3 + 7] = (byte) (0xff & Integer.parseInt(color.substring(5, 7), 16));

        }
        return tcp_data;
    }


}
