package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.HistoryRecordAdapter;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by HP on 2017/4/6.
 */

public class UploadOfflineFileActivity extends BaseActivity {

    private static final String TAG = "UploadOfflineFile";
    private ListView lv_history_upload;
    private List<HistoryRecord> uploadHistoryRecords;
    private OutputStream socketWriter;
    boolean isStartUploadData;
    private List<String> mOffLineFileNameList;
    int onePackageReadLength = 0;
    List<Integer> onePackageData = new ArrayList<>();
    String onePackageDataHexString = "";
    private List<Integer> mAllData = new ArrayList<>();

    private String currentUploadFileName;

    int mUploadFileCountIndex;  //当前传输的索引
    int mOneUploadMaxByte = 512*16;
    int mAllFileCount ;  //文件分几次上传，mAllFileCount = 按mOneUploadMaxByte传的次数(整数上传)
    int mFileLastRemainder ;  //最后一次需要上传的，mFileLastRemainder = 文件字节数%mOneUploadMaxByte
    long startTimeMillis = 0;
    long endTimeMillis;
    int requireRetransmissionCount;
    private HistoryRecordAdapter uploadHistoryRecordAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        initView();
        initData();

    }

    private void initView() {
        initHeadView();
        setCenterText("同步离线文件");
        setLeftImage(R.drawable.back_icon);
        setRightImage(R.drawable.tongbu_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"uploadHistoryRecords.size():"+uploadHistoryRecords.size());
                if (uploadHistoryRecords.size()>0){
                    ProgressBar pb_item_progress = (ProgressBar) lv_history_upload.getChildAt(0).findViewById(R.id.pb_item_progress);
                    pb_item_progress.setProgress(50);
                    Log.i(TAG,"设置进度");
                }
            }
        });

        lv_history_upload = (ListView) findViewById(R.id.lv_history_upload);

        uploadHistoryRecords = new ArrayList<>();
        uploadHistoryRecordAdapter = new HistoryRecordAdapter(uploadHistoryRecords,this);
        lv_history_upload.setAdapter(uploadHistoryRecordAdapter);

        mOffLineFileNameList = new ArrayList<>();

        DeviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"onTomeOut 判断是否需要重传");
                judgeRequireRetransmission();
            }
        });

    }

    private void initData() {


        //MyUtil.showDialog("检查离线数据",this);


        if (ConnectToWifiModuleGudieActivity2.mSock!=null){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        socketWriter = ConnectToWifiModuleGudieActivity2.mSock.getOutputStream();
                        InputStream inputStream = ConnectToWifiModuleGudieActivity2.mSock.getInputStream();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getFileList();
                            }
                        });

                        //byte[] bytes = new byte[2048];
                        final byte[] bytes = new byte[1024*10];
                        //byte[] bytes = new byte[512+14];
                        int length;
                        while ((length =inputStream.read(bytes))!=-1) {
                            Log.i(TAG, "length:" + length);
                            if (isStartUploadData) {
                                DeviceOffLineFileUtil.stopTime();
                                final String toHexString = DeviceOffLineFileUtil.binaryToHexString(bytes, length);
                                //final String s = DeviceOffLineFileUtil.binaryToHexString(bytes, length,"");
                                Log.i(TAG, "收到数据:" + toHexString);
                                //开始传输数据了
                                //dealWithDeviceFileUpload(toHexString,length);
                                dealWithDeviceFileUpload(length, toHexString);
                                DeviceOffLineFileUtil.startTime();
                            } else {
                                final String toHexString = DeviceOffLineFileUtil.binaryToHexString(bytes, length);
                                //final String s = DeviceOffLineFileUtil.binaryToHexString(bytes, length,"");
                                Log.i(TAG, "收到数据:" + toHexString);

                                if (toHexString.startsWith("FF 81")) {  //版本号：
                                    dealWithDeviceVersion(toHexString);
                                } else if (toHexString.startsWith("FF 82")) {//设备id:
                                    dealWithDeviceID(toHexString);
                                } else if (toHexString.startsWith("FF 83")) {  //文件列表：
                                    dealWithDeviceFileList(toHexString);
                                } else if (toHexString.startsWith("FF 84")) {  //文件长度：
                                    dealWithDeviceFileLength(toHexString);
                                } else if (toHexString.startsWith("FF 85")) {  //上传文件：
                                    //startTimeOutTiming();
                                    //dealWithDeviceFileUpload(toHexString,length);
                                    dealWithDeviceFileUpload(length, toHexString);
                                    DeviceOffLineFileUtil.startTime();
                                    isStartUploadData = true;
                                } else if (toHexString.startsWith("FF 86")) {  //删除文件：
                                    dealWithDeviceDeleteFile(toHexString);
                                } else if (toHexString.startsWith("FF 87")) {  //一键删除文件：
                                    dealWithDeviceDeleteAllFile(toHexString);
                                } else if (toHexString.startsWith("FF 88")) {  //生成文件：
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    });
                                }
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }
    }



    //给设备发送16进制指令
    private void sendReadDeviceOrder(String deviceOrder) {
        byte[] bytes = DeviceOffLineFileUtil.hexStringToBytes(deviceOrder);
        if (socketWriter!=null){
            try {
                socketWriter.write(bytes);
                Log.i(TAG,"发送命令：" + deviceOrder);
                socketWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getFileList() {
        MyUtil.showDialog("检查离线数据",this);
        sendReadDeviceOrder(DeviceOffLineFileUtil.readDeviceFileList);
    }


    //版本号：
    private void dealWithDeviceVersion(final String toHexString) {
        // FF 81 00 0C 10 04 07 10 04 07 C2 16
        String[] split = toHexString.split(" ");
        String deviceVersion = "";
        for (int i = 4; i <10; i++) {
            deviceVersion += Integer.parseInt(split[i], 16);
        }
        // deviceVersion:16471647
        Log.i(TAG,"deviceVersion:"+deviceVersion);
        final String finalDeviceVersion = deviceVersion;


    }

    //设备id:
    private void dealWithDeviceID(final String allHexString) {
        //  "FF 82 00 12 AF C1 50 3E 07 00 F8 FF 04 B3 FB 4C 8D 16";
        String[] split = allHexString.split(" ");
        String hexString ="";
        for (int i = 15; i >=4; i--) {
            hexString += split[i];
        }
        final String deviceID = parseHexStringToBigInt(hexString);
        //deviceID:23825146522977399412272185775
        Log.i(TAG,"deviceID:"+deviceID);

    }

    //文件列表：
    private void dealWithDeviceFileList(final String allHexString) {
        mOffLineFileNameList.clear();
        // 32 30 31 37 30 34 31 32 31 30 32 33 30 30 2E 65 63 67
        String[] split = allHexString.split(" ");
        int fileLength = Integer.parseInt(split[4], 16);
        Log.i(TAG,"fileLength:"+fileLength);
        String[] fileList = new String[fileLength];

        for (int i = 0; i < fileLength; i++) {
            String fileNameString ="";
            for (int j = 0; j < 18; j++) {
                fileNameString += DeviceOffLineFileUtil.hexStringToString(split[5+i*18+j]);
            }
            // fileNameString:20170412102300.ecg
            // fileNameString:2017 04 12 10 23 00.ecg
            Log.i(TAG,"fileNameString:"+fileNameString);
            fileList[i] = fileNameString;
            mOffLineFileNameList.add(i,fileNameString);

            String datatime = fileNameString.substring(0,4)+"-"+fileNameString.substring(4,6)+"-"+fileNameString.substring(6,8)+" "+
                    fileNameString.substring(8,10)+":"+fileNameString.substring(10,12)+":"+fileNameString.substring(12,14);
            /*if (uploadHistoryRecords.size()>0){
                if (uploadHistoryRecords.get(uploadHistoryRecords.size()-1).equals(datatime)){
                    return;
                }
            }*/
            uploadHistoryRecords.add(new HistoryRecord("0",datatime,-1,"待同步"));
            
        }
        Log.i(TAG,"fileList.length:"+fileList.length);

        String test ="";
        for (int i = 0; i < fileLength; i++) {
            test += fileList[i]+"\n";
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uploadHistoryRecordAdapter.notifyDataSetChanged();
                MyUtil.hideDialog();

                if (mOffLineFileNameList.size()>0){

                    String fileName = mOffLineFileNameList.get(0);
                    currentUploadFileName = fileName;

                    //String fileName = "20170413172800.ecg";
                    String startOrder = "FF040018";
                    String deviceOrder = startOrder + DeviceOffLineFileUtil.stringToHexString(fileName) + DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum("FF 04 00 18",fileName)+"16";
                    Log.i(TAG,"deviceOrder:"+deviceOrder);
                    sendReadDeviceOrder(deviceOrder);
                    isStartUploadData = false;
                }


                
            }
        });


    }

    /*//文件长度
    private void dealWithDeviceFileLength(String allHexString) {
        //FF 84 00 0A 00 00 02 00 8F 16
        String[] split = allHexString.split(" ");
        String hexLength = split[4]+split[5]+split[6]+split[7];
        int fileLengthInt = Integer.parseInt(hexLength, 16);
        Log.i(TAG,"fileLengthInt:"+fileLengthInt);

        recive_text.setText("fileLengthInt:\n"+fileLengthInt);

    }*/

    //文件长度
    private void dealWithDeviceFileLength(String allHexString) {
        //FF 84 00 0A 00 00 02 00 8F 16
        String[] split = allHexString.split(" ");
        String hexLength = split[4]+split[5]+split[6]+split[7];
        final int fileLengthInt = Integer.parseInt(hexLength, 16);
        Log.i(TAG,"fileLengthInt:"+fileLengthInt);

        //servic_info.setText("fileLengthInt:"+fileLengthInt);
        mUploadFileCountIndex = 0;

        if (fileLengthInt>mOneUploadMaxByte){
            mAllFileCount = fileLengthInt / mOneUploadMaxByte; // 需要传的次数
            mFileLastRemainder = fileLengthInt % mOneUploadMaxByte;  //余数，最后一次需要的传的次数
            Log.i(TAG,"需要传的次数 mAllFileCount:"+mAllFileCount);
            Log.i(TAG,"余数 mFileLastRemainder:"+mFileLastRemainder);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    float fileLength = (float) (fileLengthInt/(1024*1024.0));
                    DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                    String fileLengthAtM=decimalFormat.format(fileLength)+"M";//format 返回的是字符串
                    //tv_filelength.setText("文件长度 字节:"+fileLengthInt+"    "+fileLengthAtM+"  需要传输次数(一次8K):"+(mAllFileCount+1));
                }
            });

            String offsetHexLenght = "";
            offsetHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mOneUploadMaxByte*mUploadFileCountIndex);


            //String offsetHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, offsetTest);
            String fileHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mOneUploadMaxByte);
            String startOrder = "FF05000e"+offsetHexLenght+fileHexLenght;
            String deviceOrder = startOrder+DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum(startOrder)+"16";
            Log.i(TAG,mUploadFileCountIndex+"分段上传deviceOrder:"+deviceOrder);
            sendReadDeviceOrder(deviceOrder);
        }
        else {
            mAllFileCount = 0; // 需要传的次数
            mFileLastRemainder = fileLengthInt % mOneUploadMaxByte;  //余数，最后一次需要的传的次数

            String offsetHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, 0);
            String fileHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, fileLengthInt);
            String startOrder = "FF05000e"+offsetHexLenght+fileHexLenght;
            String deviceOrder = startOrder+DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum(startOrder)+"16";
            Log.i(TAG,"一次上传deviceOrder:"+deviceOrder);
            sendReadDeviceOrder(deviceOrder);
        }
    }

    //文件上传
    private void dealWithDeviceFileUpload(int length, final String toHexString) {
        if (startTimeMillis==0){
            startTimeMillis = System.currentTimeMillis();
        }
        /*if (startTimeMillis==0){
            startTimeMillis = System.currentTimeMillis();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
                    serverMsg ="主机hex: "+df.format(new Date()) +":\n"+ toHexString;
                    recive_msg.setText(serverMsg);
                }
            });
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serverMsg += "\n" +toHexString;
                    recive_msg.setText(serverMsg);
                }
            });

        }*/

        if (mUploadFileCountIndex<mAllFileCount){
            //前几次整数上传

            //int allHexlength = Integer.parseInt(allHexString, 16);

            //List<Integer> geIntEcgaArr = ECGUtil.geIntEcgaArrList(allHexString, " ", 12 ,allHexlength);
            //onePackageData.addAll(geIntEcgaArr);
            onePackageReadLength += length;
            onePackageDataHexString += toHexString;
            Log.i(TAG,"分包 当前收到总长度length:"+onePackageReadLength);
            if (onePackageReadLength==mOneUploadMaxByte+16*14){
                Log.i(TAG,"当前包上传成功:"+mUploadFileCountIndex);

                float progress = (float)(mUploadFileCountIndex+1)/mAllFileCount;
                ProgressBar pb_item_progress = (ProgressBar) lv_history_upload.getChildAt(mUploadFileIndex).findViewById(R.id.pb_item_progress);
                pb_item_progress.setProgress((int)(progress*100));
                Log.i(TAG,"设置进度:"+progress);




                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //tv_uploadprogress.setText("当前进度:"+mUploadFileCountIndex);
                    }
                });

                //writeEcgDataToBinaryFile(onePackageDataHexString,16);
                DeviceOffLineFileUtil.addEcgDataToList(onePackageDataHexString,mAllData);
                //onePackageData.clear();
                onePackageReadLength = 0;
                onePackageDataHexString = "";
                uploadNextPackageData();

                if (mUploadFileCountIndex==mAllFileCount-1 && mFileLastRemainder==0){
                    //上传完成
                    uploadCurrentFileSuccess();
                }
            }
        }
        else if (mUploadFileCountIndex==mAllFileCount){

            //int allHexlength = Integer.parseInt(allHexString, 16);

            //List<Integer> geIntEcgaArr = ECGUtil.geIntEcgaArrList(allHexString, " ", 12 ,uploadLengthInt);
            //onePackageData.addAll(geIntEcgaArr);
            onePackageReadLength += length;
            onePackageDataHexString += toHexString;
            Log.i(TAG,"余数 当前包收到总长度length:"+onePackageReadLength);
            //Log.i(TAG,"mFileLastRemainder:"+mFileLastRemainder);
            if (onePackageReadLength == mFileLastRemainder+(int) Math.ceil(mFileLastRemainder/512.0)*14){
                Log.i(TAG,"余数上传成功:"+mUploadFileCountIndex);

                if (mAllFileCount>0){
                    float progress = (float)(mUploadFileCountIndex+1)/mAllFileCount;
                    ProgressBar pb_item_progress = (ProgressBar) lv_history_upload.getChildAt(mUploadFileIndex).findViewById(R.id.pb_item_progress);
                    pb_item_progress.setProgress((int)(progress*100));
                    Log.i(TAG,"设置进度:"+progress);
                }
                else {
                    ProgressBar pb_item_progress = (ProgressBar) lv_history_upload.getChildAt(mUploadFileIndex).findViewById(R.id.pb_item_progress);
                    pb_item_progress.setProgress(100);
                    Log.i(TAG,"设置进度:"+100);
                }


                //则文件数据传完，isStartUploadData置为false
                DeviceOffLineFileUtil.addRemainderEcgDataToList(onePackageDataHexString,onePackageReadLength, mAllData);
                isStartUploadData = false;
                onePackageReadLength = 0;
                onePackageDataHexString = "";
                //writeEcgDataToBinaryFile(onePackageData);
                //onePackageData.clear();

                uploadCurrentFileSuccess();


            }
        }
    }

    //当前文件上传成功
    private void uploadCurrentFileSuccess() {
        //上传完成
        endTimeMillis = System.currentTimeMillis();
        Log.i(TAG,"整个文件上传完成");
        Log.i(TAG,"startTimeMillis"+new Date(startTimeMillis));
        Log.i(TAG,"endTimeMillis"+new Date(endTimeMillis));

        Log.i(TAG,"求情重传次数requireRetransmissionCount "+requireRetransmissionCount);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"endTimeMillis - startTimeMillis："+(endTimeMillis - startTimeMillis));
                double timeMin =  (endTimeMillis - startTimeMillis) / (1000 * 60.0);
                Log.i(TAG,"timeMin："+timeMin);

                DecimalFormat decimalFormat=new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                String time=decimalFormat.format(timeMin);//format 返回的是字符串
                Log.i(TAG,"time："+time);
                //tv_uploadprogress.setText("传输完成  耗时(分钟):"+time+"  丢包次数："+requireRetransmissionCount);
                startTimeMillis = 0;
            }
        });

        boolean isWriteSuccess = DeviceOffLineFileUtil.writeEcgDataToBinaryFile(mAllData, currentUploadFileName);
        Log.i(TAG,"写入文件isWriteSuccess:"+isWriteSuccess);


        mUploadFileIndex++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mOffLineFileNameList.size()>0){

                    String fileName = mOffLineFileNameList.get(mUploadFileIndex);
                    currentUploadFileName = fileName;

                    //String fileName = "20170413172800.ecg";
                    String startOrder = "FF040018";
                    String deviceOrder = startOrder + DeviceOffLineFileUtil.stringToHexString(fileName) + DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum("FF 04 00 18",fileName)+"16";
                    Log.i(TAG,"deviceOrder:"+deviceOrder);
                    sendReadDeviceOrder(deviceOrder);
                    isStartUploadData = false;
                }

            }
        });

    }

    int mUploadFileIndex;

    //判断是否需要重传
    private void judgeRequireRetransmission() {
        if (mUploadFileCountIndex==mAllFileCount){
            //文件长传完成
            DeviceOffLineFileUtil.stopTime();
            //DeviceOffLineFileUtil.destoryTime();
        }
        else {
            requireRetransmission();
        }
    }

    //请求重传
    private void requireRetransmission() {
        Log.i(TAG,"请求重传requireRetransmission:");
        requireRetransmissionCount++;
        onePackageData.clear();
        onePackageReadLength = 0;
        mUploadFileCountIndex--;
        uploadNextPackageData();

    }

    //当前包传输成功，进行下一个包传输
    private void uploadNextPackageData(){
        mUploadFileCountIndex++;
        if (mUploadFileCountIndex<mAllFileCount){
            String offsetHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mOneUploadMaxByte*mUploadFileCountIndex);
            String fileHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mOneUploadMaxByte);
            String startOrder = "FF05000e"+offsetHexLenght+fileHexLenght;
            String deviceOrder = startOrder+DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum(startOrder)+"16";
            Log.i(TAG,mUploadFileCountIndex+"分段上传deviceOrder:"+deviceOrder);
            sendReadDeviceOrder(deviceOrder);
        }
        else {
            if (mFileLastRemainder>0){
                String offsetHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mOneUploadMaxByte*mUploadFileCountIndex);
                String fileHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mFileLastRemainder);
                String startOrder = "FF05000e"+offsetHexLenght+fileHexLenght;
                String deviceOrder = startOrder+DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum(startOrder)+"16";
                Log.i(TAG,"余数上传deviceOrder:"+deviceOrder);
                sendReadDeviceOrder(deviceOrder);
                //mFileLastRemainder = 0;
            }
            else {
                //没有余数，刚好整除，则文件数据传完，isStartUploadData置为false
                isStartUploadData = false;
            }
        }
    }


    private void dealWithDeviceDeleteFile(String allHexString) {
        String[] split = allHexString.split(" ");
        final int deleteState = Integer.parseInt(split[4], 16);
        /*
        * 0：表示删除成功失败；
            1：没有该文件；
            2：删除成功失败。
            */

        Log.i(TAG,"deleteState:"+deleteState);

    }

    private void dealWithDeviceDeleteAllFile(String allHexString) {
        String[] split = allHexString.split(" ");
        final int deleteState = Integer.parseInt(split[4], 16);
        /*
        * 0：表示删除成功失败；
            1：没有该文件；
            2：删除成功失败。
            */

        Log.i(TAG,"deleteState:"+deleteState);


    }


    private  String parseHexStringToBigInt(String hexString){
        BigInteger bigInteger = new BigInteger(hexString, 16);
        return bigInteger.toString();
    }

}








