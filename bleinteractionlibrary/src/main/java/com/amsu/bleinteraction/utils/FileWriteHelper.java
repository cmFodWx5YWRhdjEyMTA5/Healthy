package com.amsu.bleinteraction.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.amsu.bleinteraction.utils.IOUtil.WriteReadDataToBinaryFile;
import com.amsu.bleinteraction.utils.IOUtil.WriteReadDataToFileStrategy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.bleinteraction.utils
 * @time 12/7/2017 10:19 AM
 * @describe
 */
public class FileWriteHelper {
    private static final String TAG = FileWriteHelper.class.getSimpleName();
    public static final int fileExtensionType_ECG = 1; //
    public static final int fileExtensionType_ACC = 2; //

    private WriteReadDataToFileStrategy mWriteECGDataToFileStrategy;
    private WriteReadDataToFileStrategy mWritACCDataToFileStrategy;
    private String mEcgFileName;
    private String mAccFileName;
    private boolean mIsNeedWriteFileHead;
    private boolean mIsrecording;
    private static FileWriteHelper fileWriteHelper;

    public static FileWriteHelper getFileWriteHelper(){
        if (fileWriteHelper==null){
            fileWriteHelper = new FileWriteHelper();
        }
        return fileWriteHelper;
    }

    void writeEcgDataToFile(int[] data){
        if (mIsrecording){
            if (mWriteECGDataToFileStrategy ==null){
                mWriteECGDataToFileStrategy = new WriteReadDataToBinaryFile(mEcgFileName,mIsNeedWriteFileHead);
            }
            mWriteECGDataToFileStrategy.writeArrayDataToBinaryFile(data);
        }
    }

    void writeAccDataToFile(int[] data){
        if (mIsrecording){
            if (mWritACCDataToFileStrategy ==null){
                mWritACCDataToFileStrategy = new WriteReadDataToBinaryFile(mAccFileName,mIsNeedWriteFileHead);
            }
            mWritACCDataToFileStrategy.writeArrayDataToBinaryFile(data);
        }
    }

    //ecg_acc是心电还是加速度 1：ecg，2：acc
    public static String getClolthDeviceLocalFileName(int ecg_acc){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/cloth";
        File file = new File(filePath);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            Log.i(TAG,"mkdirs:"+mkdirs);
        }

        filePath += "/"+ getECGFileNameDependFormatTime(new Date());
        if (ecg_acc== fileExtensionType_ECG){
            return filePath+".ecg";
        }
        else {
            return filePath+".acc";
        }
    }

    private static String getECGFileNameDependFormatTime(Date date){
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);  //07-12 15:10
        return format.format(date);
    }

    public void startRecordingToFile(String ecgFileName,String accFileName,boolean isNeedWriteFileHead){
        this.mEcgFileName  =ecgFileName;
        this.mAccFileName = accFileName;
        this.mIsrecording = true;
        this.mIsNeedWriteFileHead = isNeedWriteFileHead;
    }

    //停止数据记录，并且返回之前写入的文件名
    public String stopRecordingToFileAndGetEcgFileName(){
        this.mIsrecording = false;
        if (mWritACCDataToFileStrategy !=null){
            mWritACCDataToFileStrategy = null;
        }
        if (mWriteECGDataToFileStrategy !=null){
            mWriteECGDataToFileStrategy.closeArrayDataStreamResource();
            mWriteECGDataToFileStrategy = null;
            return mEcgFileName;  //关闭写入流，顺便得到写人文件的文件名
        }
        return null;
    }

    public static void writeECGFileHeadBytes(DataOutputStream dataOutputStream){
        byte[] head = DataTypeConversionUtil.getBytesByAsciiString("AMSU_BETA1",10);
        byte[] clothes_identifier = DataTypeConversionUtil.getBytesByAsciiString("AMSU_E9087896590",24);
        byte[] version = DataTypeConversionUtil.getBytesByAsciiString("v1",4);;  // 硬件版本  v1  v2 …..      ASSIC
        long date_time = System.currentTimeMillis() ;   //date_time[0] = (char)(2017-2000)   // 年月日时分秒
        byte[] name = DataTypeConversionUtil.getBytesByAsciiString("18689463192",16);;
        byte age = 11;   // 年龄
        byte sex = 1;    //男=1 女=2
        short height = 165;   //cm
        short weight = 60;   //kg
        short gain = 3; //增益
        byte leads = 4; //导联数
        short sample_fre = 150;//采样率
        byte ad = 8;//ad位，一代是8位，二代是16位
        byte[] remark = DataTypeConversionUtil.getBytesByAsciiString("xxxxxxx",182);;   //备注   以后备用

        try {
            dataOutputStream.write(head);
            dataOutputStream.write(clothes_identifier);
            dataOutputStream.write(version);

            byte[] date_timeByteArray = DataTypeConversionUtil.longToByteArray(date_time);
            dataOutputStream.write(date_timeByteArray);

            dataOutputStream.write(name);
            dataOutputStream.writeByte(age);
            dataOutputStream.writeByte(sex);

            byte[] heightByteArray = DataTypeConversionUtil.shortToByteArray(height);
            dataOutputStream.write(heightByteArray);

            byte[] weightByteArray = DataTypeConversionUtil.shortToByteArray(weight);
            dataOutputStream.write(weightByteArray);

            byte[] gainByteArray = DataTypeConversionUtil.shortToByteArray(gain);
            dataOutputStream.write(gainByteArray);

            dataOutputStream.writeByte(leads);

            byte[] sample_freByteArray = DataTypeConversionUtil.shortToByteArray(sample_fre);
            dataOutputStream.write(sample_freByteArray);

            dataOutputStream.writeByte(ad);
            dataOutputStream.write(remark);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static EcgFileHead readEcgFileHead(DataInputStream dataInputStream) {
        try {
            byte[] head = new byte[10];
            dataInputStream.read(head);
            String headString = DataTypeConversionUtil.getStringByAsciiBytes(head);

            byte[] clothes_identifier = new byte[24];
            dataInputStream.read(clothes_identifier);
            String clothes_identifierString = DataTypeConversionUtil.getStringByAsciiBytes(clothes_identifier);

            byte[] version = new byte[4];
            dataInputStream.read(version);
            String versionString = DataTypeConversionUtil.getStringByAsciiBytes(version);

            byte[] date_time = new byte[8];
            dataInputStream.read(date_time);

            long date_timeLong = DataTypeConversionUtil.bytesToLong(date_time);

            byte[] name = new byte[16];
            dataInputStream.read(name);
            String nameString = DataTypeConversionUtil.getStringByAsciiBytes(name);

            byte age = dataInputStream.readByte();

            byte sex = dataInputStream.readByte();    //男=1 女=2

            byte[] height = new byte[2];
            dataInputStream.read(height);

            short heightShort = DataTypeConversionUtil.bytesToShort(height);

            byte[] weight = new byte[2];
            dataInputStream.read(weight);
            short weightShort = DataTypeConversionUtil.bytesToShort(weight);


            byte[] gain = new byte[2];
            dataInputStream.read(gain);
            short gainShort = DataTypeConversionUtil.bytesToShort(gain);

            byte leads = dataInputStream.readByte(); //导联数

            byte[] sample_fre = new byte[2];
            dataInputStream.read(sample_fre);

            short sample_freShort = DataTypeConversionUtil.bytesToShort(sample_fre);

            byte ad = dataInputStream.readByte(); ;//ad位，一代是8位，二代是16位

            byte[] remark = new byte[182];
            dataInputStream.read(remark);
            String remarkString = DataTypeConversionUtil.getStringByAsciiBytes(remark);

            System.out.println("headString:"+headString);
            System.out.println("clothes_identifierString:"+clothes_identifierString);
            System.out.println("versionString:"+versionString);
            System.out.println("date_timeLong:"+date_timeLong);
            System.out.println("nameString:"+nameString);
            System.out.println("age:"+age);
            System.out.println("sex:"+sex);
            System.out.println("heightShort:"+heightShort);
            System.out.println("weightShort:"+weightShort);
            System.out.println("gainShort:"+gainShort);
            System.out.println("leads:"+leads);
            System.out.println("sample_freShort:"+sample_freShort);
            System.out.println("ad:"+ad);
            System.out.println("remarkString:"+remarkString);

            if (!TextUtils.isEmpty(headString) && headString.equals("AMSU_BETA1")){
                return new EcgFileHead(headString,clothes_identifierString,versionString,date_timeLong,nameString,age,sex,heightShort,weightShort,gainShort,leads,sample_freShort,ad,remarkString);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static class EcgFileHead{
        public String head;
        public String clothesIdentifier;
        public String version;
        public long dateTime;
        public String name;
        public  byte age;
        public  byte sex;
        public short height;
        public short weight;
        public short gain;
        public byte leads;
        public short sampleFre;
        public byte ad;
        public String remark;

        public EcgFileHead(String head, String clothesIdentifier, String version, long dateTime, String name, byte age, byte sex, short height, short weight, short gain, byte leads, short sampleFre, byte ad, String remark) {
            this.head = head;
            this.clothesIdentifier = clothesIdentifier;
            this.version = version;
            this.dateTime = dateTime;
            this.name = name;
            this.age = age;
            this.sex = sex;
            this.height = height;
            this.weight = weight;
            this.gain = gain;
            this.leads = leads;
            this.sampleFre = sampleFre;
            this.ad = ad;
            this.remark = remark;
        }
    }
}
