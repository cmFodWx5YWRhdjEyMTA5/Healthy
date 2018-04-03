package com.amsu.wear.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.proxy.BleUtil;
import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.bleinteraction.utils.SharedPreferencesUtil;
import com.amsu.wear.R;
import com.amsu.wear.activity.SearchDevicehActivity;
import com.amsu.wear.adapter.DeviceAdapter;
import com.amsu.wear.util.LogUtil;
import com.amsu.wear.util.ToastUtil;
import com.amsu.wear.util.UserUtil;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceListFragment extends Fragment {


    private static final String TAG = DeviceListFragment.class.getSimpleName();
    private View inflate;
    private ListView lv_device_devicelist;
    private List<BleDevice> mBleDeviceList;
    private DeviceAdapter mDeviceAdapter;
    private BleDevice mBleDeviceFromSP;

    public DeviceListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_device_list, container, false);
        initView();
        initData();
        return inflate;
    }

    private void initView() {
        lv_device_devicelist = inflate.findViewById(R.id.lv_device_devicelist);
        View list_bottom = View.inflate(getContext(),R.layout.view_devicelist_bottom,null);
        lv_device_devicelist.addFooterView(list_bottom);

        mBleDeviceList = new ArrayList<>();

        mBleDeviceFromSP = SharedPreferencesUtil.getDeviceFromSP(BleConstant.sportType_Cloth);

        if (mBleDeviceFromSP!=null){
            mBleDeviceList.add(mBleDeviceFromSP);
        }

        mDeviceAdapter = new DeviceAdapter(getContext(),mBleDeviceList);
        lv_device_devicelist.setAdapter(mDeviceAdapter);

        lv_device_devicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position<mBleDeviceList.size()){
                    BleDevice bleDevice = mBleDeviceList.get(position);
                    //ToastUtil.showToask("bleDevice:"+bleDevice);
                    if (UserUtil.isLoginEd()){
                        bindDevice(bleDevice);
                    }
                    else {
                        ToastUtil.showToask("请先登陆");
                    }

                }else {
                    if (!BleUtil.checkBluetoothAndOpen(getActivity())){
                        return;
                    }

                    Intent intent = new Intent(getActivity(), SearchDevicehActivity.class);
                    startActivityForResult(intent,100);
                    //ToastUtil.showToask("添加");
                }
            }
        });

    }

    private void bindDevice(BleDevice bleDevice) {
        if (bleDevice.getDeviceType()==BleConstant.sportType_Cloth){
            if (bleDevice.getClothDeviceType() == BleConstant.clothDeviceType_secondGeneration_AMSU_BindByHardware){
                BleConnectionProxy.DeviceBindByHardWareType bindType = bleDevice.getBindType();
                if (bindType == BleConnectionProxy.DeviceBindByHardWareType.bindByWeiXin || bindType ==BleConnectionProxy.DeviceBindByHardWareType.bindByPhone){
                    //自己绑定
                    if (mBleDeviceFromSP!=null && bleDevice.getMac().equals(mBleDeviceFromSP.getMac())){
                        //当前点击的是储存在SP的,则调到详情
                        //dumpToDeviceDetail();
                        ToastUtil.showToask("不需重复添加");
                    }
                    else {
                        //这个设备是以前自己绑定过，现在点击可以切换设备
                        ToastUtil.showToask("设备已添加");
                        connectNewDevice(bleDevice);
                    }
                }
                else if (bindType ==BleConnectionProxy.DeviceBindByHardWareType.bindByOther){
                    //被别人绑定,提示用户解绑
                    ToastUtil.showToask("设备被别人绑定，请先关机，然后开机长按15秒");
                }
                else if (bindType ==BleConnectionProxy.DeviceBindByHardWareType.bindByNO){
                    if (mBleDeviceFromSP!=null && BleConnectionProxy.getInstance().ismIsConnectted() &&
                            BleConnectionProxy.getInstance().getmClothDeviceConnecedMac().equals(mBleDeviceFromSP.getMac())){
                        //dumpToDeviceDetail();
                        ToastUtil.showToask("不需重复添加");
                    }
                    else{
                        ToastUtil.showToask("设备已添加");
                        connectNewDevice(bleDevice);
                    }
                }
                else if (bindType ==BleConnectionProxy.DeviceBindByHardWareType.devideNOSupport){
                    ToastUtil.showToask("主机硬件不支持旧版主机，请联系管理员");
                }
                else{
                    ToastUtil.showToask("绑定类型错误");
                }
            }
            else {

            }
        }
    }

    //连接新得设备，如果是绑定过的话直接连接，不是的话需要绑定  （isNeedBindDevice是否绑定）
    private void connectNewDevice(BleDevice bleDevice) {
        if (BleConnectionProxy.getInstance().ismIsConnectted()) {
            BleConnectionProxy.getInstance().disconnect(BleConnectionProxy.getInstance().getmClothDeviceConnecedMac());  //断开其他连接的设备
        }

        BleConnectionProxy.getInstance().connect(bleDevice.getMac());
        BleConnectionProxy.getInstance().deviceBindSuccessAndSaveToLocalSP(bleDevice);
    }

    private void initData() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==RESULT_OK) {
            List<BleDevice> searchBleDeviceLists = data.getParcelableArrayListExtra("searchBleDeviceList");
            mBleDeviceList.clear();
            if (mBleDeviceFromSP!=null){
                mBleDeviceList.add(mBleDeviceFromSP);
            }
            mBleDeviceList.addAll(searchBleDeviceLists);
            LogUtil.i(TAG,"mBleDeviceList:"+mBleDeviceList);
            mDeviceAdapter.notifyDataSetChanged();
        }
    }
}

