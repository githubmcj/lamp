package com.wya.env.net.tpc.connection.reconnect;

import com.wya.env.net.tpc.entity.OriginReadData;
import com.wya.env.net.tpc.entity.SocketAddress;
import com.wya.env.net.tpc.interfaces.conn.IConnectionManager;
import com.wya.env.net.tpc.interfaces.conn.IReconnListener;
import com.wya.env.net.tpc.interfaces.conn.ISocketActionListener;

/**
 * Author：Alex
 * Date：2019/5/31
 * Note：抽象重连管理器
 */
public abstract class AbsReconnection implements ISocketActionListener, IReconnListener {
    /**
     * 连接管理器
     */
    protected IConnectionManager connectionManager;
    /**
     * socket连接管理器是否已销毁
     */
    protected boolean isDetach;


    @Override
    public synchronized void attach(IConnectionManager iConnectionManager) {
        if (!isDetach) {
            detach();
        }
        isDetach = false;
        connectionManager = iConnectionManager;
        connectionManager.subscribeSocketAction(this); // 监听socket行为
    }

    @Override
    public synchronized void detach() {
        isDetach = true;
        if (connectionManager != null) {
            connectionManager.unSubscribeSocketAction(this);
        }
    }

    @Override
    public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
        // donothing
    }
}
