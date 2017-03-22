package com.amsu.healthy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.ProvinceModel;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.ParseXmlDataUtil;
import com.amsu.healthy.view.DateTimeDialogOnlyYMD;
import com.amsu.healthy.view.PickerView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SupplyPersionDataActivity extends BaseActivity implements DateTimeDialogOnlyYMD.MyOnDateSetListener{

    private static final String TAG = "SupplyPersionData";
    private DateTimeDialogOnlyYMD dateTimeDialogOnlyYMD;
    private TextView tv_step2_birthday;
    private TextView tv_step2_sex;
    private TextView tv_step2_weight;
    private TextView tv_step2_height;
    private TextView tv_step2_area;
    public PickerView pickerView;
    private String username = "";
    private String birthday = "";
    private String weightValue = "";
    private String heightValue = "";
    private List<ProvinceModel> provinceModels;
    private String province;
    private String city;
    private String area = "" ;
    private int sex =-1;
    private EditText tv_step2_username;
    private int year;
    private int month;
    private int day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_persiondata);

        initView();

        initData();
        
    }

  

    private void initView() {
        initHeadView();
        setLeftImage(R.drawable.guanbi_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout rl_step2_birthday = (RelativeLayout) findViewById(R.id.rl_step2_birthday);
        RelativeLayout rl_step2_sex = (RelativeLayout) findViewById(R.id.rl_step2_sex);
        RelativeLayout rl_step2_weight = (RelativeLayout) findViewById(R.id.rl_step2_weight);
        RelativeLayout rl_step2_height = (RelativeLayout) findViewById(R.id.rl_step2_height);
        RelativeLayout rl_step2_area = (RelativeLayout) findViewById(R.id.rl_step2_area);
        Button t_step_nextstep = (Button) findViewById(R.id.t_step_nextstep);
        tv_step2_birthday = (TextView) findViewById(R.id.tv_step2_birthday);
        tv_step2_sex = (TextView) findViewById(R.id.tv_step2_sex);
        tv_step2_weight = (TextView) findViewById(R.id.tv_step2_weight);
        tv_step2_height = (TextView) findViewById(R.id.tv_step2_height);
        tv_step2_area = (TextView) findViewById(R.id.tv_step2_area);
        tv_step2_username = (EditText) findViewById(R.id.tv_step2_username);

        MyOnclickListener myOnclickListener = new MyOnclickListener();
        rl_step2_birthday.setOnClickListener(myOnclickListener);
        rl_step2_sex.setOnClickListener(myOnclickListener);
        rl_step2_weight.setOnClickListener(myOnclickListener);
        rl_step2_height.setOnClickListener(myOnclickListener);
        rl_step2_area.setOnClickListener(myOnclickListener);
        t_step_nextstep.setOnClickListener(myOnclickListener);
    }

    private void initData() {
        dateTimeDialogOnlyYMD = new DateTimeDialogOnlyYMD(this, this, true, true, true);
    }

    @Override
    public void onDateSet(Date date) {
        year = date.getYear() + 1900;
        month = date.getMonth() + 1;
        day = date.getDate();

        Log.i(TAG,"onDateSet:"+ year +","+ month +","+ day);
        tv_step2_birthday.setText(year +"-"+ month +"-"+ day);   //          1998/12/21
        birthday = year +"-"+ month +"-"+ day;
        if (month<10){
            birthday = year+"-0"+month+"-"+day;
        }
    }

    class MyOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_step2_birthday:
                    dateTimeDialogOnlyYMD.hideOrShow();
                    break;
                case R.id.rl_step2_sex:
                    chooseSexDialog();
                    break;
                case R.id.rl_step2_weight:
                    chooseWeightDialog();
                    break;
                case R.id.rl_step2_height:
                    chooseHeightDialog();
                    break;
                case R.id.rl_step2_area:
                    chooseAreaDialog();
                    break;
                case R.id.t_step_nextstep:

                    ToStep3Register();

                    break;
            }
        }


    }

    private void ToStep3Register() {
        username = tv_step2_username.getText().toString();
        if (username.isEmpty()){
            Toast.makeText(this,"昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (birthday.isEmpty()){
            Toast.makeText(this,"请输入生日", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (sex==-1){
            Toast.makeText(this,"请输入性别", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (weightValue.isEmpty()){
            Toast.makeText(this,"请输入体重", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (heightValue.isEmpty()){
            Toast.makeText(this,"请输入身高", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (area.isEmpty()){
            Toast.makeText(this,"请输入地区", Toast.LENGTH_SHORT).show();
            return;
        }

        registerToDB();

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
                tv_step2_area.setText(area);
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
                heightValue = grade.get(position)+"";
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
                tv_step2_height.setText(heightValue+"cm");
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
                tv_step2_weight.setText(weightValue+"kg");
            }
        });
    }

    //选择性别
    private void chooseSexDialog() {
        final String[] items = new String[] { "女", "男" };

        new AlertDialog.Builder(this).
                setTitle("选择性别")
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        tv_step2_sex.setText(items[which]);
                        sex=which==0?2:1;
                    }
                })
                .show();
    }

    //上传数据
    private void registerToDB() {
        MyUtil.showDialog("正在上传",this);
        final String phone = MyUtil.getStringValueFromSP("phone");
        final String sexString = String.valueOf(sex);

        //保存用户信息
        final User user = new User(phone,username,birthday,sexString,weightValue,heightValue,area);
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
        params.addBodyParameter("Email","");
        params.addBodyParameter("RestingHeartRate","70");

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
                    MyUtil.showToask(SupplyPersionDataActivity.this,errDesc);
                    if (ret==0){
                        //个人资料完善成功
                        MyUtil.putBooleanValueFromSP("isPrefectInfo",true);

                        birthday = year+"-"+month+"-"+day;
                        user.setBirthday(birthday);
                        MyUtil.saveUserToSP(user);
                        startActivity(new Intent(SupplyPersionDataActivity.this,MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MyUtil.showToask(SupplyPersionDataActivity.this,"解析错误");
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
