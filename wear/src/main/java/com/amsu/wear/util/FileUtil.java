package com.amsu.wear.util;

import android.util.Base64;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.util
 * @time 2018-03-09 4:10 PM
 * @describe
 */
public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        if(file.exists()){
            try {
                in = new FileInputStream(file);
                byte[] bytes = new byte[in.available()];
                int length = in.read(bytes);
                base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in!=null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return base64;
    }

    //从文件中读取心电数据
    public static List<Integer> readIntArrayDataFromFile(File file) {
        List<Integer> calcuData = new ArrayList<>();  //心电数据
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream); //读取二进制文件

            byte[] bytes = new byte[1024*1024];
            Log.i(TAG,"dataInputStream.available():"+dataInputStream.available());
            Log.i(TAG,"new Date(System.currentTimeMillis()):"+new Date(System.currentTimeMillis()));

            while(dataInputStream.available() >0){
                int read = dataInputStream.read(bytes);
                Log.i(TAG,"read:"+read);
                for (int i = 0; i < read/2; i++) {
                    bytes[0] = bytes[i*2];
                    bytes[1] = bytes[i*2+1];
                    int temp =  getShortByTwoBytes(bytes[0],bytes[1]);
                    calcuData.add(temp);
                    /*if (calcuData.size()<1000){
                        //Log.i(TAG,calcuData.size()+":"+temp);
                    }*/
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return calcuData;
    }

    public static short getShortByTwoBytes(byte argB1, byte argB2) {
        return (short) ((argB1 & 0xFF)| (argB2 << 8));
    }

}
