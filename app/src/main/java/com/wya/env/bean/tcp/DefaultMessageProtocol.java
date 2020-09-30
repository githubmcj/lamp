package com.wya.env.bean.tcp;

import com.easysocket.interfaces.io.IMessageProtocol;
import com.easysocket.utils.LogUtil;
import com.wya.env.util.ByteUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DefaultMessageProtocol implements IMessageProtocol {
    @Override
    public int getHeaderLength() {
        return 8; // 包头的长度，用来保存body的长度值
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        if (header == null || header.length < getHeaderLength()) {
            return 0;
        }
        return  ByteUtil.decodeHEX(ByteUtil.byte2hex(header).substring(15, 17) + ByteUtil.byte2hex(header).substring(12, 14)); // body的长度
    }

    @Override
    public byte[] pack(byte[] body) {
        // 消息头的长度，指多少个byte
        ByteBuffer bb = ByteBuffer.allocate(body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(body); // body
        return bb.array();
    }
}