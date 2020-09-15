package com.wya.env.module.login.start;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wya.env.R;
import com.wya.env.base.BaseActivity;
import com.wya.env.bean.login.Lamps;
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
        setTitle("Please enter your home WIFI");
        initLampInfo();
        initEasySocket(lamps.getChose_ip());

        etAccount.setText("WYA-T-5G");
        etPassword.setText("wya88888");
        etRename.setText("test");
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
                .setCallbackIdKeyFactory(new CallbackIdKeyFactoryImpl())
                .setReaderProtocol(null)
                .build();

        options.setMessageProtocol(null);
        options.setMaxResponseDataMb(1000000);
        options.setHeartbeatFreq(4000);
        // 初始化EasySocket
        EasySocket.getInstance()
                .options(options) // 项目配置
                .createConnection();// 创建一个socket连接

        // 监听socket行为
        EasySocket.getInstance().subscribeSocketAction(socketActionListener);
        toStartHeart();
    }

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
//            LogUtil.d("连接成功, 并发送数据：" + ByteUtil.byte2hex(bodyData));
//            EasySocket.getInstance().upBytes(bodyData);
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
                // 跳转到主界面
                startActivity(new Intent(LinkActivity.this, Start4Activity.class));
                finish();
            } else if (originReadData.getBodyData()[originReadData.getBodyData().length - 1] == 1) {
                LogUtil.e("失败");
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            EasySocket.getInstance().destroyConnection();
        } catch (Exception e) {

        }
    }
}
