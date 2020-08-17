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
import com.wya.env.bean.doodle.SaveModel;
import com.wya.env.bean.login.LoginInfo;
import com.wya.env.common.CommonValue;
import com.wya.env.net.tpc.TaskCenter;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.env.view.LampView;
import com.wya.utils.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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

    private HomeFragmentPresenter homeFragmentPresenter = new HomeFragmentPresenter();

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
        homeFragmentPresenter.mView = this;
        if (isVisible) {
            initData();//初始化数据
        }
    }

    private void initData() {
        getLocalData();
        getNetData();
        initRecyclerView();
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
        recyclerView.setAdapter(adapter);
        //RecyclerView条目点击事件
        adapter.setOnItemClickListener((adapter, view, position) -> {
            name.setText(lampModels.get(position).getName());
            lampView.setModel(lampModels.get(position).getModeArr());
            for (int i = 0; i < lampModels.size(); i++) {
                lampModels.get(i).setChose(0);
            }
            lampModels.get(position).setChose(1);
            adapter.notifyDataSetChanged();
            setTcpData(lampModels.get(position).getModeArr());
        });
    }

    private void setTcpData(List<DoodlePattern> modeArr) {
        TaskCenter.sharedCenter().setDisconnectedCallback(new TaskCenter.OnServerDisconnectedCallbackBlock() {
            @Override
            public void callback(IOException e) {
                showShort("连接失败：" + e.getMessage());
            }
        });
        TaskCenter.sharedCenter().setConnectedCallback(new TaskCenter.OnServerConnectedCallbackBlock() {
            @Override
            public void callback() {
                LogUtil.e("连接成功， 打开文件");
                TaskCenter.sharedCenter().send(getOpenFileData());
//                LogUtil.e("连接成功");

            }
        });
        TaskCenter.sharedCenter().setReceivedCallback(new TaskCenter.OnReceiveCallbackBlock() {
            @Override
            public void callback(byte[] receiceData) {
                showShort("返回数据：" + byte2hex(receiceData));
            }
        });
        //连接
        TaskCenter.sharedCenter().connect("192.168.4.1", 6600);
//        //发送
//
//        // 断开连接
//        TaskCenter.sharedCenter().disconnect();
    }

    int step = 0;
    int fileIndex = 3;

    private byte[] getOpenFileData() {
        byte[] bodyData = new byte[4];
        bodyData[0] = 0x01;
        bodyData[1] = (byte) (0xff & step);
        bodyData[2] = (byte) (0xff & fileIndex);
        bodyData[3] = 0x01;
        byte[] send_head_data = getHeadByteData(bodyData);
        byte[] openFileData = byteMerger(send_head_data, bodyData);
        LogUtil.e("openFileData:" + byte2hex(openFileData));
        return openFileData;
    }

    private String byte2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            //将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp + " ");
        }
        return sb.toString();
    }

    private byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    private byte[] getHeadByteData(byte[] bodyData) {
        byte[] head_data = new byte[8];
        head_data[0] = 0x53;
        head_data[1] = 0x48;
        head_data[2] = 0x59;
        head_data[3] = 0x55;
        if (bodyData.length == 4) {
            head_data[4] = 0x04;
            head_data[5] = 0x00;
        }
        head_data[6] = (byte) (0xff & Integer.parseInt(CheckDigit(bodyData), 16));
        head_data[7] = (byte) (0xff & (Integer.parseInt("ff", 16) - Integer.parseInt(CheckDigit(bodyData), 16)));
        return head_data;
    }

    /**
     * @param udpByteData
     * @return 校验位计算，取低8位为校验位
     */
    private String CheckDigit(byte[] udpByteData) {
        int sum = 0;
        for (int i = 0; i < udpByteData.length; i++) {
            sum += udpByteData[i];
        }
        String CheckSumBinary = Integer.toBinaryString(sum);
        String CheckSum = "";
        String CheckSum_hex = "";
        if (CheckSumBinary.length() > 8) {
            CheckSum =
                    CheckSumBinary.substring(CheckSumBinary.length() - 8, CheckSumBinary.length());
            sum = Integer.parseInt(CheckSum, 2);
            CheckSum_hex = Integer.toHexString(sum);
        } else {
            sum = Integer.parseInt(CheckSumBinary, 2);
            CheckSum_hex = Integer.toHexString(sum);
        }

        return CheckSum_hex;
    }




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
        if (!hidden && toRefresh) {
            SaveSharedPreferences.save(getActivity(), CommonValue.TO_REFRESH, false);
            getLocalData();
            getNetData();
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
                        doodle.setLight(255);
                        doodlePattern.getLight_status().put(String.valueOf(k), doodle);
                    }
                }
            }
            netLampModels.add(lampModel);
        }
        lampModels.addAll(netLampModels);
        if (adapter != null) {
            adapter.setNewData(lampModels);
        }
    }
}
