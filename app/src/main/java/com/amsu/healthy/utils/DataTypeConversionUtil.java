package com.amsu.healthy.utils;

import java.io.UnsupportedEncodingException;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils
 * @time 11/29/2017 10:43 AM
 * @describe
 */
public class DataTypeConversionUtil {

    public static String getStringByAsciiBytes(byte[] bytes) {
        int count = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i]!=0) {
                count++;
            }
        }
        String ascii = null;
        try {
            ascii = new String(bytes,0,count, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ascii;
    }

    public static byte[] getBytesByAsciiString(String value, int bytesLength) {
        byte[] bytes = new byte[bytesLength];

        byte[] StringBytes = value.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            if (i<StringBytes.length) {
                bytes[i] = StringBytes[i];
            }
        }
        return bytes;
    }

    public static byte[] shortToByteArray(short s) {
        byte[] shortBuf = new byte[2];
        for(int i=0;i<2;i++) {
            int offset = (shortBuf.length - 1 -i)*8;
            shortBuf[shortBuf.length - 1 -i] = (byte)((s>>>offset)&0xff);
        }
        return shortBuf;
    }

    public static short bytesToShort(byte[] bytes) {
        return (short) ((bytes[0] & 0xFF)| (bytes[1] << 8));
    }

    public static long bytesToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    public static byte[] longToByteArray(long s) {
        byte[] shortBuf = new byte[8];
        for(int i=0;i<8;i++) {
            int offset = (shortBuf.length - 1 -i)*8;
            shortBuf[shortBuf.length - 1 -i] = (byte)((s>>>offset)&0xff);
        }
        return shortBuf;
    }
}
