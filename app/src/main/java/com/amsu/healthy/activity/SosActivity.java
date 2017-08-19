package com.amsu.healthy.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.QQListView;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class SosActivity extends BaseActivity {
    private static final String TAG = "SosActivity";
    private List<SosNumber> sosNumberList;
    private SosListAdapter sosListAdapter;
    private EditText ed_sos_sosinfo;
    String mSosinfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        //MyUtil.putStringValueFromSP("sosNumberList","");

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.emergency_help));
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsNeedUploadSosInfo();
            }
        });


        final QQListView lv_sos_list = (QQListView) findViewById(R.id.lv_sos_list);


        View footView = View.inflate(this, R.layout.view_foot_add_contant, null);
        lv_sos_list.addFooterView(footView);

        RelativeLayout rl_sos_add = (RelativeLayout) footView.findViewById(R.id.rl_sos_add);
        ed_sos_sosinfo = (EditText) footView.findViewById(R.id.ed_sos_sosinfo);

        rl_sos_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI), 0);

            }
        });

        sosNumberList = new ArrayList<>();

        sosListAdapter = new SosListAdapter();
        lv_sos_list.setAdapter(sosListAdapter);

        lv_sos_list.setDelButtonClickListener(new QQListView.DelButtonClickListener() {
            @Override
            public void clickHappend(int position) {
                deleteSosContact(position);
            }
        });
    }

    private void deleteSosContact(final int position) {
        MyUtil.showDialog(getResources().getString(R.string.deleting),this);
        SosNumber sosNumber = sosNumberList.get(position);
        HttpUtils httpUtils = new HttpUtils();
        final RequestParams params = new RequestParams();
        params.addBodyParameter("id",sosNumber.id);
        MyUtil.addCookieForHttp(params);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.deleteSosContact, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(SosActivity.this);
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    sosNumberList.remove(position);
                    sosListAdapter.notifyDataSetChanged();
                    MyUtil.putSosNumberList(sosNumberList);
                    MyUtil.showToask(SosActivity.this,getResources().getString(R.string.delete_successfully));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(SosActivity.this);
                Log.i(TAG,"上传onFailure==s:"+s);
                MyUtil.showToask(SosActivity.this,getResources().getString(R.string.delete_failed));
            }
        });
    }

    private boolean checkIsNeedUploadSosInfo() {
        String sosinfo = ed_sos_sosinfo.getText().toString();
        if (MyUtil.isEmpty(sosinfo) || (sosNumberList!=null && sosNumberList.size()==0)){
            //MyUtil.showToask(SosActivity.this,"请输入求助信息");
            ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(this);
            chooseAlertDialogUtil.setAlertDialogText(getResources().getString(R.string.no_help_information_set),getResources().getString(R.string.exit_confirm),getResources().getString(R.string.exit_cancel));
            chooseAlertDialogUtil.setOnConfirmClickListener(new ChooseAlertDialogUtil.OnConfirmClickListener() {
                @Override
                public void onConfirmClick() {
                    finish();
                }
            });
            //return false;
        }
        else {
            if (!sosinfo.equals(mSosinfo)){
                addSosMessageToServer(sosinfo);
            }
            finish();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            return checkIsNeedUploadSosInfo();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void addSosMessageToServer(final String sosinfo) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("message",sosinfo);
        MyUtil.addCookieForHttp(params);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.setSosMessage, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(SosActivity.this);
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    MyUtil.putStringValueFromSP(Constant.sosinfo,sosinfo);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(SosActivity.this);
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }


    private void initData() {
        String stringValueFromSP = MyUtil.getStringValueFromSP(Constant.sosinfo);
        if(!MyUtil.isEmpty(stringValueFromSP)){
            ed_sos_sosinfo.setText(stringValueFromSP);
            mSosinfo = stringValueFromSP;
        }
        List<SosNumber> sosNumbers = MyUtil.getSosNumberList();
        if (sosNumbers!=null && sosNumbers.size()>0){
            //本地有
            sosNumberList = sosNumbers;
            sosListAdapter.notifyDataSetChanged();
        }
        else {
            //请求网络
            MyUtil.showDialog("正在查询",this);
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();
            MyUtil.addCookieForHttp(params);
            httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getAllContacts, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    MyUtil.hideDialog(SosActivity.this);
                    String result = responseInfo.result;
                    Log.i(TAG,"上传onSuccess==result:"+result);
                    Gson gson = new Gson();
                    JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                    Log.i(TAG,"jsonBase:"+jsonBase);
                    if (jsonBase.getRet()==0){
                        SosInfo fromJson = gson.fromJson(result, SosInfo.class);
                        List<SosNumber> contacts = fromJson.errDesc.contacts;
                        String message = fromJson.errDesc.message;
                        Log.i(TAG,"contantsInfo:"+contacts.toString());

                        sosNumberList.addAll(contacts);
                        sosListAdapter.notifyDataSetChanged();
                        ed_sos_sosinfo.setText(message);
                        MyUtil.putSosNumberList(sosNumberList);
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    MyUtil.hideDialog(SosActivity.this);
                    Log.i(TAG,"上传onFailure==s:"+s);
                }
            });
        }



    }


    public void addSosNumber(View view) {
        /*View inflate = View.inflate(this, R.layout.view_dialog_input, null);
        final TextView tv_sos_name = (TextView) inflate.findViewById(R.id.tv_sos_name);
        final TextView tv_sos_number = (TextView) inflate.findViewById(R.id.tv_sos_number);
        TextView bt_choose_cancel = (TextView) inflate.findViewById(R.id.bt_choose_cancel);
        TextView bt_choose_ok = (TextView) inflate.findViewById(R.id.bt_choose_ok);


        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.myCorDialog).setView(inflate).create();
        alertDialog.show();
        float width = getResources().getDimension(R.dimen.x800);
        float height = getResources().getDimension(R.dimen.x700);

        Window window = alertDialog.getWindow();
        window.setLayout(Float.valueOf(width).intValue(),Float.valueOf(height).intValue());

        bt_choose_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = tv_sos_name.getText().toString();
                String number = tv_sos_number.getText().toString();
                if (!MyUtil.isEmpty(name) && !MyUtil.isEmpty(number)){
                    SosNumber sosNumber = new SosNumber(name,number);
                    sosNumberList.add(sosNumber);
                    sosListAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                    addSosNumberToServer(sosNumber);
                }
                else{
                    MyUtil.showToask(SosActivity.this,"未输入姓名或号码");
                }
            }
        });
        bt_choose_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });*/

       /* Uri uri = Uri.parse("content://contacts/people");
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        startActivityForResult(intent, 0);*/
        startActivityForResult(new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI), 0);
    }

    //  跳转联系人列表的回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if(data==null) { return; }
                //处理返回的data,获取选择的联系人信息
                Uri uri=data.getData();
                String[] contacts=getPhoneContacts(uri);
                Log.i(TAG,"contacts:"+contacts[0]+", "+contacts[1]);
                //严莹莹, 13714387129
                if (contacts[1]!=null && contacts[1].startsWith("+")){
                    contacts[1] = contacts[1].substring(3);
                }
                SosNumber sosNumber = new SosNumber(contacts[0],contacts[1]);
                addSosNumberToServer(sosNumber);

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String[] getPhoneContacts(Uri uri){
        String[] contact=new String[2];
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor=cr.query(uri,null,null,null,null);
        if(cursor!=null) {
            cursor.moveToFirst();
            //取得联系人姓名
            int nameFieldColumnIndex=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0]=cursor.getString(nameFieldColumnIndex);

            Log.i(TAG,"contact[0] :"+contact[0]);
            //取得电话号码
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Log.i(TAG,"contactId :"+contactId);
            Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);

            Log.i(TAG,"phone.getCount(): "+phone.getCount());
             boolean moveToFirst = phone.moveToFirst();
            if(phone != null && moveToFirst){
                Log.i(TAG,"moveToFirst: "+moveToFirst);
                contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            else {
                MyUtil.showToask(SosActivity.this,"号码格式不正确");
            }
            phone.close();
            cursor.close();
        } else {
            return null;
        }
        return contact;
    }

    private void addSosNumberToServer(final SosNumber sosNumber) {
        MyUtil.showDialog("正在添加",this);
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("name",sosNumber.name);
        params.addBodyParameter("phone",sosNumber.phone);
        MyUtil.addCookieForHttp(params);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.addSosContact, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(SosActivity.this);
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                //JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    double id = (double)jsonBase.errDesc;
                    int intID = (int) id;
                    sosNumber.setId(intID+"");
                    sosNumberList.add(sosNumber);
                    sosListAdapter.notifyDataSetChanged();
                    MyUtil.putSosNumberList(sosNumberList);
                    MyUtil.showToask(SosActivity.this,"添加成功");
                }
                else {
                    MyUtil.showToask(SosActivity.this,"添加失败,"+jsonBase.errDesc);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(SosActivity.this);
                Log.i(TAG,"上传onFailure==s:"+s);
                MyUtil.showToask(SosActivity.this,"添加失败,"+s);
            }
        });
    }

    private class SosListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return sosNumberList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SosNumber sosNumber = sosNumberList.get(position);
            View inflate = View.inflate(SosActivity.this, R.layout.item_sosnumber_list, null);
            TextView tv_item_name = (TextView) inflate.findViewById(R.id.tv_item_name);
            TextView tv_item_number = (TextView) inflate.findViewById(R.id.tv_item_number);
            tv_item_name.setText(sosNumber.name);
            tv_item_number.setText(sosNumber.phone);
            return inflate;
        }
    }

    class SosInfo{
        public String ret;
        public ContantsInfo errDesc;

        @Override
        public String toString() {
            return "SosInfo{" +
                    "ret='" + ret + '\'' +
                    ", errDesc=" + errDesc +
                    '}';
        }

        class ContantsInfo{
            List<SosNumber> contacts;
            String message;

            @Override
            public String toString() {
                return "ContantsInfo{" +
                        "contacts=" + contacts +
                        ", message='" + message + '\'' +
                        '}';
            }
        }
    }

    public class SosNumber {
        private String id;
        private String name;
        public String phone;

        public SosNumber(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }

        @Override
        public String toString() {
            return "SosNumber{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", phone='" + phone + '\'' +
                    '}';
        }

        public void setId(String id) {
            this.id = id;
        }
    }


}
