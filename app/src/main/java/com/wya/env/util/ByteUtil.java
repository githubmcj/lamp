package com.wya.env.util;

import com.wya.utils.utils.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ByteUtil {

    public static byte[] getHeadByteData(byte[] udpByteData) {
        byte[] head_data = new byte[8];
        head_data[0] = 0x53;
        head_data[1] = 0x48;
        head_data[2] = 0x59;
        head_data[3] = 0x55;
//
//        head_data[4] = ByteUtil.intToByteArray(udpByteData.length)[0];
//        head_data[5] = ByteUtil.intToByteArray(udpByteData.length)[1];
        LogUtil.e("udpByteDatalen:" + udpByteData.length);

        byte[] len = ByteUtil.intToByteArray(udpByteData.length);
        if (len.length == 1) {
            head_data[4] = len[0];
            head_data[5] = 0x00;
        } else if (len.length == 2) {
            head_data[4] = len[0];
            head_data[5] = len[1];
        }
        LogUtil.e("len:" + bytesToHex(len));

//        if (udpByteData.length == 4) {
//            head_data[4] = 0x04;
//            head_data[5] = 0x00;
//        }else if (udpByteData.length == 1) {
//            head_data[4] = 0x01;
//            head_data[5] = 0x00;
//        }else if (udpByteData.length == 2) {
//            head_data[4] = 0x02;
//            head_data[5] = 0x00;
//        } else if (udpByteData.length == 905) {
//            head_data[4] = (byte) 0x89;
//            head_data[5] = 0x03;
//        } else if (udpByteData.length == 94){
//            head_data[4] = 0x5e;
//            head_data[5] = 0x00;
//        }else if (udpByteData.length == 13){
//            head_data[4] = (byte) 0x0d;
//            head_data[5] = 0x00;
//        } else if (udpByteData.length == 19){
//            head_data[4] = (byte) 0x13;
//            head_data[5] = 0x00;
//        } else {
//            head_data[4] = (byte) 0x0d;
//            head_data[5] = 0x07;
//        }
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

    /**
     * int 转 16进制byte[]
     */
    public static byte[] intToByteArray(int num) {
        byte[] data = hexToByteArray(encodeHEX(num));
        for (int i = 0; i < data.length; i++) {
            byte temp;
            int j = data.length - 1 - i;
            if (i <= j) {
                temp = data[i];
                data[i] = data[j];
                data[j] = temp;
            }
        }
        return data;
    }


    /**
     * hex字符串转byte数组
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }


    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * 將10進制轉換為16進制
     */
    public static String encodeHEX(Integer numb) {

        String hex = Integer.toHexString(numb);
        return hex;

    }

    /**
     * 將16進制字符串轉換為10進制數字
     */
    public static int decodeHEX(String hexs) {
        BigInteger bigint = new BigInteger(hexs, 16);
        int numb = bigint.intValue();
        return numb;
    }

    /**
     * 将接收到byte数组转成String字符串
     *
     * @param bytes 接收的byte数组
     * @return string字符串
     */
    public static String bytesToHex(byte[] bytes) {
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

    /**
     * 16进制字符串转一个字节byte
     *
     * @param value
     * @return
     */
    public static byte hexStringToOneByte(String value) {
        int len = value.length();
        if (len > 2) {
            value = value.substring(0, 2);
        } else if (len < 2) {
            value = addLeftZero(value, 2);
        }
        byte[] bytes = hexStringToBytes(value);
        return bytes[0];
    }

    /**
     * 指定长度左补0
     *
     * @param s
     * @param len
     * @return
     */
    public static String addLeftZero(String s, int len) {
        int tempLen = s.length();
        StringBuffer sbuffer = new StringBuffer();
        if (tempLen < len) {
            int temp = len - tempLen;
            for (int i = 0; i < temp; i++) {
                sbuffer.append("0");
            }
        }
        sbuffer.append(s);
        return sbuffer.toString();
    }

    /**
     * 将16进制数转化为字节，例如AF0AE5,其中AF、0A、E5分别对应一个字节，A表示一个字节的高位1010,
     * F对应一个字节的低位1111，那么该字节就是10101111(将该8bit转化为字节)
     *
     * @param value
     * @return
     */
    public static byte[] hexStringToBytes(String value) {
        byte[] bytes = null;
        StringBuffer valueBuf = new StringBuffer();
        if (value.length() % 2 == 1) {
            valueBuf.append("0");
        }
        valueBuf.append(value);
        char[] values = valueBuf.toString().toCharArray();

        int len = values.length / 2;
        bytes = new byte[len];
        byte upByte, downByte;
        for (int i = 0; i < values.length; i += 2) {
            upByte = Byte.decode("0x" + String.valueOf(values[i]));
            downByte = Byte.decode("0x" + String.valueOf(values[i + 1]));
            bytes[i / 2] = (byte) ((upByte << 4) | downByte);
        }
        return bytes;
    }

    /**
     * 将16进制数转化为字节，例如AF0AE5,其中AF、0A、E5分别对应一个字节，A表示一个字节的高位1010,
     * F对应一个字节的低位1111，那么该字节就是10101111(将该8bit转化为字节)
     *
     * @param value
     * @param byteCount 表示要转化多少个字节，不够的在前面加00
     * @return
     */
    public static byte[] hexStringToBytes(String value, int byteCount) {
        byte[] bytes = null;
        StringBuffer valueBuf = new StringBuffer();
        if (value.length() % 2 == 1) {
            valueBuf.append("0");
        }
        valueBuf.append(value);
        char[] values = valueBuf.toString().toCharArray();
        int len = values.length / 2;
        bytes = new byte[len];
        byte upByte, downByte;
        for (int i = 0; i < values.length; i += 2) {
            upByte = Byte.decode("0x" + String.valueOf(values[i]));
            downByte = Byte.decode("0x" + String.valueOf(values[i + 1]));
            bytes[i / 2] = (byte) ((upByte << 4) | downByte);
        }
        if (len < byteCount) {
            byte[] resultBytes = new byte[byteCount];
            System.arraycopy(bytes, 0, resultBytes, byteCount - len, len);
            return resultBytes;
        }
        return bytes;
    }

    /**
     * byte[]转16进制字符串
     *
     * @param bytes
     * @param isSpace 16进制字符串之间是否加空格
     * @return
     */
    public static String bytesToHexString(byte[] bytes, boolean isSpace) {
        StringBuffer result = new StringBuffer();
        String upByte, downByte;
        for (int i = 0; i < bytes.length; i++) {
            upByte = Integer.toHexString((bytes[i] & 0xF0) >> 4);
            downByte = Integer.toHexString(bytes[i] & 0xF);
            result.append(upByte).append(downByte);
            if (isSpace) {
                result.append(" ");
            }
        }
        return result.toString().toLowerCase().trim();
    }

    /**
     * 将一个字节转化为16进制，转为算法与上面的刚好相反
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        String upByte, downByte;
        for (int i = 0; i < bytes.length; i++) {
            upByte = Integer.toHexString((bytes[i] & 0xF0) >> 4);
            downByte = Integer.toHexString(bytes[i] & 0xF);
            result.append(upByte).append(downByte);
        }
        return result.toString().toUpperCase();
    }

    /**
     * 将一个字节转化为16进制
     *
     * @param bytes
     * @param offset 要转化字节的起始位置
     * @param len    要转化字节的长度
     * @return
     */
    public static String bytesToHexString(byte[] bytes, int offset, int len) {
        StringBuffer result = new StringBuffer();
        String upByte, downByte;
        for (int i = offset; i < offset + len; i++) {
            upByte = Integer.toHexString((bytes[i] & 0xF0) >> 4);
            downByte = Integer.toHexString(bytes[i] & 0xF);
            result.append(upByte).append(downByte);
        }
        return result.toString().toUpperCase();
    }

    /**
     * 将文件里的内容转为内存中的字节数组
     *
     * @param path 文件的位置
     * @return
     */
    public static byte[] fileToBytes(String path) {
        try {
            InputStream is = new FileInputStream(new File(path));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) > 0) {
                baos.write(buff, 0, len);
            }
            baos.close();
            is.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将内存中的字节数组输出到文件中
     *
     * @param bytes
     * @param path
     */
    public static void bytesToFile(byte[] bytes, String path) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            OutputStream os = new FileOutputStream(new File(path));
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = bais.read(buff)) > 0) {
                os.write(buff, 0, len);
            }
            os.close();
            bais.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将多个字节数组合并为一个字节数组
     *
     * @param allResultBytes
     * @return
     */
    public static byte[] bytesAdd(byte[]... allResultBytes) {
        byte[] bytes;
        int allLen = 0;
        int offset, len = 0;
        for (byte[] resultBytes : allResultBytes) {
            len = resultBytes.length;
            allLen += len;
        }
        bytes = new byte[allLen];
        offset = 0;
        for (byte[] resultBytes : allResultBytes) {
            System.arraycopy(resultBytes, 0, bytes, offset, resultBytes.length);
            offset += resultBytes.length;
        }
        return bytes;
    }

    /**
     * 在一个字节数组前面补0
     *
     * @param bytes
     * @param needLen 表示需要的字节长度，不够的话就补0
     * @return
     */
    public static byte[] addBytesBeforeBytes(byte[] bytes, int needLen) {
        int len = bytes.length;
        if (len < needLen) {
            byte[] resultBytes = new byte[needLen];
            System.arraycopy(bytes, 0, resultBytes, needLen - len, len);
            return resultBytes;
        }
        return bytes;
    }


    /**
     * 将多个字节数组合并为一个字节数组
     *
     * @param allResultBytes
     * @return
     */
    public static byte[] bytesAdd(List<byte[]> allResultBytes) {
        byte[] bytes;
        int allLen = 0;
        int offset, len = 0;
        for (byte[] resultBytes : allResultBytes) {
            len = resultBytes.length;
            allLen += len;
        }
        bytes = new byte[allLen];
        offset = 0;
        for (byte[] resultBytes : allResultBytes) {
            System.arraycopy(resultBytes, 0, bytes, offset, resultBytes.length);
            offset += resultBytes.length;
        }
        return bytes;
    }

    /**
     * 将一个大的字节数组分割为多个小的字节数组
     *
     * @param bytes
     * @param step
     * @return
     */
    public static List<byte[]> bytesSplit(byte[] bytes, int step) {
        List<byte[]> allResultBytes = new ArrayList<byte[]>();
        byte[] resultBytes = null;
        int len = bytes.length;
        int count = len % step == 0 ? len / step : len / step + 1;
        int offset = 0, realLen = step;
        for (int i = 1; i <= count; i++) {
            if (count == i) {
                realLen = len - (count - 1) * step;
            }
            resultBytes = new byte[realLen];
            System.arraycopy(bytes, offset, resultBytes, 0, realLen);
            offset += realLen;
            allResultBytes.add(resultBytes);
        }
        return allResultBytes;
    }

    /**
     * 取一个字节数据的部分字节
     *
     * @param bytes
     * @param offset 需要取的起始位置
     * @param len    需要取的长度
     * @return
     */
    public static byte[] bytesPart(byte[] bytes, int offset, int len) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(bytes, offset, len);
            baos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将16进制的字符转化为Int值
     *
     * @param hexString
     * @return
     */
    public static int hexStringToInt(String hexString) {
        return Integer.parseInt(hexString, 16);
    }

    /**
     * 将int值转化为16进制字符串
     *
     * @param value
     * @return
     */
    public static String intToHexString(int value) {
        String result = Integer.toHexString(value).toUpperCase();
        if (result.length() % 2 == 1) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * 将16进制转化为long值
     *
     * @param hexString
     * @return
     */
    public static long hexStringToLong(String hexString) {
        return Long.parseLong(hexString, 16);
    }

    /**
     * 将long值转化为16进制字符串
     *
     * @param value
     * @return
     */
    public static String longToHexString(long value) {
        String result = Long.toHexString(value).toUpperCase();
        if (result.length() % 2 == 1) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * 将16进制的值转化为Int值
     *
     * @param hex
     * @return
     */
    public static int hexToInt(int hex) {
        return Integer.parseInt(String.valueOf(hex), 16);
    }

    /**
     * 将8进制的值转化为Int值
     *
     * @param octal
     * @return
     */
    public static int octalToInt(int octal) {
        return Integer.parseInt(String.valueOf(octal), 8);
    }

    /**
     * 将ascii的序号转化为对应的ascii表示的字符
     *
     * @param value
     * @param radix
     * @return
     */
    public static String asciiNoToAsciiChar(int value, int radix) {
        int decimalValue = value;
        if (radix == 16) {
            decimalValue = ByteUtil.hexToInt(value);
        } else if (radix == 8) {
            decimalValue = ByteUtil.octalToInt(value);
        }
        return String.valueOf((char) decimalValue);
    }

    /**
     * 将16进制表示的ascii的序号转化为对应的ascii表示的字符
     *
     * @param hexString
     * @return
     */
    public static String hexStringAsciiNoToAsciiChar(String hexString) {
        int value = ByteUtil.hexStringToInt(hexString);
        return ByteUtil.asciiNoToAsciiChar(value, 10);
    }

    /**
     * ascii多个字符，所对应的序号
     *
     * @param value
     * @return
     */
    public static String asciiCharsToAsciiNos(String value) {
        try {
            byte[] result = value.getBytes("ascii");
            return ByteUtil.bytesToHexString(result);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ascii码序号对应的字节转化为ascii对应的字符
     *
     * @param value
     * @return
     */
    public static String asciiNosBytesToAsciiChars(byte[] value) {
        try {
            return new String(value, "ascii");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 得到一个字节中BIT位对应的是0还是1，例如字节11100110，postion为0，返回的是0，postion为7，返回的是1
     *
     * @param value
     * @param position
     * @return
     */
    public static int getBitFromByte(byte value, int position) {
        int result = -1;
        switch (position) {
            case 0:
                result = (value & 0x1) == 1 ? 1 : 0;
                break;
            case 1:
                result = (value & 0x2) == 2 ? 1 : 0;
                break;
            case 2:
                result = (value & 0x4) == 4 ? 1 : 0;
                break;
            case 3:
                result = (value & 0x8) == 8 ? 1 : 0;
                break;
            case 4:
                result = (value & 0x10) == 16 ? 1 : 0;
                break;
            case 5:
                result = (value & 0x20) == 32 ? 1 : 0;
                break;
            case 6:
                result = (value & 0x40) == 64 ? 1 : 0;
                break;
            case 7:
                result = (value & 0x80) == 128 ? 1 : 0;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 设置一个字节中BIT位为0还是1
     *
     * @param value
     * @param position
     * @param bitValue
     * @return
     */
    public static byte setBitFromByte(byte value, int position, int bitValue) {
        byte newValue = 0;
        if (getBitFromByte(value, position) == 1) {
            if (bitValue == 0) {
                switch (position) {
                    case 0:
                        newValue = (byte) (value & 0xFE);
                        break;
                    case 1:
                        newValue = (byte) (value & 0xFD);
                        break;
                    case 2:
                        newValue = (byte) (value & 0xFB);
                        break;
                    case 3:
                        newValue = (byte) (value & 0xF7);
                        break;
                    case 4:
                        newValue = (byte) (value & 0xEF);
                        break;
                    case 5:
                        newValue = (byte) (value & 0xDF);
                        break;
                    case 6:
                        newValue = (byte) (value & 0xBF);
                        break;
                    case 7:
                        newValue = (byte) (value & 0x7F);
                        break;
                    default:
                        break;
                }
            } else if (bitValue == 1) {
                newValue = (byte) (value & 0xFF);
            }
        } else if (getBitFromByte(value, position) == 0) {
            if (bitValue == 0) {
                newValue = value;
            } else if (bitValue == 1) {
                switch (position) {
                    case 0:
                        newValue = (byte) (value | 0x1);
                        break;
                    case 1:
                        newValue = (byte) (value | 0x2);
                        break;
                    case 2:
                        newValue = (byte) (value | 0x4);
                        break;
                    case 3:
                        newValue = (byte) (value | 0x8);
                        break;
                    case 4:
                        newValue = (byte) (value | 0x10);
                        break;
                    case 5:
                        newValue = (byte) (value | 0x20);
                        break;
                    case 6:
                        newValue = (byte) (value | 0x40);
                        break;
                    case 7:
                        newValue = (byte) (value | 0x80);
                        break;
                    default:
                        break;
                }
            }
        }
        return newValue;
    }

    /**
     * 在Int值前面补0，如果不够的话
     *
     * @param value
     * @param needLen
     * @return
     */
    public static String addZeroBeforeInt(int value, int needLen) {
        StringBuffer sb = new StringBuffer();
        String valueString = String.valueOf(value);
        int len = valueString.length();
        if (len < needLen) {
            for (int i = 0; i < needLen - len; i++) {
                sb.append("0");
            }
        }
        sb.append(value);
        return sb.toString();
    }

    /**
     * 在long前面补0，如果不够的话
     *
     * @param value
     * @param needLen
     * @return
     */
    public static String addZeroBeforeLong(long value, int needLen) {
        StringBuffer sb = new StringBuffer();
        String valueString = String.valueOf(value);
        int len = valueString.length();
        if (len < needLen) {
            for (int i = 0; i < needLen - len; i++) {
                sb.append("0");
            }
        }
        sb.append(value);
        return sb.toString();
    }

    /**
     * 在string类型前面补0，如果不够的话
     *
     * @param value
     * @param needLen
     * @return
     */
    public static String addZeroBeforeString(String value, int needLen) {
        StringBuffer sb = new StringBuffer();
        if (value.length() < needLen) {
            for (int i = 0; i < needLen - value.length(); i++) {
                sb.append("0");
            }
        }
        sb.append(value);
        return sb.toString();
    }

    /*bytes型HEx数据低位在前转化为int
     * Hex:101212 对应的字节数组  to int str：1184272
     * value: bytes of hex, 低位在前
     * len:需要转化的字节长度,从字节数组的索引0开始
     * */
	/*
	public static String BytesOfInverHexToIntStr(byte[] value,int len)
	{
		
		int oneByte = 0;
		int upByte =0;
		int downByte = 0;
		int resultInt =0;
		for(int i=len;i>0;i--){
		   upByte =(int)((value[i-1]& 0xF0)>>4);
		   downByte =(int)(value[i-1] & 0xF);
		   oneByte = upByte<<4 + downByte;
		   resultInt = resultInt<<8;
		   resultInt = resultInt + oneByte;
		  
		}
		return String.valueOf(resultInt);	     
	}
	*/
    /*低位字节在前 1184272 to HEX str:101212 */
    public static String IntToInverHexString(int value) {

        String result = "";
        String org = Integer.toHexString(value).toUpperCase();
        if (org.length() % 2 == 1) {
            org = "0" + org;
        }
        int index = 0, len;
        len = org.length();
        while (index < len) {
            result = org.substring(index, index + 2) + result;
            index += 2;
        }
        return result;
    }

    // double 转化为1字节小数形式的hex字节型  低位在前 小数字节在前
    //db 
    //1字节小数对应2位有效小数位
    //intLen 整数字节数
    public static byte[] doubleTo1ByteDecimalHexBytes(double db, int intLen) {
        String strDb = "";
        String[] valueArr = null;
        byte[] resultArr = new byte[1 + intLen];
        //2 = 1*2
        strDb = String.format("%.2f", db);
        //分别取出小数部分与整数部分
        valueArr = strDb.split("\\.");
        //小数部分
        //把小数部分转化为低位在前的十六进制字符串
        valueArr[1] = IntToInverHexString(Integer.parseInt(valueArr[1]));
        //小数部分对应的十六进制字符串转为字节型
        resultArr[0] = (hexStringToBytes(valueArr[1]))[0];
        //整数部分
        if (intLen > 0) {
            if (intLen < 4) {
                //把整数部分转化为低位在前的十六进制字符串
                valueArr[0] = ByteUtil.intToHexString(Integer.parseInt(valueArr[0]));
            } else {
                valueArr[0] = ByteUtil.longToHexString(Long.parseLong(valueArr[0]));
            }
            //整数部分对应的十六进制字符串转为字节型
            byte[] intPart = ByteUtil.hexStringToInvertBytes(valueArr[0]);
            System.arraycopy(intPart, 0, resultArr, 1, intPart.length);
        }
        return resultArr;


    }

    // double 转化为2字节小数形式的hex字节型  低位在前 小数字节在前
    //db 
    //2字节小数对应4位有效小数位
    //intLen 整数字节数
    public static byte[] doubleTo2ByteDecimalHexBytes(double db, int intLen) {
        String strDb = "";
        String[] valueArr = null;
        byte[] resultArr = new byte[2 + intLen];
        //4 = 2*2
        strDb = String.format("%.4f", db);
        //分别取出小数部分与整数部分
        valueArr = strDb.split("\\.");
        //小数部分
        //把小数部分转化为低位在前的十六进制字符串
        valueArr[1] = IntToInverHexString(Integer.parseInt(valueArr[1]));
        //小数部分对应的十六进制字符串转为字节型
        byte[] decimalPart = hexStringToBytes(valueArr[1]);
        System.arraycopy(decimalPart, 0, resultArr, 0, decimalPart.length);

        //整数部分
        if (intLen > 0) {
            if (intLen < 4) {
                //把整数部分转化为低位在前的十六进制字符串
                valueArr[0] = ByteUtil.intToHexString(Integer.parseInt(valueArr[0]));
            } else {
                valueArr[0] = ByteUtil.longToHexString(Long.parseLong(valueArr[0]));
            }
            //整数部分对应的十六进制字符串转为字节型
            byte[] intPart = hexStringToInvertBytes(valueArr[0]);
            System.arraycopy(intPart, 0, resultArr, 2, intPart.length);
        }
        return resultArr;

    }

    // double 转化为hex字节型  低位在前 小数字节在前，整数字节在后
    //db 
    //intLen 整数字节数
    //decimalLen 小数字节数
    public static byte[] doubleToDecimalAndIntHexBytes(double db, int intLen, int decimalLen) {
        String strDb = "";
        String[] valueArr = null;
        byte[] resultArr = new byte[decimalLen + intLen];
        //4 = 2*2
        int decimalBit = decimalLen * 2;
        String strDecimalFormat = "%." + Integer.toString(decimalBit) + "f";
        strDb = String.format(strDecimalFormat, db);
        //分别取出小数部分与整数部分
        valueArr = strDb.split("\\.");
        ///
        //小数部分
        //把小数部分转化为低位在前的十六进制字符串
        valueArr[1] = IntToInverHexString(Integer.parseInt(valueArr[1]));
        //小数部分对应的十六进制字符串转为字节型
        byte[] decimalPart = hexStringToBytes(valueArr[1]);
        System.arraycopy(decimalPart, 0, resultArr, 0, decimalPart.length);


        //整数部分
        if (intLen > 0) {
            if (intLen < 4) {
                //把整数部分转化为低位在前的十六进制字符串
                valueArr[0] = IntToInverHexString(Integer.parseInt(valueArr[0]));
            } else {
                valueArr[0] = LongToInverHexString(Long.parseLong(valueArr[0]));
            }
            //整数部分对应的十六进制字符串转为字节型
            byte[] intPart = hexStringToBytes(valueArr[0]);
            System.arraycopy(intPart, 0, resultArr, decimalLen, intPart.length);
        }

        return resultArr;

    }


    /*小数部分和整数部分都是的hex字节型，且 低位在前，小数部分在前，两部分组合字节数组转为double型
     *参数：
     *value 小数和整数两部分组合字节数组
     *decimalLen 小数部分字节长度
     *返回：double
     */
    public static double DecimalAndIntHexBytesToDouble(byte[] value, int decimalLen) {
        StringBuffer result = new StringBuffer();
        int len = value.length;
        //整数部分处理
        byte[] intBytes = new byte[len - decimalLen];
        System.arraycopy(value, decimalLen, intBytes, 0, intBytes.length);
        //
        String intPartOfHex = ByteUtil.InvertBytesTohexString(intBytes);
        String intPart = "0";
        if (intBytes.length < 4) {
            intPart = Integer.toString(Integer.parseInt(intPartOfHex, 16));
        } else {

        }
        //String intPart =ByteUtil.BytesOfInverHexToIntStr(intBytes, intBytes.length);
        result.append(intPart);
        result.append('.');
        //小数部分处理  
        byte[] decimalBytes = new byte[decimalLen];
        System.arraycopy(value, 0, decimalBytes, 0, decimalLen);
        String decimalPartOfHex = ByteUtil.InvertBytesTohexString(decimalBytes);
        int decimalPartOfInt = Integer.parseInt(decimalPartOfHex, 16);
        //String decimalPart =ByteUtil.BytesOfInverHexToIntStr(value, decimalLen);
        //求得小数有效位长度      1个字节对应2个有效位
        int decimalBitLen = decimalLen * 2;
        String decimalFormat = "%0" +
                String.valueOf(decimalBitLen) + "d";

        //2字节小数对应4位有效位小数
        String decimalPart = String.format(decimalFormat, decimalPartOfInt);
        result.append(decimalPart);
        return Double.parseDouble(result.toString());
    }

    // double 转化为hex字节型  低位在前 整数字节在前
    //db 
    //intLen 整数字节数
    //decimalLen 小数字节数
    public static byte[] doubleToIntAndDecimalHexBytes(double db, int intLen, int decimalLen) {
        String strDb = "";
        String[] valueArr = null;
        byte[] resultArr = new byte[decimalLen + intLen];
        //4 = 2*2
        int decimalBit = decimalLen * 2;
        String strDecimalFormat = "%." + Integer.toString(decimalBit) + "f";
        strDb = String.format(strDecimalFormat, db);
        //分别取出小数部分与整数部分
        valueArr = strDb.split("\\.");
        ///
        //整数部分
        if (intLen > 0) {
            if (intLen < 4) {
                //把整数部分转化为低位在前的十六进制字符串
                valueArr[0] = IntToInverHexString(Integer.parseInt(valueArr[0]));
            } else {
                valueArr[0] = LongToInverHexString(Long.parseLong(valueArr[0]));
            }
            //整数部分对应的十六进制字符串转为字节型
            byte[] intPart = hexStringToBytes(valueArr[0]);
            System.arraycopy(intPart, 0, resultArr, 0, intPart.length);
        }
        //小数部分
        //把小数部分转化为低位在前的十六进制字符串
        valueArr[1] = IntToInverHexString(Integer.parseInt(valueArr[1]));
        //小数部分对应的十六进制字符串转为字节型
        byte[] decimalPart = hexStringToBytes(valueArr[1]);
        System.arraycopy(decimalPart, 0, resultArr, intLen, decimalPart.length);

        return resultArr;

    }


    /*小数部分和整数部分都是的hex字节型，且 低位在前，小数部分在后，两部分组合字节数组转为double型
     *参数：
     *value 小数和整数两部分组合字节数组
     *decimalLen 小数部分字节长度
     *返回：double
     */
    public static double IntAndDecimalHexBytesToDouble(byte[] value, int decimalLen) {
        StringBuffer result = new StringBuffer();
        int len = value.length;
        //整数部分处理
        byte[] intBytes = new byte[len - decimalLen];
        System.arraycopy(value, 0, intBytes, 0, intBytes.length);
        //
        String intPartOfHex = ByteUtil.InvertBytesTohexString(intBytes);
        String intPart = "";
        if (intBytes.length < 4) {
            intPart = Integer.toString(Integer.parseInt(intPartOfHex, 16));
        } else {
            intPart = Long.toString(Long.parseLong(intPartOfHex, 16));
        }
        //String intPart =ByteUtil.BytesOfInverHexToIntStr(intBytes, intBytes.length);
        result.append(intPart);
        result.append('.');
        //小数部分处理  
        byte[] decimalBytes = new byte[decimalLen];
        System.arraycopy(value, len - decimalLen, decimalBytes, 0, decimalLen);
        String decimalPartOfHex = ByteUtil.InvertBytesTohexString(decimalBytes);
        int decimalPartOfInt = Integer.parseInt(decimalPartOfHex, 16);
        //String decimalPart =ByteUtil.BytesOfInverHexToIntStr(value, decimalLen);
        //求得小数有效位长度      1个字节对应2个有效位
        int decimalBitLen = decimalLen * 2;
        String decimalFormat = "%0" +
                String.valueOf(decimalBitLen) + "d";

        //2字节小数对应4位有效位小数
        String decimalPart = String.format(decimalFormat, decimalPartOfInt);
        result.append(decimalPart);
        return Double.parseDouble(result.toString());
    }

    /*低位字节在前 1184272 to HEX str:101212 */
    public static String LongToInverHexString(long value) {

        String result = "";
        String org = Long.toHexString(value).toUpperCase();
        if (org.length() % 2 == 1) {
            org = "0" + org;
        }
        int index = 0, len;
        len = org.length();
        while (index < len) {
            result = org.substring(index, index + 2) + result;
            index += 2;
        }
        return result;
    }

    //
    public static String IntToBCDString(int value) {
        String result = Integer.toString(value);
        if (result.length() % 2 == 1) {
            result = "0" + result;
        }
        return result;

    }

    //十六进制字符串转化为低位字节在前的字节数组
    public static byte[] hexStringToInvertBytes(String value) {

        byte[] dataBytes = null;
        String tempStr = "";
        if (value.length() % 2 == 1) {
            tempStr = "0";
        }
        tempStr = tempStr + value;
        dataBytes = ByteUtil.hexStringToBytes(tempStr);
        //逆序
        int len = dataBytes.length;
        byte[] retBytes = new byte[len];
        int index = 0;
        while (index < len) {
            retBytes[index] = dataBytes[len - 1 - index];
            index++;
        }
        return retBytes;
    }


    //低位字节在前的字节数组转化为十六进制字符串
    public static String InvertBytesTohexString(byte[] value) {
        int index = 0;
        int len = value.length;
        String retStr = "";
        String tempStr = null;
        while (index < len) {
            tempStr = ByteUtil.bytesToHexString(value, index, 1);
            retStr = tempStr + retStr;
            index++;
        }
        return retStr;
    }

    //低位字节在前的字节数组转化为十六进制字符串	
    public static String InvertBytesTohexString(byte[] value, int pos, int len) {

        int index = pos;
        //int len = value.length;
        int lastIndex = len + pos;
        String retStr = "";
        String tempStr = null;
        while (index < lastIndex) {
            tempStr = ByteUtil.bytesToHexString(value, index, 1);
            retStr = tempStr + retStr;
            index++;
        }
        return retStr;
    }

    /**
     * ascii多个字符，所对应的字节
     *
     * @param value
     * @return
     */
    public static byte[] asciiCharsToAsciiBytes(String value) {
        try {
            byte[] result = value.getBytes("ascii");
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
	/*
	public static void rf_main(String[] args){
		
	}*/

    public static String bcdByte2MeterAddressStr(byte[] data, int offset, int count) {
        if (data == null || data.length < offset + count) {
            return null;
        }
        String meterAddress = "";
        int value = 0;

        //count should be 6
        for (int k = 0; k < count; k++) {
            value = ByteUtil.bcdByteTointLe99(data[offset + 5 - k]);
            if (value < 10) {
                meterAddress += "0" + String.valueOf(value);
            } else {
                meterAddress += String.valueOf(value);
            }


        }

        return meterAddress;


    }

    public static int bcdByteTointLe99(byte data) {
        int lowValue = data & 0x0F;
        int highValue = (data & 0xF0) >>> 4;

        int value = highValue * 10 + lowValue;
        return value;

    }

}
