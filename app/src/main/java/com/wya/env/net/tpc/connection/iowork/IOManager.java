package com.wya.env.net.tpc.connection.iowork;

import com.wya.env.net.tpc.config.EasySocketOptions;
import com.wya.env.net.tpc.connection.iowork.EasyReader;
import com.wya.env.net.tpc.connection.iowork.EasyWriter;
import com.wya.env.net.tpc.interfaces.config.IOptions;
import com.wya.env.net.tpc.interfaces.conn.IConnectionManager;
import com.wya.env.net.tpc.interfaces.conn.ISocketActionDispatch;
import com.wya.env.net.tpc.interfaces.io.IIOManager;
import com.wya.env.net.tpc.interfaces.io.IReader;
import com.wya.env.net.tpc.interfaces.io.IWriter;

/**
 * Author：Alex
 * Date：2019/5/28
 * Note：
 */
public class IOManager implements IIOManager, IOptions {
    /**
     * socket行为回调
     */
    private ISocketActionDispatch actionDispatch;
    /**
     * 连接管理
     */
    private IConnectionManager connectionManager;
    /**
     * io写
     */
    private IWriter writer;
    /**
     * io读
     */
    private IReader reader;

    public IOManager(IConnectionManager connectionManager,
                     ISocketActionDispatch connActionDispatch) {
        this.connectionManager = connectionManager;
        this.actionDispatch = connActionDispatch;
        initIO();
    }

    //  初始化io
    private void initIO() {
        //makesureHeaderProtocolNotEmpty();
        reader = new EasyReader(connectionManager, actionDispatch); //  读
        writer = new EasyWriter(connectionManager, actionDispatch); //  写
    }

    @Override
    public void sendBytes(byte[] bytes) {
        if (writer != null) {
            writer.offer(bytes);
        }
    }

    @Override
    public void startIO() {
        if (writer != null) {
            writer.openWriter();
        }
        if (reader != null) {
            reader.openReader();
        }
    }

    @Override
    public void closeIO() {
        if (writer != null) {
            writer.closeWriter();
        }
        if (reader != null) {
            reader.closeReader();
        }
    }

    @Override
    public Object setOptions(EasySocketOptions socketOptions) {
        //makesureHeaderProtocolNotEmpty();
        if (writer != null) {
            writer.setOption(socketOptions);
        }
        if (reader != null) {
            reader.setOption(socketOptions);
        }
        return this;
    }

    @Override
    public EasySocketOptions getOptions() {
        return connectionManager.getOptions();
    }

    /**
     * 确保包结构协议不为空
     */
//    private void makesureHeaderProtocolNotEmpty() {
//        IMessageProtocol protocol = connectionManager.getOptions().getMessageProtocol();
//        if (protocol == null) {
//            throw new NoNullException("The reader protocol can not be Null.");
//        }
//
//        if (protocol.getHeaderLength() == 0) {
//            throw new NoNullException("The header length can not be zero.");
//        }
//    }
}
