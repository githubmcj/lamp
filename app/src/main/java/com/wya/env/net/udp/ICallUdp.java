package com.wya.env.net.udp;

public interface ICallUdp {
    void start();

    void success(byte[] data, String ip);

    void failure(String message);

    void close();
}
