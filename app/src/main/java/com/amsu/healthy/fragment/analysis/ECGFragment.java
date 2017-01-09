package com.amsu.healthy.fragment.analysis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.EcgView;
import com.amsu.healthy.view.PathView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private String ecgDatatext = "";;
    private EcgView pv_ecg_path;

    private List<Integer> datas = new ArrayList<>();

    private Queue<Integer> data0Q = new LinkedList<Integer>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_ecg, null);

        initView();
        return inflate;

    }

    private void initView() {
        pv_ecg_path = (EcgView) inflate.findViewById(R.id.pv_ecg_path);

        String cacheFileName = MyUtil.getStringValueFromSP("cacheFileName");
        if (!cacheFileName.equals("")){
            try {
                if (fileInputStream==null){
                    fileInputStream = new FileInputStream(cacheFileName);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
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
        }
    }

    //开始画线
    private void startDrawSimulator(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(EcgView.isRunning){
                    if(data0Q.size() > 0){
                        pv_ecg_path.addEcgCacheData(data0Q.poll());
                    }
                }
            }
        }, 0, 2);
    }
}
