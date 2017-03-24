package com.amsu.healthy.fragment.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.HistoryRecordActivity;
import com.amsu.healthy.activity.MyReportActivity;
import com.amsu.healthy.activity.RateAnalysisActivity;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.EcgFilterUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.EcgView;
import com.amsu.healthy.view.PathView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class ECGFragment extends Fragment {

    private static final String TAG = "ECGFragment";
    private View inflate;
    private FileInputStream fileInputStream;
    private EcgView pv_ecg_path;

    private List<Integer> datas = new ArrayList<>();

    private Queue<Integer> data0Q = new LinkedList<Integer>();
    private Timer mDrawWareTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_ecg, null);

        initView();
        return inflate;

    }

    private void initView() {
        pv_ecg_path = (EcgView) inflate.findViewById(R.id.pv_ecg_path);
        Button bt_hrv_history = (Button) inflate.findViewById(R.id.bt_hrv_history);
        Button bt_hrv_myreport = (Button) inflate.findViewById(R.id.bt_hrv_myreport);

        bt_hrv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HistoryRecordActivity.class));
            }
        });
        bt_hrv_myreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyReportActivity.class));
            }
        });

        UploadRecord mUploadRecord = RateAnalysisActivity.mUploadRecord;
        if (mUploadRecord!=null){
            Log.i(TAG,"mUploadRecord:"+mUploadRecord.toString());
            Log.i(TAG,"EC :"+mUploadRecord.EC);
            long timestamp =  Long.valueOf(mUploadRecord.timestamp);
            String eCGFilePath = MyUtil.generateECGFilePath(getActivity(), timestamp);
            //String cacheFileName = MyUtil.getStringValueFromSP("cacheFileName");
            //String cacheFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20170220210301.ecg";  //测试
            if (!eCGFilePath.equals("")){
                try {
                    if (fileInputStream==null){
                        File file = new File(eCGFilePath);
                        if (!file.exists()){
                            //文件不存在的话说明  1、本地文件已经被删除了  2、第一次从历史记录获取文件，需用Base64生成文件
                            file = MyUtil.base64ToFile(mUploadRecord.EC, eCGFilePath);
                        }
                        if (file.exists()){
                            fileInputStream = new FileInputStream(eCGFilePath);
                            DataInputStream dataInputStream = new DataInputStream(fileInputStream); //读取二进制文件
                            byte[] bytes = new byte[2];
                            ByteBuffer buffer=  ByteBuffer.wrap(bytes);
                            byte b;
                            try {
                                while ((b = (byte) dataInputStream.read()) != -1 ){
                                    bytes[1] =b;
                                    bytes[0] =(byte)dataInputStream.read();
                                    short readCsharpInt = buffer.getShort();
                                    buffer.clear();
                                    //Log.i(TAG,"readByte:"+readByte);
                                    //滤波处理
                                    int temp = EcgFilterUtil.miniEcgFilterLp(readCsharpInt, 0);
                                    temp = EcgFilterUtil.miniEcgFilterHp(temp, 0);
                                    data0Q.add(temp);
                                }

                /*for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    int readByte = 0;
                    readByte = dataInputStream.readInt();
                    Log.i(TAG,"readByte:"+readByte);
                    //滤波处理
                    int temp = EcgFilterUtil.miniEcgFilterLp(readByte, 0);
                    temp = EcgFilterUtil.miniEcgFilterHp(temp, 0);
                    readByte = temp;
                    data0Q.add(readByte);
                }*/
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        /*String cacheFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/20170220210301.ecg";  //测试
        if (!cacheFileName.equals("")){
            try {
                if (fileInputStream==null){
                    File file = new File(cacheFileName);
                    if (file.exists()){
                        fileInputStream = new FileInputStream(cacheFileName);
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        String ecgDatatext = "";
        if (fileInputStream!=null){
            byte [] mybyte = new byte[1024];
            int length=0;
            try {
                while (true) {
                    length = fileInputStream.read(mybyte,0,mybyte.length);
                    Log.i(TAG,"length:"+length);
                    if (length!=-1) {
                        String s = new String(mybyte,0,length);
                        ecgDatatext +=s;
                    }else {
                        break;
                    }
                }
                Log.i(TAG,"ecgDatatext:"+ecgDatatext);
                if (!ecgDatatext.equals("")){
                    //此处的数据是滤波之后的，无需再进行滤波
                    String[] allGrounpData = ecgDatatext.split(",");
                    for (int i=0;i<allGrounpData.length;i++) {
                        datas.add(Integer.parseInt(allGrounpData[i]));
                    }
                    data0Q.addAll(datas);
                    startDrawSimulator();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }


    @Override
    public void onResume() {
        super.onResume();


        if (fileInputStream!=null){
            Log.i(TAG,"over:开始画图");
            startDrawSimulator();
            /*byte [] mybyte = new byte[1024];
            int length=0;
            try {
                while (true) {
                    dataInputStream.read
                    length = dataInputStream.read(mybyte,0,mybyte.length);
                    Log.i(TAG,"length:"+length);
                    if (length!=-1) {
                        String s = new String(mybyte,0,length);
                        ecgDatatext +=s;
                    }else {
                        break;
                    }
                }
                Log.i(TAG,"ecgDatatext:"+ecgDatatext);
                if (!ecgDatatext.equals("")){
                    //此处的数据是滤波之后的，无需再进行滤波
                    String[] allGrounpData = ecgDatatext.split(",");
                    for (int i=0;i<allGrounpData.length;i++) {
                        datas.add(Integer.parseInt(allGrounpData[i]));
                    }
                    data0Q.addAll(datas);
                    startDrawSimulator();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    //开始画线
    private void startDrawSimulator(){
        Log.i(TAG,"data0Q:"+data0Q.size());
        mDrawWareTimer = new Timer();
        mDrawWareTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(EcgView.isRunning){
                    if(data0Q.size() > 0){
                        Integer poll = data0Q.poll();
                        Log.i(TAG,"poll:"+poll);
                        pv_ecg_path.addEcgCacheData(poll);
                    }
                }
            }
        }, 0, 2);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDrawWareTimer!=null){
            mDrawWareTimer.cancel();
            mDrawWareTimer = null;
        }
    }
}
