package com.wya.env.net.udp;

public interface ICallUdp {
    void start();

    void success(byte[] data, String ip, int type);

    void failure(String message);

    void close();
}
