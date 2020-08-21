package com.wya.env.util;

public class ByteUtil {

    public static byte[] getHeadByteData(byte[] udpByteData) {
        byte[] head_data = new byte[8];
        head_data[0] = 0x53;
        head_data[1] = 0x48;
        head_data[2] = 0x59;
        head_data[3] = 0x55;
        if (udpByteData.length == 4) {
            head_data[4] = 0x04;
            head_data[5] = 0x00;
        }else if (udpByteData.length == 1) {
            head_data[4] = 0x01;
            head_data[5] = 0x00;
        } else if (udpByteData.length == 905) {
            head_data[4] = (byte) 0x89;
            head_data[5] = 0x03;
        } else {
            head_data[4] = (byte) 0x0d;
            head_data[5] = 0x07;
        }
        head_data[6] = (byte) (0xff & Integer.parseInt(CheckDigit(udpByteData), 16));
        head_data[7] = (byte) (0xff & (Integer.parseInt("ff", 16) - Integer.parseInt(CheckDigit(udpByteData), 16)));
        return head_data;
    }


    /**
     * @param udpByteData
     * @return 校验位计算，取低8位为校验位
     */
    private static String CheckDigit(byte[] udpByteData) {
        int sum = 0;
        for (int i = 0; i < udpByteData.length; i++) {
            sum += udpByteData[i];
        }
        String CheckSumBinary = Integer.toBinaryString(sum);
        String CheckSum = "";
        String CheckSum_hex = "";
        if (CheckSumBinary.length() > 8) {
            CheckSum =
                    CheckSumBinary.substring(CheckSumBinary.length() - 8, CheckSumBinary.length());
            sum = Integer.parseInt(CheckSum, 2);
            CheckSum_hex = Integer.toHexString(sum);
        } else {
            sum = Integer.parseInt(CheckSumBinary, 2);
            CheckSum_hex = Integer.toHexString(sum);
        }

        return CheckSum_hex;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static String byte2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            //将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp + " ");
        }
        return sb.toString();
    }

}
