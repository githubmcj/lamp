package com.wya.env.module.login.start;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.easysocket.EasySocket;
import com.easysocket.config.EasySocketOptions;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.google.gson.Gson;
import com.wya.env.App;
import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.bean.login.Lamps;
import com.wya.env.bean.tcp.DefaultMessageProtocol;
import com.wya.env.common.CommonValue;
import com.wya.env.util.ByteUtil;
import com.wya.env.util.SaveSharedPreferences;
import com.wya.uikit.button.WYAButton;
import com.wya.utils.utils.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;

import static com.wya.env.common.CommonValue.TCP_PORT;

public class LinkActivity extends BaseActivity {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_rename)
    EditText etRename;
    @BindView(R.id.but_submit)
    WYAButton butSubmit;
    private Lamps lamps;

    private boolean isConnected;

    @Override
    protected void initView() {
        setTitle("Please type your local WiFi information");
        initLampInfo();
        initEasySocket(lamps.getChose_ip());
        setEnableButton(isConnected);


    }

    private void setEnableButton(boolean isConnected) {
        if (butSubmit != null) {
            butSubmit.setEnabled(isConnected);
            if (isConnected) {
                butSubmit.setBackGroundColor(getResources().getColor(R.color.app_blue));
                butSubmit.setBackGroundColorPress(getResources().getColor(R.color.app_blue));
            } else {
                butSubmit.setBackGroundColor(getResources().getColor(R.color.c999999));
                butSubmit.setBackGroundColorPress(getResources().getColor(R.color.c999999));
            }
        }
    }

    private void initLampInfo() {
        lamps = new Gson().fromJson(SaveSharedPreferences.getString(this, CommonValue.LAMPS), Lamps.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_link;
    }


    @OnClick(R.id.but_submit)
    public void onViewClicked() {

        if (TextUtils.isEmpty(etAccount.getText().toString())) {
            Toast.makeText(this, etAccount.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] wifi = etAccount.getText().toString().trim().getBytes();
        byte[] data2 = new byte[31];
        for (int i = 0; i < 31; i++) {
            if (wifi.length > i) {
                data2[i] = wifi[i];
            } else {
                data2[i] = 0x00;
            }
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            Toast.makeText(this, etPassword.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] password = etPassword.getText().toString().trim().getBytes();
        byte[] data3 = new byte[31];
        for (int i = 0; i < 31; i++) {
            if (password.length > i) {
                data3[i] = password[i];
            } else {
                data3[i] = 0x00;
            }
        }

        if (TextUtils.isEmpty(etRename.getText().toString())) {
            Toast.makeText(this, etRename.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] name = etRename.getText().toString().trim().getBytes();
        byte[] data4 = new byte[31];
        for (int i = 0; i < 31; i++) {
            if (name.length > i) {
                data4[i] = name[i];
            } else {
                data4[i] = 0x00;
            }
        }

        byte[] initData = getIntData(data2, data3, data4);
        LogUtil.d("initData-----------=" + ByteUtil.byte2hex(initData));

        EasySocket.getInstance().upBytes(initData);
    }

    private byte[] getIntData(byte[] data2, byte[] data3, byte[] data4) {
        byte[] bodyData = new byte[94];
        bodyData[0] = 0x0a;
        for (int i = 0; i < 31; i++) {
            bodyData[i + 1] = data2[i];
        }
        for (int i = 0; i < 31; i++) {
            bodyData[i + 32] = data3[i];
        }
        for (int i = 0; i < 31; i++) {
            bodyData[i + 63] = data4[i];
        }
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] initData = ByteUtil.byteMerger(send_head_data, bodyData);
        return initData;
    }


    /**
     * 初始化EasySocket
     */
    private void initEasySocket(String ip) {
        // socket配置
        EasySocketOptions options = new EasySocketOptions.Builder()
                .setSocketAddress(new SocketAddress(ip, TCP_PORT)) // 主机地址
                .setReaderProtocol(new DefaultMessageProtocol())
                .build();

        options.setMessageProtocol(new DefaultMessageProtocol());
        options.setMaxResponseDataMb(1000000);
        options.setHeartbeatFreq(4000);
        // 初始化EasySocket
        EasySocket.getInstance()
                .options(options) // 项目配置
                .createConnection();// 创建一个socket连接

        // 监听socket行为
        EasySocket.getInstance().subscribeSocketAction(socketActionListener);
    }


    private int start_count;

    private void toStartHeart() {
        try {
            start_count++;
            LogUtil.e("发送的心跳数据：" + ByteUtil.byte2hex(getBreathData()));
            EasySocket.getInstance().startHeartBeat(getBreathData(), originReadData -> {
                if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == -122) {
                    LogUtil.d("心跳监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
                    return true;
                } else {
                    return false;
                }
            });
        } catch (Exception e) {
            LogUtil.e("start_count:" + start_count);
            if (start_count < 5) {
                toStartHeart();
            }
        }
    }

    private byte[] getBreathData() {
        byte[] bodyData = new byte[1];
        bodyData[0] = 0x06;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] breathData = ByteUtil.byteMerger(send_head_data, bodyData);
        return breathData;
    }

    /**
     * socket行为监听
     */
    private ISocketActionListener socketActionListener = new SocketActionListener() {
        /**
         * socket连接成功
         * @param socketAddress
         */
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            super.onSocketConnSuccess(socketAddress);
            LogUtil.d("连接成功");
            getWifiInfo();
            start_count = 0;
            toStartHeart();
            isConnected = true;
            setEnableButton(isConnected);
            App.getInstance().setTcpConnected(true);
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
            setEnableButton(isConnected);
            App.getInstance().setTcpConnected(false);
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
            setEnableButton(isConnected);
            App.getInstance().setTcpConnected(false);
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
            dealSocketResponseData(originReadData);
        }
    };

    /**
     * 获取的数据处理
     *
     * @param originReadData TCP 接收的原始数据
     */
    private void dealSocketResponseData(OriginReadData originReadData) {
        LogUtil.d("socket监听器收到数据=" + ByteUtil.byte2hex(originReadData.getBodyData()));
        switch (originReadData.getBodyData()[0]) {
            case (byte) 0x8a:
                LogUtil.e("配置路由器");
                if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 0) {
                    LogUtil.e("成功");
                    // 跳转到主界面
                    startActivity(new Intent(LinkActivity.this, Start4Activity.class).putExtra("name", etRename.getText().toString()));
                    finish();
                } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                    LogUtil.e("失败");
                }
                break;
            case (byte) 0x86:
                LogUtil.e("心跳数据");
                break;
            case (byte) 0x89:
                LogUtil.e("获取控制器wifi数据");
                String wifi = new String(getWifiData(originReadData.getBodyData()));
                String password = new String(getPasswordData(originReadData.getBodyData()));
                String name = new String(getNameData(originReadData.getBodyData()));
                if(!TextUtils.isEmpty(wifi)){
                    etAccount.setText(wifi);
                }
                if(!TextUtils.isEmpty(password)){
                    etPassword.setText(password);
                }
                if(!TextUtils.isEmpty(name)){
                    etRename.setText(name);
                }
                break;
            default:
                break;
        }
    }

    private byte[] getWifiData(byte[] data) {
        byte[] name = new byte[31];
        for (int i = 0; i < 31; i++) {
            name[i] = data[i + 1];
        }
        return name;
    }

    private byte[] getPasswordData(byte[] data) {
        byte[] name = new byte[31];
        for (int i = 0; i < 31; i++) {
            name[i] = data[i + 32];
        }
        return name;
    }
    private byte[] getNameData(byte[] data) {
        byte[] name = new byte[31];
        for (int i = 0; i < 31; i++) {
            name[i] = data[i + 63];
        }
        return name;
    }



    private void getWifiInfo() {
        byte[] bodyData = new byte[1];
        bodyData[0] = 0x09;
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        EasySocket.getInstance().upBytes(openFileData);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            EasySocket.getInstance().destroyConnection();
        } catch (Exception e) {

        }
    }
}
