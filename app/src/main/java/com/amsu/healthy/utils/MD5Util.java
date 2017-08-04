package com.amsu.healthy.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by HP on 2016/11/30.
 */
public class MD5Util {
    public static String getMD5(String val) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return getString(m);
    }

    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(b[i]);
        }
        return sb.toString();
    }

    public static String getFileMd5Message(String path){
        String resultPassword="";
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("SHA-256");

            File file = new File(path);
            if (file.exists()){
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[1025];
                int read= -1;
                while ((read=fileInputStream.read(bytes,0,bytes.length))!=-1){
                    md5.update(bytes,0,read);
                }
                digest = md5.digest();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (digest!=null){
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b:digest) {
                int i = b & 0xff;
                String s = Integer.toHexString(i);
                if (s.length()==1){
                    stringBuffer.append(0);
                }
                stringBuffer.append(s);
            }
            resultPassword = stringBuffer.toString();
        }
        return resultPassword;
    }
}
