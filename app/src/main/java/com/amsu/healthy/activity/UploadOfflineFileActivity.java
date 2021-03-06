package com.amsu.healthy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.HistoryRecordAdapter;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTransmit.DeviceOffLineFileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HP on 2017/4/6.
 */

public class UploadOfflineFileActivity extends BaseActivity {

    private static final String TAG = UploadOfflineFileActivity.class.getSimpleName();
    private ListView lv_history_upload;
    private List<HistoryRecord> mUploadHistoryRecords;
    private OutputStream mSocketWriter;
    private boolean mIsStartUploadData = false;
    private List<String> mOffLineFileNameList;
    private List<String> mEcgOffLineFileNameList;
    private List<String> mAccOffLineFileNameList;

    private Map<String,String> mEcgLocalOffLineFileNameList;
    private Map<String,String> mAccLocalOffLineFileNameList;

    private List<String> mLocalFileNameList;
    int onePackageReadLength = 0;
    List<Integer> onePackageData = new ArrayList<>();
    String onePackageDataHexString = "";
    private List<Integer> mAllData = new ArrayList<>();

    private String mCurrentUploadFileName;

    int mCurUploadFilePackageIndex = -1;  //当前传输的索引
    int mOneUploadMaxByte = 512*16;  //一包的大小，8K
    int mAllFileCount ;  //文件分几次上传，mAllFileCount = 按mOneUploadMaxByte传的次数(整数上传)
    int mFileLastRemainder ;  //最后一次需要上传的，mFileLastRemainder = 文件字节数%mOneUploadMaxByte
    long startTimeMillis = 0;
    long endTimeMillis;
    int requireRetransmissionCount;
    private HistoryRecordAdapter uploadHistoryRecordAdapter;
    private int analyCount;
    private DeviceOffLineFileUtil deviceOffLineFileUtil;
    private InputStream inputStream;
    private ImageView iv_base_myreport;
    private boolean isGetFileList;

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
                judgeIsFinshActivity();
            }
        });

        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGetFileList){
                    MyUtil.showToask(UploadOfflineFileActivity.this,"正在传输数据，不需要重传");
                }else {
                    MyUtil.showDialog(getResources().getString(R.string.connected_check_offline_files),UploadOfflineFileActivity.this);
                    mUploadHistoryRecords.clear();
                    uploadHistoryRecordAdapter.notifyDataSetChanged();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getFileList();
                            sleepWaitGetFileList();
                        }
                    }){}.start();
                }
            }
        });

        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                judgeIsFinshActivity();
            }
        });

        iv_base_myreport = (ImageView) findViewById(R.id.iv_base_myreport);
        iv_base_myreport.setImageResource(R.drawable.ic_settings_white_24dp);
        iv_base_myreport.setVisibility(View.VISIBLE);

        iv_base_myreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadOfflineFileActivity.this, ConnectToWifiGudieActivity1.class));
            }
        });



        lv_history_upload = (ListView) findViewById(R.id.lv_history_upload);

        mUploadHistoryRecords = new ArrayList<>();
        uploadHistoryRecordAdapter = new HistoryRecordAdapter(mUploadHistoryRecords,this,0);
        lv_history_upload.setAdapter(uploadHistoryRecordAdapter);

        mOffLineFileNameList = new ArrayList<>();
        mLocalFileNameList = new ArrayList<>();
        mEcgOffLineFileNameList = new ArrayList<>();
        mAccOffLineFileNameList = new ArrayList<>();
        mEcgLocalOffLineFileNameList = new HashMap<>();
        mAccLocalOffLineFileNameList = new HashMap<>();

        deviceOffLineFileUtil = new DeviceOffLineFileUtil();
        deviceOffLineFileUtil.setTransferTimeOverTime(new DeviceOffLineFileUtil.OnTimeOutListener() {
            @Override
            public void onTomeOut() {
                Log.i(TAG,"onTomeOut 判断是否需要重传");
                judgeRequireRetransmission();
            }
        });

        lv_history_upload.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"onItemClick:"+position);
                //文件长度是空的，点击不需要弹出分析选择
                for (int i:mEmptyIndexItemArray){
                    if (i==position){
                        MyUtil.showToask(UploadOfflineFileActivity.this,getResources().getString(R.string.insufficient_to_analyze));
                        return;
                    }
                }

                if (position<mEcgOffLineFileNameList.size()){
                    chooseDataState(position);
                }
            }
        });

    }

    private void judgeIsFinshActivity() {
        int needAnalyCount = mEcgOffLineFileNameList.size() - mEmptyIndexItemArray.size(); //不是空的列表,需要分析
        if (needAnalyCount==analyCount){
            //都分析了
            finish();

        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(UploadOfflineFileActivity.this)

                    .setTitle(getResources().getString(R.string.exit_sure_offline_files))
                    .setPositiveButton(getResources().getString(R.string.exit_confirm), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.exit_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    private boolean isAnalyed;

    //弹出分析选择
    private void chooseDataState(final int position) {
        final String ecgLocalFileName = mEcgLocalOffLineFileNameList.get(position+"");
        final String accLocalFileName = mAccLocalOffLineFileNameList.get(position+"");
        final String offLineFileName = mOffLineFileNameList.get(position);
        Log.i(TAG,"ecgLocalFileName:"+ecgLocalFileName);

        final Intent intent = new Intent(UploadOfflineFileActivity.this, HeartRateAnalysisActivity.class);
        intent.putExtra(Constant.ecgLocalFileName,ecgLocalFileName);
        /*Date date = new Date(Integer.parseInt(offLineFileName.substring(0,4))-1900,Integer.parseInt(offLineFileName.substring(4,6)),Integer.parseInt(offLineFileName.substring(6,8)),
                Integer.parseInt(offLineFileName.substring(8,10)),Integer.parseInt(offLineFileName.substring(10,12)),Integer.parseInt(offLineFileName.substring(12,14)));*/

        long ecgFiletimeMillis = -1;

        HistoryRecord historyRecord = mUploadHistoryRecords.get(position);

        if (historyRecord!=null){
            ecgFiletimeMillis = historyRecord.getDatatime();
        }
        intent.putExtra(Constant.startTimeMillis,ecgFiletimeMillis);

        isAnalyed = false;

        ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
        chooseAlertDialogUtil.setAlertDialogTextHaveTitle(getResources().getString(R.string.choose_sport_state),getResources().getString(R.string.active),
                getResources().getString(R.string.rest));

        chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
            @Override
            public void onConfirmClick() {
                //动态
                isAnalyed = true;
                intent.putExtra(Constant.sportState,Constant.SPORTSTATE_ATHLETIC);
                intent.putExtra(Constant.accLocalFileName,accLocalFileName);
                Log.i(TAG,"accLocalFileName:"+accLocalFileName);
                startActivity(intent);
            }
        });
        chooseAlertDialogUtil.setOnCancelClickListener(new ChooseAlertDialogUtil.OnCancelClickListener() {
            @Override
            public void onCancelClick() {
                //静态
                isAnalyed = true;
                intent.putExtra(Constant.sportState,Constant.SPORTSTATE_STATIC);
                startActivity(intent);
            }
        });

        if (isAnalyed){
            analyCount++;

            //删除文件
            deleteOneFile(ecgLocalFileName);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    deleteOneFile(accLocalFileName);
                }
            }.start();
        }
    }

    private void sleepWaitGetFileList(){
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                MyUtil.hideDialog(getApplication());
                if (!isGetFileList){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyUtil.showToask(UploadOfflineFileActivity.this,"获取文件列表失败，请检查主机是否放在底座上，或拔掉电源重新连底座", Toast.LENGTH_LONG);
                        }
                    });
                }
            }
        }.start();
    }

    private void initData() {
        MyUtil.showDialog(getResources().getString(R.string.connected_check_offline_files),this);
        if (ConnectToWifiGudieActivity2.mSock!=null){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        mSocketWriter = ConnectToWifiGudieActivity2.mSock.getOutputStream();
                        inputStream = ConnectToWifiGudieActivity2.mSock.getInputStream();


                        getFileList();
                        sleepWaitGetFileList();

                        //byte[] bytes = new byte[2048];
                        final byte[] bytes = new byte[1024*10];
                        //byte[] bytes = new byte[512+14];
                        int length;
                        while (inputStream!=null && (length = inputStream.read(bytes))!=-1) {
                            Log.i(TAG, "length:" + length);
                            Log.i(TAG,"mIsStartUploadData:"+ mIsStartUploadData);

                            if (mIsStartUploadData) {
                                deviceOffLineFileUtil.stopTime();
                                final String toHexString = DeviceOffLineFileUtil.binaryToHexString(bytes, length);
                                //final String s = DeviceOffLineFileUtil.binaryToHexString(bytes, length,"");
                                Log.i(TAG, "收到文件上传数据:" + toHexString);
                                //开始传输数据了
                                //dealWithDeviceFileUpload(toHexString,length);
                                dealWithDeviceFileUpload(length, toHexString);
                                deviceOffLineFileUtil.startTime();
                            } else {
                                final String toHexString = DeviceOffLineFileUtil.binaryToHexString(bytes, length);
                                //final String s = DeviceOffLineFileUtil.binaryToHexString(bytes, length,"");
                                Log.i(TAG, "收到数据:" + toHexString);

                                if (isFileListDataSplited){
                                    dealWithDeviceFileList(toHexString);
                                }

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
                                    deviceOffLineFileUtil.startTime();
                                    mIsStartUploadData = true;
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
                        Log.i(TAG,"接受数据异常："+e);
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    /*//给设备发送16进制指令
    private void sendHexStringDeviceOrder(String deviceOrder) {
        byte[] bytes = DeviceOffLineFileUtil.hexStringToBytes(deviceOrder);
        if (mSocketWriter!=null){
            try {
                Thread.sleep(20);
                mSocketWriter.write(bytes);
                Log.i(TAG,"发送命令：" + deviceOrder);
                mSocketWriter.flush();
            } catch (IOException e) {
                Log.e(TAG,"e:"+e);
                e.printStackTrace();

                *//*Log.i(TAG,"异常重建socket");
                try {
                    Socket sock = new Socket(ConnectToWifiGudieActivity2.serverAddress, 8080);
                    mSocketWriter = sock.getOutputStream();
                    Log.i(TAG,"异常重建socket成功");
                    inputStream = sock.getInputStream();
                } catch (IOException eeee) {
                    e.printStackTrace();
                    Log.e(TAG,"异常重建socket失败:"+eeee);
                }*//*
                inputStream  = null;
                mSocketWriter = null;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/

    public void getFileList() {
        //MyUtil.showDialog("检查离线数据",this);
        sendHexStringDeviceOrder(DeviceOffLineFileUtil.readDeviceFileList);
    }

    //给设备发送16进制指令
    private void sendHexStringDeviceOrder(String hexDeviceOrderString) {
        Log.i(TAG,"发送16进制指令：" + hexDeviceOrderString);
        byte[] bytes = DeviceOffLineFileUtil.hexStringToBytes(hexDeviceOrderString);
        sendOrder(bytes);
    }

    //给设备发送ASCII命令
    private void sendAsciiStringDeviceOrder(String asciiDeviceOrderString) {
        Log.i(TAG,"发送ASCII命令：" + asciiDeviceOrderString);
        byte[] bytes = asciiDeviceOrderString.getBytes();
        sendOrder(bytes);
    }

    private void sendOrder(final byte[] bytes){
        if (mSocketWriter !=null){
            try {
                deviceOffLineFileUtil.startTime();
                Thread.sleep(10);    //睡一会，不然发送后收不到数据
                mSocketWriter.write(bytes);
                mSocketWriter.flush();
                Log.i(TAG,"发送成功");
            } catch (IOException e) {
                Log.i(TAG,"发送命令异常："+e);
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                e.printStackTrace();
            }
        }
    }

    public void deleteOneFile(String fileName) {
        String startOrder = "FF060018";
        String deviceOrder = startOrder + DeviceOffLineFileUtil.stringToHexString(fileName) + DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum("FF 06 00 18",fileName)+"16";
        Log.i(TAG,"删除文件deviceOrder:"+deviceOrder);
        sendHexStringDeviceOrder(deviceOrder);
    }

    public void deleteAllFile(View view) {
        String deviceOrder  ="FF0700060C16";
        Log.i(TAG,"删除所有文件deviceOrder:"+deviceOrder);
        sendHexStringDeviceOrder(deviceOrder);
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

    boolean isFileListDataSplited ;   //文件列表是否被分割
    String fileListHexData = "";
    String currFileNameString="";

    //文件列表：
    private void dealWithDeviceFileList(String allHexString) {
        if (isFileListDataSplited){
            allHexString = fileListHexData +allHexString;
        }

        Log.i(TAG,"allHexString:"+allHexString);
        mOffLineFileNameList.clear();
        mListViewItemUolpadIndex = 0;
        mUploadFileIndex = 0;
        // 32 30 31 37 30 34 31 32 31 30 32 33 30 30 2E 65 63 67
        final String[] split = allHexString.split(" ");
        int fileLength = Integer.parseInt(split[4], 16);
        Log.i(TAG,"fileLength:"+fileLength);
        String[] fileList = new String[fileLength];
        /*
        * FF 83 00 07 00 89 16  第五个00表示文件长度为0
        * FF 83 00 07 FF 88 16  第五个FF表示读物出错
        * */
        if (split.length==7){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyUtil.hideDialog(UploadOfflineFileActivity.this);
                    if (split[4].equals("FF")){
                        MyUtil.showToask(UploadOfflineFileActivity.this,"文件解析出错，请重启主机试试");
                    }
                    else if (split[4].equals("00")){
                        MyUtil.showToask(UploadOfflineFileActivity.this,"无离线文件，不需要上传");
                    }
                }
            });
            return;
        }
        else  if (split.length<7){
            isFileListDataSplited = true;
            fileListHexData = allHexString;
            return;
        }
        else {
            isFileListDataSplited = false;
            fileListHexData = "";
        }

        isGetFileList = true;

        boolean isFirstFile = true;
        int pre_file_type = 0;
        int file_type_ecg = 1;
        int file_type_acc = 2;

        List<String> rightOrderFileList = new ArrayList<>();

        for (int i = 0; i < fileLength; i++) {
            String fileNameString ="";
            for (int j = 0; j < 18; j++) {
                fileNameString += DeviceOffLineFileUtil.hexStringToString(split[5+i*18+j]);
            }
            // fileNameString:20170412102300.ecg
            // fileNameString:2017 04 12 10 23 00.ecg
            Log.i(TAG,"fileNameString:"+fileNameString);

            boolean isAddToRightOrderFileList = false;

            if (isFirstFile){
                if(fileNameString.endsWith("ecg")){
                    /*fileList[i] = fileNameString;
                    mOffLineFileNameList.add(i,fileNameString);*/
                    isAddToRightOrderFileList = true;
                    isFirstFile = false;
                    pre_file_type = file_type_ecg;
                }
            }
            else {
                if (pre_file_type==file_type_ecg && fileNameString.endsWith("acc")){
                    isAddToRightOrderFileList = true;
                    pre_file_type = file_type_acc;
                }
                else if (pre_file_type==file_type_acc && fileNameString.endsWith("ecg")){
                    isAddToRightOrderFileList = true;
                    pre_file_type = file_type_ecg;
                }
            }

            if (isAddToRightOrderFileList){
                rightOrderFileList.add(fileNameString);
            }
        }

        /*if (mOffLineFileNameList.get(0).endsWith("acc")){
            mOffLineFileNameList.remove(0);
        }*/

        mUploadHistoryRecords.clear();

        Log.i(TAG,"正确文件顺序");
        for (String s:rightOrderFileList){
            Log.i(TAG,"s:"+s);
            if (s.endsWith(".ecg")){
                if (s.length()>=14){
                    //以ecg结尾的文件在离线文件中列出来
                    String datatime = s.substring(0,4)+"-"+s.substring(4,6)+"-"+s.substring(6,8)+" "+
                            s.substring(8,10)+":"+s.substring(10,12)+":"+s.substring(12,14);
            /*if (mUploadHistoryRecords.size()>0){
                if (mUploadHistoryRecords.get(mUploadHistoryRecords.size()-1).equals(datatime)){
                    return;
                }
            }*/     int year = Integer.parseInt(s.substring(0,4));
                    int mouth = Integer.parseInt(s.substring(4,6));
                    int date = Integer.parseInt(s.substring(6,8));
                    int hrs = Integer.parseInt(s.substring(8,10));
                    int min = Integer.parseInt(s.substring(10,12));
                    int sec = Integer.parseInt(s.substring(12,14));

                    Date date1 = new Date(year-1900, mouth-1, date, hrs, min, sec);
                    mUploadHistoryRecords.add(new HistoryRecord("0",date1.getTime(),-1,HistoryRecord.analysisState_noAnalysised));
                }

            }
        }

        mOffLineFileNameList.clear();  //在点右上角重试后，获取到新的列表后先清空之前的列表

        mOffLineFileNameList.addAll(rightOrderFileList);

        Log.i(TAG,"mOffLineFileNameList.size():"+mOffLineFileNameList.size());

        /*String test ="";
        for (int i = 0; i < fileLength; i++) {
            test += fileList[i]+"\n";
        }*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyUtil.hideDialog(UploadOfflineFileActivity.this);
                lv_history_upload.setVisibility(View.GONE);
                uploadHistoryRecordAdapter.notifyDataSetChanged();
                lv_history_upload.setVisibility(View.VISIBLE);
                MyUtil.showToask(UploadOfflineFileActivity.this,"开始传输，次过程可能需要几分钟时间，请耐心等待", Toast.LENGTH_LONG);
            }
        });

        if (mOffLineFileNameList.size()>0){
            String fileName = mOffLineFileNameList.get(0);
            mCurrentUploadFileName = fileName;

            //String fileName = "20170413172800.ecg";
            String startOrder = "FF040018";
            String deviceOrder = startOrder + DeviceOffLineFileUtil.stringToHexString(fileName) + DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum("FF 04 00 18",fileName)+"16";
            Log.i(TAG,"deviceOrder:"+deviceOrder);
            isSendReadLengthOrder = true;
            mIsStartUploadData = false;


            sendHexStringDeviceOrder(deviceOrder);

        }

        for (String s:mOffLineFileNameList){
            if (s.endsWith("ecg")){
                mEcgOffLineFileNameList.add(s);
            }
            else if (s.endsWith("acc")){
                mAccOffLineFileNameList.add(s);
            }
        }
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



    List<Integer> mEmptyIndexItemArray = new ArrayList<>();
    boolean isFileDataTooLittleDelete;

    //文件长度
    private void dealWithDeviceFileLength(String allHexString) {
        //FF 84 00 0A 00 00 02 00 8F 16
        String[] split = allHexString.split(" ");
        String hexLength = split[4]+split[5]+split[6]+split[7];
        final int fileLengthInt = Integer.parseInt(hexLength, 16);
        Log.i(TAG,"fileLengthInt:"+fileLengthInt);

        //servic_info.setText("fileLengthInt:"+fileLengthInt);
        mCurUploadFilePackageIndex = 0;

        if (fileLengthInt<100){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv_history_alstate = (TextView) lv_history_upload.getChildAt(mUploadFileIndex/2).findViewById(R.id.tv_history_alstate);
                    tv_history_alstate.setText("数据不足");
                    tv_history_alstate.setTextColor(Color.parseColor("#FF7F24"));
                    lv_history_upload.getChildAt(mUploadFileIndex/2).setClickable(false);
                    mEmptyIndexItemArray.add(mUploadFileIndex/2);

                    mUploadFileIndex++;
                    mListViewItemUolpadIndex++;

                    sendReadNextFileOrder();
                    isFileDataTooLittleDelete = true;
                }
            });
            //deleteOneFile(mCurrentUploadFileName);
            return;
        }else {
            isFileDataTooLittleDelete  = false;
        }

        if (fileLengthInt>=mOneUploadMaxByte){
            mAllFileCount = fileLengthInt / mOneUploadMaxByte; // 需要传的次数
            mFileLastRemainder = fileLengthInt % mOneUploadMaxByte;  //余数，最后一次需要的传的次数
            Log.i(TAG,"需要传的次数 mAllFileCount:"+mAllFileCount);
            Log.i(TAG,"余数 mFileLastRemainder:"+mFileLastRemainder);

            String offsetHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mOneUploadMaxByte* mCurUploadFilePackageIndex);
            //String offsetHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, offsetTest);
            String fileHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mOneUploadMaxByte);
            String startOrder = "FF05000e"+offsetHexLenght+fileHexLenght;
            String deviceOrder = startOrder+DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum(startOrder)+"16";
            Log.i(TAG, mCurUploadFilePackageIndex +"分段上传deviceOrder:"+deviceOrder);
            sendHexStringDeviceOrder(deviceOrder);
        }
        else {
            mAllFileCount = 0; // 需要传的次数
            mFileLastRemainder = fileLengthInt % mOneUploadMaxByte;  //余数，最后一次需要的传的次数

            String offsetHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, 0);
            String fileHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, fileLengthInt);
            String startOrder = "FF05000e"+offsetHexLenght+fileHexLenght;
            String deviceOrder = startOrder+DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum(startOrder)+"16";
            Log.i(TAG,"一次上传deviceOrder:"+deviceOrder);
            sendHexStringDeviceOrder(deviceOrder);

        }
    }
    //boolean mIsStartUploadData

    //文件上传
    private void dealWithDeviceFileUpload(int length, final String toHexString) {
        noRepronseCount=0;
        if (startTimeMillis==0){
            startTimeMillis = System.currentTimeMillis();
        }

        if (mCurUploadFilePackageIndex <mAllFileCount){
            //前几次整数上传
            onePackageReadLength += length;
            onePackageDataHexString += toHexString;
            Log.i(TAG,"分包 当前收到总长度length:"+onePackageReadLength);
            if (onePackageReadLength==mOneUploadMaxByte+16*14){
                Log.i(TAG,"当前包上传成功:"+ mCurUploadFilePackageIndex);

                float progress ;
                //有可能整个文件的包长为8192，刚好一次传完，则mAllFileCount=1，mFileLastRemainder=0；
                if (mFileLastRemainder!=0){
                    progress = (float)(mCurUploadFilePackageIndex +1)/(mAllFileCount+1);
                }
                else {
                    progress = (float)(mCurUploadFilePackageIndex +1)/(mAllFileCount);
                }

                ProgressBar pb_item_progress = (ProgressBar) lv_history_upload.getChildAt(mUploadFileIndex/2).findViewById(R.id.pb_item_progress);
                //ProgressBar pb_item_progress = HistoryRecordAdapter.progressBarList.get(mUploadFileIndex);
                //pb_item_progress.setProgress((int)(progress*100));

                if (mCurrListViewItemProgress>=0.5f){
                    mCurrListViewItemProgress = 0.5f+progress*0.5f;
                }
                else {
                    mCurrListViewItemProgress = progress*0.5f;
                }
                pb_item_progress.setProgress((int) (mCurrListViewItemProgress*100));
                Log.i(TAG,"设置进度:"+(int) (mCurrListViewItemProgress*100));


                //writeEcgDataToBinaryFile(onePackageDataHexString,16);
                DeviceOffLineFileUtil.addEcgDataToList(onePackageDataHexString,mAllData);
                //onePackageData.clear();
                onePackageReadLength = 0;
                onePackageDataHexString = "";

                if (mCurUploadFilePackageIndex ==mAllFileCount-1 && mFileLastRemainder==0){
                    //上传完成
                    uploadCurrentFileSuccess();
                    if ((int) (mCurrListViewItemProgress*100)>90){
                        mListViewItemUolpadIndex++;
                        mCurrListViewItemProgress = 0;
                    }
                }
                else {
                    mCurUploadFilePackageIndex++;
                    uploadNextPackageData();
                }
            }
        }
        else if (mCurUploadFilePackageIndex ==mAllFileCount){

            //int allHexlength = Integer.parseInt(allHexString, 16);

            //List<Integer> geIntEcgaArr = EcgAccDataUtil.geIntEcgaArrList(allHexString, " ", 12 ,uploadLengthInt);
            //onePackageData.addAll(geIntEcgaArr);
            onePackageReadLength += length;
            onePackageDataHexString += toHexString;
            Log.i(TAG,"余数 当前包收到总长度length:"+onePackageReadLength);
            //Log.i(TAG,"mFileLastRemainder:"+mFileLastRemainder);
            if (onePackageReadLength == mFileLastRemainder+(int) Math.ceil(mFileLastRemainder/512.0)*14){
                Log.i(TAG,"余数上传成功:"+ mCurUploadFilePackageIndex);

                mIsCurrFileRemainderUploadOk = true;
                /*if (mAllFileCount>0){
                    float progress = (float)(mCurUploadFilePackageIndex+1)/mAllFileCount;
                    if (mCurrListViewItemProgress>0){
                        progress += mCurrListViewItemProgress;
                    }
                    ProgressBar pb_item_progress = (ProgressBar) lv_history_upload.getChildAt(mUploadFileIndex/2).findViewById(R.id.pb_item_progress);
                    //ProgressBar pb_item_progress = HistoryRecordAdapter.progressBarList.get(mUploadFileIndex);
                    pb_item_progress.setProgress((int)(progress*100));
                    Log.i(TAG,"设置进度:"+progress);
                }
                else {
                    ProgressBar pb_item_progress = (ProgressBar) lv_history_upload.getChildAt(mUploadFileIndex).findViewById(R.id.pb_item_progress);
                    //ProgressBar pb_item_progress = HistoryRecordAdapter.progressBarList.get(mUploadFileIndex);
                    if (mCurrListViewItemProgress>0){
                        mCurrListViewItemProgress = 1;
                        pb_item_progress.setProgress(100);
                        Log.i(TAG,"设置进度:"+100);
                    }
                    else {
                        mCurrListViewItemProgress = 0.5f;
                        pb_item_progress.setProgress(50);
                        Log.i(TAG,"设置进度:"+50);
                    }

                }*/

                ProgressBar pb_item_progress = null;
                View childAt = lv_history_upload.getChildAt(mUploadFileIndex / 2);
                if (childAt!=null){
                    pb_item_progress = (ProgressBar) childAt.findViewById(R.id.pb_item_progress);
                }

                //ProgressBar pb_item_progress = HistoryRecordAdapter.progressBarList.get(mUploadFileIndex);
                if (mCurrListViewItemProgress>=0.5f){
                    mCurrListViewItemProgress = 0;
                    if (pb_item_progress!=null){
                        pb_item_progress.setProgress(100);
                    }
                    Log.i(TAG,"设置进度:"+100);
                }
                else {
                    mCurrListViewItemProgress = 0.5f;
                    if (pb_item_progress!=null){
                        pb_item_progress.setProgress(50);
                    }
                    Log.i(TAG,"设置进度:"+50);
                }

                //则文件数据传完，isStartUploadData置为false
                DeviceOffLineFileUtil.addRemainderEcgDataToList(onePackageDataHexString,onePackageReadLength, mAllData);

                onePackageReadLength = 0;
                onePackageDataHexString = "";
                //writeEcgDataToBinaryFile(onePackageData);
                //onePackageData.clear();
                uploadCurrentFileSuccess();

                //if (pb_item_progress.getProgress()==100){
                if (mCurrListViewItemProgress == 0){
                    mListViewItemUolpadIndex++;
                }
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

        mIsStartUploadData = false;
        //deleteOneFile(mCurrentUploadFileName);
        isSendReadLengthOrder = false;

        final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ mCurrentUploadFileName;
        final boolean isWriteSuccess = DeviceOffLineFileUtil.writeEcgDataToBinaryFile(mAllData, filePath);
        Log.i(TAG,"写入文件isWriteSuccess:"+isWriteSuccess+ "  filePath:"+filePath);
        Log.i(TAG,"mListViewItemUolpadIndex:"+mListViewItemUolpadIndex);

        deviceOffLineFileUtil.stopTime();
        mAllData.clear();


        if (isWriteSuccess){
            if (filePath.endsWith("ecg")){
                mEcgLocalOffLineFileNameList.put(mListViewItemUolpadIndex+"",filePath);
            }
            else {
                mAccLocalOffLineFileNameList.put(mListViewItemUolpadIndex+"",filePath);
            }
            mLocalFileNameList.add(filePath);
        }


        final TextView tv_history_alstate;

        if (mCurrentUploadFileName.endsWith("acc")){
            tv_history_alstate = (TextView) lv_history_upload.getChildAt(mUploadFileIndex/2).findViewById(R.id.tv_history_alstate);
            //TextView tv_history_alstate = HistoryRecordAdapter.textViewList.get(mUploadFileIndex);

            if (tv_history_alstate!=null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_history_alstate.setText("上传完成（点击分析）");
                        tv_history_alstate.setTextColor(Color.parseColor("#333333"));
                        Log.i(TAG,"设置状态:上传完成（点击分析）");
                    }
                });
            }
        }
        sendReadNextFileOrder();
    }

    boolean isSendReadLengthOrder;  //是否已经发送了读取文件指令
    boolean isSendUploadFileOrder;

    //发送读取下一个文件的指令
    private void sendReadNextFileOrder(){
        mUploadFileIndex++; //上传下一个文件
        mCurUploadFilePackageIndex = -1;  //将mUploadFileCountIndex标识为-1，标识还没有读取到这个文件的长度

        if (mOffLineFileNameList.size()>0 && mUploadFileIndex<mOffLineFileNameList.size()){
            String fileName = mOffLineFileNameList.get(mUploadFileIndex);
            mCurrentUploadFileName = fileName;

            Log.i(TAG,"读取长度currentUploadFileName:"+ mCurrentUploadFileName);
            //String fileName = "20170413172800.ecg";
            mIsCurrFileRemainderUploadOk = false;
            mIsStartUploadData = false;
            String startOrder = "FF040018";
            String deviceOrder = startOrder + DeviceOffLineFileUtil.stringToHexString(fileName) + DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum("FF 04 00 18",fileName)+"16";
            Log.i(TAG,"读取deviceOrder:"+deviceOrder);
            Log.i(TAG,"mIsStartUploadData:"+ mIsStartUploadData);
            isSendReadLengthOrder = true;

            sendHexStringDeviceOrder(deviceOrder);
        }
        else if (mOffLineFileNameList.size()>0 && mUploadFileIndex==mOffLineFileNameList.size()){
            Log.i(TAG,"所有文件上传完成!");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog alertDialog = new AlertDialog.Builder(UploadOfflineFileActivity.this)
                            .setTitle("所有文件上传完成，请点击文件进行分析!")
                            .setPositiveButton(getResources().getString(R.string.exit_confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
            });
        }

    }

    int mUploadFileIndex;  //需要上传的所有文件的索引（ecg+acc）
    float mCurrListViewItemProgress;
    boolean mIsCurrFileRemainderUploadOk;
    int mListViewItemUolpadIndex = 0;  //listView中正在上传的文件索引

    //判断是否需要重传
    private void judgeRequireRetransmission() {
        Log.i(TAG,"isSendReadLengthOrder:"+isSendReadLengthOrder);
        if (isSendReadLengthOrder ){
            Log.i(TAG,"mCurUploadFilePackageIndex:"+ mCurUploadFilePackageIndex +"   mAllFileCount:"+mAllFileCount);
            if (mCurUploadFilePackageIndex ==mAllFileCount ){
                if (mFileLastRemainder>0 && !mIsCurrFileRemainderUploadOk){
                    requireRetransmission();
                }
                else {
                    //文件长传完成
                    deviceOffLineFileUtil.stopTime();
                    Log.i(TAG,"DeviceOffLineFileUtil.stopTime();");
                    //DeviceOffLineFileUtil.destoryTime();
                }
            }
            else {
                requireRetransmission();
            }
        }
    }

    int noRepronseCount;

    //请求重传
    private void requireRetransmission() {
        /*Log.i(TAG,"请求重传requireRetransmission:");
        Log.i(TAG,"noRepronseCount:"+noRepronseCount);
        if (noRepronseCount==3){
            mSocketWriter = null;
            Log.i(TAG,"重建socket");
            try {
                Socket sock = new Socket(ConnectToWifiGudieActivity2.serverAddress, 8080);
                ConnectToWifiGudieActivity2.mSock = sock;
                mSocketWriter = sock.getOutputStream();
                Log.i(TAG,"重建socket成功");
                inputStream = sock.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"重建socket失败:"+e);
            }
            noRepronseCount=-1;

            mIsStartUploadData = false;
            requireRetransmissionCount++;
            onePackageData.clear();
            onePackageReadLength = 0;
            uploadNextPackageData();
            noRepronseCount++;
        }
        else {
            mIsStartUploadData = false;
            requireRetransmissionCount++;
            onePackageData.clear();
            onePackageReadLength = 0;
            uploadNextPackageData();
            noRepronseCount++;
        }*/

        Log.i(TAG,"请求重传requireRetransmission:");
        Log.i(TAG,"noRepronseCount:"+noRepronseCount);

        mIsStartUploadData = false;
        requireRetransmissionCount++;
        onePackageData.clear();
        onePackageReadLength = 0;
        uploadNextPackageData();
        noRepronseCount++;
    }

    //当前包传输成功，进行下一个包传输
    private void uploadNextPackageData(){
        if (mCurUploadFilePackageIndex !=-1){  //mCurUploadFilePackageIndex!=-1表示还没有读取文件长度
            if (mCurUploadFilePackageIndex <mAllFileCount){
                String offsetHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mOneUploadMaxByte* mCurUploadFilePackageIndex);
                String fileHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mOneUploadMaxByte);
                String startOrder = "FF05000e"+offsetHexLenght+fileHexLenght;
                String deviceOrder = startOrder+DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum(startOrder)+"16";
                Log.i(TAG, mCurUploadFilePackageIndex +"分段上传deviceOrder:"+deviceOrder);
                sendHexStringDeviceOrder(deviceOrder);
            }
            else
            if (mFileLastRemainder>0){
                String offsetHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mOneUploadMaxByte* mCurUploadFilePackageIndex);
                String fileHexLenght = DeviceOffLineFileUtil.getFormatHexFileLenght(8, mFileLastRemainder);
                String startOrder = "FF05000e"+offsetHexLenght+fileHexLenght;
                String deviceOrder = startOrder+DeviceOffLineFileUtil.readDeviceSpecialFileBeforeAddSum(startOrder)+"16";
                Log.i(TAG,"余数上传deviceOrder:"+deviceOrder);
                sendHexStringDeviceOrder(deviceOrder);

                //mFileLastRemainder = 0;
            }
            else {
                //没有余数，刚好整除，则文件数据传完，isStartUploadData置为false

            }
        }
        else {
            mUploadFileIndex--;
            sendReadNextFileOrder();
        }


        mIsStartUploadData = false;
    }


    private void dealWithDeviceDeleteFile(String allHexString) {
        String[] split = allHexString.split(" ");
        final int deleteState = Integer.parseInt(split[4], 16);
        /*
        * 0：表示删除成功失败；
            1：没有该文件；
            2：删除成功失败。
            */

       /* if (isFileDataTooLittleDelete){
            sendReadNextFileOrder();
        }*/

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

    public void getVersion(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendHexStringDeviceOrder(DeviceOffLineFileUtil.readDeviceVersion);
            }
        }){}.start();
    }

    public void readLength(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendHexStringDeviceOrder("FF04001832303137303831373135313332392e6563674716");
            }
        }){}.start();
    }

    public void uoloadFile(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendHexStringDeviceOrder("FF05000e0000000000000a001c16");
            }
        }){}.start();
    }
}

