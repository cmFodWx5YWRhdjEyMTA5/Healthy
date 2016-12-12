package com.amsu.healthy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
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

public class ECGFragment extends Fragment {

    private View inflate;
    private FileInputStream fileInputStream;
    private String ecgDatatext = "";;
    private PathView pv_ecg_path;

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
        pv_ecg_path = (PathView) inflate.findViewById(R.id.pv_ecg_path);

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
                    System.out.println("length:"+length);
                    if (length!=-1) {
                        String s = new String(mybyte,0,length);
                        ecgDatatext +=s;
                        System.out.println(s);
                    }else {
                        break;
                    }
                }

                if (!ecgDatatext.equals("")){
                    String[] allGrounpData = ecgDatatext.split(",");
                    for (int i=0;i<allGrounpData.length;i++){
                        String[] oneGroupData = allGrounpData[i].split(",");
                        final int arr[] = new int[oneGroupData.length];
                        for (int j=0;j<oneGroupData.length;j++){
                            arr[j] = Integer.parseInt(oneGroupData[j]);
                        }

                        //给心电图界面传递数据
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pv_ecg_path.drawLine(arr);
                            }
                        });
                        try {
                            Thread.sleep(1000/15);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        new Thread(){
            @Override
            public void run() {
                if (!ecgDatatext.equals("")){
                    String[] allGrounpData = ecgDatatext.split(",");
                    for (int i=0;i<allGrounpData.length;i++){
                        String[] oneGroupData = allGrounpData[i].split(",");
                        final int arr[] = new int[oneGroupData.length];
                        for (int j=0;j<oneGroupData.length;j++){
                            datas.add(Integer.parseInt(oneGroupData[j]));
                        }
                        data0Q.addAll(datas);

                        //给心电图界面传递数据
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pv_ecg_path.drawLine(arr);
                            }
                        });
                        try {
                            Thread.sleep(1000/15);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }.start();

    }

    //画整个文件
    private void loadDatas(){
        try{
            String data0 = "";
            InputStream in = getResources().openRawResource(R.raw.ecgdata);
            int length = in.available();
            byte [] buffer = new byte[length];
            in.read(buffer);
            data0 = new String(buffer);
            in.close();
            String[] data0s = data0.split(",");
            for(String str : data0s){
                datas.add(Integer.parseInt(str));
            }

            data0Q.addAll(datas);
        }catch (Exception e){}

    }
}
