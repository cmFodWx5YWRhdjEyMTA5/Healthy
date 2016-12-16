package com.amsu.healthy.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.ProvinceModel;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyBitMapUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.ParseXmlDataUtil;
import com.amsu.healthy.view.CircleImageView;
import com.amsu.healthy.view.DateTimeDialogOnlyYMD;
import com.amsu.healthy.view.PickerView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersionDataActivity extends BaseActivity implements DateTimeDialogOnlyYMD.MyOnDateSetListener{

    private static final String TAG = "PersionDataActivity";
    private TextView tv_persiondata_name;
    private TextView tv_persiondata_birthday;
    private TextView tv_persiondata_sex;
    private TextView tv_persiondata_weight;
    private TextView tv_persiondata_height;
    private TextView tv_persiondata_phone;
    private TextView tv_persiondata_area;
    private TextView tv_persiondata_email;
    private User userFromSP;

    private String birthday = "";
    private String weightValue = "";
    private String heightValue = "";
    private List<ProvinceModel> provinceModels;
    private String province;
    private String city;
    private String area = "" ;
    public PickerView pickerView;
    private DateTimeDialogOnlyYMD dateTimeDialogOnlyYMD;
    private Button bt_dialog_take;
    private Button bt_dialog_choose;
    private Button bt_dialog_cancel;
    private MyOnClickListener myOnClickListener;
    private File currentImageSaveFile;
    private CircleImageView cv_persiondata_headicon;
    private String iconFilePath;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persion_data);

        initView();


        initData();




    }



    private void initView() {
        initHeadView();
        setCenterText("个人资料");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout rl_persiondata_headicon = (RelativeLayout) findViewById(R.id.rl_persiondata_headicon);
        RelativeLayout rl_persiondata_name = (RelativeLayout) findViewById(R.id.rl_persiondata_name);
        RelativeLayout rl_persiondata_birthday = (RelativeLayout) findViewById(R.id.rl_persiondata_birthday);
        RelativeLayout rl_persiondata_sex = (RelativeLayout) findViewById(R.id.rl_persiondata_sex);
        RelativeLayout rl_persiondata_weight = (RelativeLayout) findViewById(R.id.rl_persiondata_weight);
        RelativeLayout rl_persiondata_height = (RelativeLayout) findViewById(R.id.rl_persiondata_height);
        RelativeLayout rl_persiondata_area = (RelativeLayout) findViewById(R.id.rl_persiondata_area);
        RelativeLayout rl_persiondata_phone = (RelativeLayout) findViewById(R.id.rl_persiondata_phone);
        RelativeLayout rl_persiondata_email = (RelativeLayout) findViewById(R.id.rl_persiondata_email);

        tv_persiondata_name = (TextView) findViewById(R.id.tv_persiondata_name);
        tv_persiondata_birthday = (TextView) findViewById(R.id.tv_persiondata_birthday);
        tv_persiondata_sex = (TextView) findViewById(R.id.tv_persiondata_sex);
        tv_persiondata_weight = (TextView) findViewById(R.id.tv_persiondata_weight);
        tv_persiondata_height = (TextView) findViewById(R.id.tv_persiondata_height);
        tv_persiondata_area = (TextView) findViewById(R.id.tv_persiondata_area);
        tv_persiondata_phone = (TextView) findViewById(R.id.tv_persiondata_phone);
        tv_persiondata_email = (TextView) findViewById(R.id.tv_persiondata_email);
        cv_persiondata_headicon = (CircleImageView) findViewById(R.id.cv_persiondata_headicon);

        Button bt_persion_save = (Button) findViewById(R.id.bt_persion_save);

        myOnClickListener = new MyOnClickListener();

        rl_persiondata_headicon.setOnClickListener(myOnClickListener);
        rl_persiondata_name.setOnClickListener(myOnClickListener);
        rl_persiondata_birthday.setOnClickListener(myOnClickListener);
        rl_persiondata_sex.setOnClickListener(myOnClickListener);
        rl_persiondata_weight.setOnClickListener(myOnClickListener);
        rl_persiondata_height.setOnClickListener(myOnClickListener);
        rl_persiondata_area.setOnClickListener(myOnClickListener);
        rl_persiondata_phone.setOnClickListener(myOnClickListener);
        rl_persiondata_email.setOnClickListener(myOnClickListener);
        bt_persion_save.setOnClickListener(myOnClickListener);

        dateTimeDialogOnlyYMD = new DateTimeDialogOnlyYMD(this, this, true, true, true);

    }

    private void initData() {
        userFromSP = MyUtil.getUserFromSP();
        if (userFromSP!=null){
            tv_persiondata_name.setText(userFromSP.getUsername());

            String birthday = userFromSP.getBirthday();  //	1998/12/21  ===1999-11-11
            if (!birthday.equals("")){
                String[] split = birthday.split("-");
                String newBirthday = split[0]+"-"+split[1]+"-"+split[2];
                tv_persiondata_birthday.setText(newBirthday);
            }

            String sex = userFromSP.getSex();
            if (sex.equals("1")){
                tv_persiondata_sex.setText("男");
            }
            else if (sex.equals("2")){
                tv_persiondata_sex.setText("女");
            }

            tv_persiondata_weight.setText(userFromSP.getWeight()+"kg");
            tv_persiondata_height.setText(userFromSP.getHeight()+"cm");
            tv_persiondata_area.setText(userFromSP.getArea());

            tv_persiondata_phone.setText(userFromSP.getPhone());
            tv_persiondata_email.setText(userFromSP.getEmail());

            String iconUrl = userFromSP.getIcon();
            if (!iconUrl.equals("")){
                if (iconUrl.endsWith("jpg") || iconUrl.endsWith("png") || iconUrl.endsWith("jpeg") || iconUrl.endsWith("gif")){
                    BitmapUtils bitmapUtils = new BitmapUtils(this);
                    bitmapUtils.display(cv_persiondata_headicon,iconUrl);
                }

            }
        }


    }

    @Override
    public void onDateSet(Date date) {
        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDate();

        Log.i(TAG,"onDateSet:"+year+","+month+","+day);
        tv_persiondata_birthday.setText(year+"/"+month+"/"+day);   //          1998/12/21
        birthday = year+"/"+month+"/"+day;
    }

    class MyOnClickListener implements View.OnClickListener {
        Intent intent = new Intent(PersionDataActivity.this,ModifyPersionDataActivity.class);
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_persiondata_headicon:
                    choosePicture();
                    break;
                case R.id.rl_persiondata_name:
                    intent.putExtra("modifyType", Constant.MODIFY_USERNSME);
                    intent.putExtra("modifyValue",userFromSP.getUsername());
                    startActivityForResult(intent,100);
                    break;
                case R.id.rl_persiondata_sex:
                    intent.putExtra("modifyType", Constant.MODIFY_SEX);
                    intent.putExtra("modifyValue",userFromSP.getSex());
                    startActivityForResult(intent,100);
                    break;
                case R.id.rl_persiondata_email:
                    intent.putExtra("modifyType", Constant.MODIFY_EMAIL);
                    intent.putExtra("modifyValue",userFromSP.getEmail());
                    startActivityForResult(intent,100);
                    break;
                case R.id.rl_persiondata_birthday:
                    dateTimeDialogOnlyYMD.hideOrShow();
                    break;
                case R.id.rl_persiondata_area:
                    chooseAreaDialog();
                    break;
                case R.id.rl_persiondata_weight:
                    chooseWeightDialog();
                    break;
                case R.id.rl_persiondata_height:
                    chooseHeightDialog();
                    break;
                case R.id.bt_persion_save:
                    registerToDB();
                    break;
                case R.id.bt_dialog_choose:
                    bottomSheetDialog.dismiss();
                    chooosePicture();
                    break;
                case R.id.bt_dialog_take:
                    bottomSheetDialog.dismiss();
                    takePicture();
                    break;
                case R.id.bt_dialog_cancel:
                    bottomSheetDialog.dismiss();
                    break;
            }
        }
    }

    private void chooosePicture() {
        Intent intent = new Intent();
        //匹配其过滤器
        intent.setAction("android.intent.action.PICK");
        intent.setType("image/*");
        startActivityForResult(intent,101);
    }

    private void takePicture() {
        Intent tackIntent = new Intent();
        //匹配其过滤器
        tackIntent.setAction("android.media.action.IMAGE_CAPTURE");
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/tiyu");
        if (!file.exists()){
            boolean mkdirs = file.mkdirs();
            if (!mkdirs){
                return;
            }
        }
        ///storage/emulated/0/cts/1469067312871.png
        currentImageSaveFile = new File(file,System.currentTimeMillis() + ".png");
        tackIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageSaveFile));
        startActivityForResult(tackIntent,102);
    }

    private void choosePicture() {
        bottomSheetDialog = new BottomSheetDialog(PersionDataActivity.this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.choose_pcicture_dailog, null);

        bottomSheetDialog.setContentView(inflate);
        Window window = bottomSheetDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.mystyle);  //添加动画
        bottomSheetDialog.show();

        bt_dialog_take = (Button) inflate.findViewById(R.id.bt_dialog_take);
        bt_dialog_choose = (Button) inflate.findViewById(R.id.bt_dialog_choose);
        bt_dialog_cancel = (Button) inflate.findViewById(R.id.bt_dialog_cancel);

        bt_dialog_take.setOnClickListener(myOnClickListener);
        bt_dialog_choose.setOnClickListener(myOnClickListener);
        bt_dialog_cancel.setOnClickListener(myOnClickListener);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"onActivityResult=="+"requestCode:"+requestCode+",resultCode:"+resultCode);
        if (resultCode==RESULT_OK){
            if (requestCode==100){
                int modifyType = data.getIntExtra("modifyType", -1);
                String modifyValue = data.getStringExtra("modifyValue");
                Log.i(TAG,"modifyType:"+modifyType+",modifyValue:"+modifyValue);
                if(modifyType!=-1){
                    if (modifyType == Constant.MODIFY_USERNSME){
                        tv_persiondata_name.setText(modifyValue);
                    }
                    else if (modifyType ==Constant.MODIFY_SEX){
                        tv_persiondata_sex.setText(modifyValue);
                    }
                    else if (modifyType ==Constant.MODIFY_EMAIL){
                        tv_persiondata_email.setText(modifyValue);
                    }
                    else if (modifyType ==Constant.MODIFY_PHONE){
                        //电话，暂不修改
                        //tv_persiondata_name.setText(modifyValue);
                    }

                }
            }
            else if (requestCode==101){
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null,null);
                if (cursor != null && cursor.moveToFirst()) {
                    iconFilePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)); //storage/emulated/0/360Browser/download/20151006063040806.jpg
                    currentImageSaveFile = new File(iconFilePath);
                    Log.i(TAG,"iconFilePath:"+ iconFilePath);
                    Log.i(TAG,"currentImageSaveFile.length():"+ currentImageSaveFile.length());
                }
                if (cursor != null) {
                    cursor.close();
                }
                showImageAndUpload(iconFilePath);


            }
            else if (requestCode==102){
                iconFilePath = currentImageSaveFile.getAbsolutePath();
                Log.i(TAG,"iconFilePath:"+ iconFilePath);
                Log.i(TAG,"currentImageSaveFile.length():"+ currentImageSaveFile.length());
                showImageAndUpload(iconFilePath);
            }
        }
    }

    //显示，上传
    private void showImageAndUpload(String iconFilePath) {

        if (iconFilePath!=null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize= 6;

            Bitmap bitmap = BitmapFactory.decodeFile(iconFilePath,options);
            bitmap = MyBitMapUtil.compressImage(bitmap);


            cv_persiondata_headicon.setImageBitmap(bitmap);


            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();

            File file = MyBitMapUtil.saveBitmapFile(bitmap, this);
            long length = file.length();
            Log.i(TAG,"压缩后length:"+length);

            //params.addBodyParameter("userfile",currentImageSaveFile);
            params.addBodyParameter("userfile",file);
            MyUtil.addCookieForHttp(params);

            httpUtils.send(HttpRequest.HttpMethod.POST, Constant.uploadIconURL, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String result = responseInfo.result;
                    Log.i(TAG,"onSuccess==result:"+result);
                    /*{
                        "ret": "0",
                            "errDesc":"http://119.29.201.120:83/xxxx/xxxx.jpg"
                    }*/
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        int ret = jsonObject.getInt("ret");
                        String errDesc = jsonObject.getString("errDesc");
                        if (ret==0){
                            MyUtil.showToask(PersionDataActivity.this,"上传成功");
                            MyUtil.putStringValueFromSP("icon",errDesc);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i(TAG,"onFailure==s:"+s);
                }
            });

        }
    }




    private void chooseAreaDialog() {
        initProvinceData();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //初始化自定义布局参数
        LayoutInflater layoutInflater = getLayoutInflater();
        final View customLayout = layoutInflater.inflate(R.layout.dialog_pick_area, (ViewGroup)findViewById(R.id.customDialog));
        Button bt_pick_cancel = (Button) customLayout.findViewById(R.id.bt_pick_cancel);
        Button bt_pick_ok = (Button) customLayout.findViewById(R.id.bt_pick_ok);

        //为对话框设置视图
        builder.setView(customLayout);
        PickerView picker_provice = (PickerView)customLayout.findViewById(R.id.picker_provice);
        final PickerView picker_city = (PickerView)customLayout.findViewById(R.id.picker_city);
        //定义滚动选择器的数据项
        final ArrayList<String> grade = new ArrayList<>();
        for(int i=0;i<provinceModels.size();i++){
            grade.add(provinceModels.get(i).getName());
        }


        //省份的数据
        picker_provice.setData(grade);
        province = grade.get(grade.size()/2);
        picker_provice.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.i(TAG,"选择了"+grade.get(position));
                province = grade.get(position);
                //heightValue = text;
                int provincePosition = getTextPosition(grade.get(position));

                //省份切换时的城市改变
                final List<String> cityList = provinceModels.get(provincePosition).getCityList();
                picker_city.setData(cityList);

                area = province+city;
                picker_city.setOnSelectListener(new PickerView.onSelectListener() {
                    @Override
                    public void onSelect(int position) {
                        Log.i(TAG,"选择了"+cityList.get(position));
                        city = cityList.get(position);
                        area = province+city;


                    }
                });
            }
        });

        //城市的默认数据
        List<String> cityList = provinceModels.get(provinceModels.size() / 2).getCityList();
        picker_city.setData(cityList);
        picker_city.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.i(TAG,"选择了"+grade.get(position));
            }
        });
        city = cityList.get(cityList.size()/2);
        area = province+city;

        //显示对话框
        final AlertDialog showAlertDialog = builder.show();

        bt_pick_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog.dismiss();
            }
        });

        bt_pick_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog.dismiss();
                tv_persiondata_area.setText(area);
            }
        });
    }

    //返回文字在List中的位置
    private int getTextPosition(String s) {
        int index = 0;
        for (int i=0;i<provinceModels.size();i++){
            if (s.equals(provinceModels.get(i).getName())){
                index =  i;
            }
        }
        return index;
    }

    private void initProvinceData() {
        provinceModels = ParseXmlDataUtil.parseXmlDataFromAssets("province_data.xml",this);
    }

    private void chooseHeightDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //初始化自定义布局参数
        LayoutInflater layoutInflater = getLayoutInflater();
        final View customLayout = layoutInflater.inflate(R.layout.dialog_pick, (ViewGroup)findViewById(R.id.customDialog));

        TextView tv_pick_name = (TextView) customLayout.findViewById(R.id.tv_pick_name);
        TextView tv_pick_unit = (TextView) customLayout.findViewById(R.id.tv_pick_unit);
        Button bt_pick_cancel = (Button) customLayout.findViewById(R.id.bt_pick_cancel);
        Button bt_pick_ok = (Button) customLayout.findViewById(R.id.bt_pick_ok);
        tv_pick_name.setText("选择身高");
        tv_pick_unit.setText("cm");

        //为对话框设置视图
        builder.setView(customLayout);
        pickerView = (PickerView)customLayout.findViewById(R.id.picker);
        //定义滚动选择器的数据项
        final ArrayList<String> grade = new ArrayList<>();
        for(int i=100;i<220;i++){
            grade.add(i+"");

        }
        heightValue = 160+"";
        //为滚动选择器设置数据
        pickerView.setData(grade);
        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.i(TAG,"选择了"+grade.get(position));
                heightValue = position+"";
            }
        });
        //显示对话框
        final AlertDialog showAlertDialog = builder.show();

        bt_pick_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog.dismiss();
            }
        });

        bt_pick_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog.dismiss();
                tv_persiondata_height.setText(heightValue+"cm");
            }
        });
    }

    private void chooseWeightDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //初始化自定义布局参数
        LayoutInflater layoutInflater = getLayoutInflater();
        final View customLayout = layoutInflater.inflate(R.layout.dialog_pick, (ViewGroup)findViewById(R.id.customDialog));

        TextView tv_pick_name = (TextView) customLayout.findViewById(R.id.tv_pick_name);
        TextView tv_pick_unit = (TextView) customLayout.findViewById(R.id.tv_pick_unit);
        Button bt_pick_cancel = (Button) customLayout.findViewById(R.id.bt_pick_cancel);
        Button bt_pick_ok = (Button) customLayout.findViewById(R.id.bt_pick_ok);
        tv_pick_name.setText("选择体重");
        tv_pick_unit.setText("kg");

        //为对话框设置视图
        builder.setView(customLayout);
        pickerView = (PickerView)customLayout.findViewById(R.id.picker);
        //定义滚动选择器的数据项
        final ArrayList<String> grade = new ArrayList<>();
        for(int i=1;i<130;i++){
            grade.add(i+"");

        }
        weightValue = 65+"";
        //为滚动选择器设置数据
        pickerView.setData(grade);
        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.i(TAG,"选择了"+grade.get(position));
                weightValue = grade.get(position);
            }
        });
        //显示对话框
        final AlertDialog showAlertDialog = builder.show();

        bt_pick_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog.dismiss();
            }
        });

        bt_pick_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog.dismiss();
                tv_persiondata_weight.setText(weightValue+"kg");
            }
        });
    }


    //上传数据
    private void registerToDB() {
        MyUtil.showDialog("正在上传",this);
        final String phone = MyUtil.getStringValueFromSP("phone");
        String sexString = tv_persiondata_sex.getText().toString();
        if (sexString.equals("男")){
            sexString = "1";
        }
        else {
            sexString = "2";
        }

        String username = tv_persiondata_name.getText().toString();
        String email = tv_persiondata_email.getText().toString();

        final User user = new User(phone,username,birthday,sexString,weightValue,heightValue,area,email);
        Log.i(TAG,"user:"+user.toString());

        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("UserName",username);
        params.addBodyParameter("Birthday",birthday);
        params.addBodyParameter("Sex",sexString);
        params.addBodyParameter("Weight",weightValue);
        params.addBodyParameter("Height",heightValue);
        params.addBodyParameter("Address",area);
        params.addBodyParameter("Phone",phone);
        params.addBodyParameter("Email",email);

        //httpUtils.configCookieStore(MyApplication.cookieStore);  //配置Cookie

        MyUtil.addCookieForHttp(params);
        String url = "https://bodylistener.amsu-new.com/intellingence/UserinfoController/uploadUserinfo"; //上传个人信息
        httpUtils.send(HttpRequest.HttpMethod.POST, url,params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog();
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int ret = jsonObject.getInt("ret");
                    String errDesc = jsonObject.getString("errDesc");
                    MyUtil.showToask(PersionDataActivity.this,errDesc);
                    if (ret==0){
                        //个人资料完善成功
                        MyUtil.putBooleanValueFromSP("isPrefectInfo",true);
                        MyUtil.saveUserToSP(user);
                        startActivity(new Intent(PersionDataActivity.this,MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog();
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }
}
