package com.wya.env.net.tpc.config;

import com.wya.env.net.tpc.config.EasySocketOptions;
import com.wya.env.net.tpc.entity.SocketAddress;

import java.net.Socket;

/**
 * Author：Alex
 * Date：2019/5/31
 * Note：socket工厂
 */
public abstract class SocketFactory {
    public abstract Socket createSocket(SocketAddress info, EasySocketOptions options) throws Exception;
}
