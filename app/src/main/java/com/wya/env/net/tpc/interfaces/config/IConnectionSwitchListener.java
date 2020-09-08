package com.wya.env.net.tpc.interfaces.config;

import com.wya.env.net.tpc.entity.SocketAddress;
import com.wya.env.net.tpc.interfaces.conn.IConnectionManager;

/**
 * Author：Alex
 * Date：2019/6/4
 * Note：
 */
public interface IConnectionSwitchListener {
    void onSwitchConnectionInfo(IConnectionManager manager, SocketAddress oldAddress, SocketAddress newAddress);
}
