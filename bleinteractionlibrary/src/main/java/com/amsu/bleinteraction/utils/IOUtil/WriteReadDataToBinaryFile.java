package com.amsu.bleinteraction.utils.IOUtil;

import android.util.Log;

import com.amsu.bleinteraction.utils.DataTypeConversionUtil;
import com.amsu.bleinteraction.utils.EcgFilterUtil_1;
import com.amsu.bleinteraction.utils.FileWriteHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by HP on 2017/4/27.
 */

public class WriteReadDataToBinaryFile implements WriteReadDataToFileStrategy {
    private static final String TAG = "WriteReadDataToBinaryFile";
    private String mFileName;
    private DataOutputStream mDataOutputStream;  //二进制文件输出流，写入文件
    private ByteBuffer mByteBuffer;
    private boolean mIsNeedWriteFileHead;

    public WriteReadDataToBinaryFile(String fileName,boolean isNeedWriteFileHead) {
        this.mFileName = fileName;
        this.mIsNeedWriteFileHead = isNeedWriteFileHead;
    }

    @Override
    public boolean writeDataToFile(List<Integer> integerList, String fileName) {
        DataOutputStream dataOutputStream = null;
        boolean isWriteSuccess = false;
        try {
            dataOutputStream = new DataOutputStream(new FileOutputStream(fileName));
            byte[] bytes = new byte[1024*1024];
            int byteLength = integerList.size()*2;

            int count = byteLength/bytes.length;
            int reminder = byteLength%bytes.length;

            System.out.println("count: "+count);
            System.out.println("reminder: "+reminder);
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < bytes.length/2; j++) {
                    int integer = integerList.get((bytes.length/2)*(count/2)+j);
                    byte[] shortToByte = DataTypeConversionUtil.shortToByte((short)integer);
                    bytes[j*2]  = shortToByte[0];
                    bytes[j*2+1]  = shortToByte[1];
                }
                //写入文件
                dataOutputStream.write(bytes);
            }
            if (reminder>0) {
                int index;
                for (int i = count*bytes.length; i < integerList.size(); i++) {
                    index = i-count*bytes.length;
                    int integer = integerList.get(index);
                    byte[] shortToByte = DataTypeConversionUtil.shortToByte((short)integer);
                    bytes[index*2]  = shortToByte[0];
                    bytes[index*2+1]  = shortToByte[1];
                }
                //写入文件
                dataOutputStream.write(bytes, 0, reminder);;
            }
            dataOutputStream.flush();
            isWriteSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIOStream(dataOutputStream);
        }
        return isWriteSuccess;
    }


    @Override
    public List<Integer> readDataFromFile(String fileName) {
        EcgFilterUtil_1 mEcgFilterUtil_1 = EcgFilterUtil_1.getInstance();

        List<Integer> integerList = new ArrayList<>();
        DataInputStream dataInputStream = null;
        try {
            dataInputStream= new DataInputStream(new FileInputStream(fileName));

            byte[] bytes = new byte[1024*1024];
            Log.i(TAG,"dataInputStream.available():"+dataInputStream.available());
            Log.i(TAG,"new Date(System.currentTimeMillis()):"+new Date(System.currentTimeMillis()));

            while(dataInputStream.available() >0){
                int read = dataInputStream.read(bytes);
                for (int i = 0; i < read/2-1; i++) {
                    bytes[0] = bytes[i*2];
                    bytes[1] = bytes[i*2+1];
                    //滤波处理
                    int shortByTwoBytes = DataTypeConversionUtil.getShortByTwoBytes(bytes[0], bytes[1]);
                    int temp = mEcgFilterUtil_1.miniEcgFilterLp(mEcgFilterUtil_1.miniEcgFilterHp (mEcgFilterUtil_1.NotchPowerLine( shortByTwoBytes, 1)));

                    integerList.add(temp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtil.closeIOStream(dataInputStream);
        }
        return integerList;
    }

    @Override
    public boolean writeByteDataToFile(byte[] bytes, String fileName) {
        boolean isWriteSuccess = false;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(bytes,0,bytes.length);
            isWriteSuccess = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isWriteSuccess;
    }

    //写到文件里，二进制方式写入
    @Override
    public void writeArrayDataToBinaryFile(final int[] ints) {
        try {
            if (mDataOutputStream ==null){
                mDataOutputStream = new DataOutputStream(new FileOutputStream(mFileName,true));  //追加到文件末尾
                Log.i(TAG,"mFileName:"+mFileName);
                mByteBuffer = ByteBuffer.allocate(2);
                if (mIsNeedWriteFileHead){
                    FileWriteHelper.writeECGFileHeadBytes(mDataOutputStream);
                }
            }
            for (int anInt : ints) {
                mByteBuffer.clear();
                mByteBuffer.putShort((short) anInt);
                mDataOutputStream.writeByte(mByteBuffer.get(1));
                mDataOutputStream.writeByte(mByteBuffer.get(0));
            }
        } catch (IOException e) {
            Log.i(TAG,"e:"+e);
            e.printStackTrace();
        }
    }

    @Override
    public void closeArrayDataStreamResource() {
        IOUtil.closeIOStream(mDataOutputStream);
        mDataOutputStream = null;
        mByteBuffer = null;
        mFileName = null;
    }

}
