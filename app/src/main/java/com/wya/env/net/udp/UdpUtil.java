package com.wya.env.net.udp;

import com.wya.env.common.CommonValue;
import com.wya.utils.utils.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class UdpUtil {
    private static DatagramSocket client;

    /**
     * 连接的方法
     *
     * @param data     发送指令
     * @param iCallUdp 返回的接口数据
     */
    public static void send(byte[] data, String loc_ip, ICallUdp iCallUdp) {
        iCallUdp.start();
        try {

            // 1,创建服务端+端口

            if (client != null && !client.isClosed()) {
                client.close();
                client = null;
            }
            client = new DatagramSocket(null);
            client.setReuseAddress(true);
            //myPort 设置自己的端口号  用于相互通信
            client.bind(new InetSocketAddress(CommonValue.UDP_PORT));

            // 3,打包（发送的地点及端口）
            //IP 连接的目标IP地址
            //port 连接目标的 端口号
            DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress("255.255.255.255", CommonValue.UDP_PORT));
//            DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress("192.168.137.115", CommonValue.UDP_PORT));

            // 4,发送资源
            client.send(packet);
            //发送成功
//            iCallUdp.confirm();
            // 准备接收容器


            while (true) {
                byte[] container = new byte[1024];
                // 封装成包
                DatagramPacket packet2 = new DatagramPacket(container, container.length);
                client.setSoTimeout(5000);
                // 接收数据,使用 DatagramSocket的实例的 receive( DatagramPacket ) 方法进行接收
                client.receive(packet2);
                InetAddress ip = packet2.getAddress();
                LogUtil.e("------------------udpReceiver");
                String ipStr = null;
                if (ip != null) {
                    ipStr = ip.toString().replace("/", "");
                    LogUtil.d(ipStr + "----ipStr------------ipStr-----------");
                    LogUtil.d(loc_ip + "---loc_ip-------------ipStr-----------");
                    if (((!ipStr.equals("")) && ipStr.equals(loc_ip)) || loc_ip.equals("0.0.0.0")) {
                        continue;
                    }
                }
                // 分析数据、打印数据
                byte[] data2 = packet2.getData();
                System.out.println("回调传接收的数据-----------" + bytesToHex(data2));
                //回调传接收的数据
                iCallUdp.success(data2, ipStr);
                // 5,关闭资源
                client.close();
                client = null;
                iCallUdp.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "==" + e.toString());
            iCallUdp.failure(e.getMessage());
            if (client != null && !client.isClosed()) {
                client.close();
                client = null;
            }
        }


    }

    /**
     * 将接收到byte数组转成String字符串
     *
     * @param bytes 接收的byte数组
     * @return string字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }


}