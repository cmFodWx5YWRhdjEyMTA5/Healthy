package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.ClubCampaign;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.DateTimeDialogOnlyYMD;
import com.amsu.healthy.view.PickerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.amsu.healthy.R.id.iv_devicerun_switvh;

public class SetupCampaignActivity extends BaseActivity implements DateTimeDialogOnlyYMD.MyOnDateSetListener{

    private static final String TAG = "SetupCampaignActivity";
    private boolean isNeedOpen;
    private ImageView iv_campaign_switvh;
    private EditText et_campaign_title;
    private EditText et_campaign_date;
    private EditText et_campaign_type;
    private EditText et_campaign_time;
    private EditText et_campaign_description;
    private EditText et_campaign_allnumber;
    private String protocolContent;
    private DateTimeDialogOnlyYMD dateTimeDialogOnlyYMD;
    private String mTime =  "";
    private String mStartTime =  "";
    private String mEndTime =  "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_campaign);

        initView();

    }

    private void initView() {
        initHeadView();
        setCenterText("创建活动");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_campaign_switvh = (ImageView) findViewById(R.id.iv_campaign_switvh);
        et_campaign_title = (EditText) findViewById(R.id.et_campaign_title);
        et_campaign_type = (EditText) findViewById(R.id.et_campaign_type);
        et_campaign_date = (EditText) findViewById(R.id.et_campaign_date);
        et_campaign_time = (EditText) findViewById(R.id.et_campaign_time);
        et_campaign_description = (EditText) findViewById(R.id.et_campaign_description);
        et_campaign_allnumber = (EditText) findViewById(R.id.et_campaign_allnumber);

        dateTimeDialogOnlyYMD = new DateTimeDialogOnlyYMD(this, this, true, true, true);

    }

    public void switchState(View view) {
        //切换自动分析状态
        if (!isNeedOpen){
            iv_campaign_switvh.setImageResource(R.drawable.switch_on);
            isNeedOpen = true;
            MyUtil.putBooleanValueFromSP("isAutoOpen",true);
        }
        else {
            iv_campaign_switvh.setImageResource(R.drawable.switch_of);
            isNeedOpen = false;
            MyUtil.putBooleanValueFromSP("isAutoOpen",false);
        }
    }

    //提交数据
    public void submit(View view) {
        String title = et_campaign_title.getText().toString();
        String type = et_campaign_type.getText().toString();
        String date = et_campaign_date.getText().toString();
        String time = et_campaign_time.getText().toString();
        String description = et_campaign_description.getText().toString();
        String allnumber = et_campaign_allnumber.getText().toString();

        if (MyUtil.isEmpty(title)){
            MyUtil.showToask(this,"输入标题");
        }
        else if (MyUtil.isEmpty(type)){
            MyUtil.showToask(this,"输入活动类型");
        }
        else if (MyUtil.isEmpty(date)){
            MyUtil.showToask(this,"输入日期");
        }
        else if (MyUtil.isEmpty(time)){
            MyUtil.showToask(this,"输入时间");
        }
        else if (MyUtil.isEmpty(allnumber)){
            MyUtil.showToask(this,"输入活动人数");
        }
        else if (MyUtil.isEmpty(description)){
            MyUtil.showToask(this,"输入活动描述");
        }
        else if (MyUtil.isEmpty(protocolContent)){
            MyUtil.showToask(this,"输入活动协议");
        }
        else{
            //上传数据
            ClubCampaign clubCampaign = new ClubCampaign(title,date,time,type,"0",allnumber,description,"");  //活动头像为发起人的头像

            Log.i(TAG,"clubCampaign:"+clubCampaign.toString());

            //成功
            Intent intent = getIntent();
            Bundle bundle = new Bundle();
            bundle.putParcelable("clubCampaign",clubCampaign);
            intent.putExtra("bundle",bundle);
            setResult(RESULT_OK,intent);

            finish();
        }

    }

    //编辑协议内容
    public void editprotocol(View view) {
        startActivityForResult(new Intent(this,EditProtocolActivity.class),101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101 && resultCode==RESULT_OK){
            boolean isNeedOpen = data.getBooleanExtra("isNeedOpen",false);
            protocolContent = data.getStringExtra("content");

        }
    }

    //选择日期
    public void chooseDate(View view) {
        dateTimeDialogOnlyYMD.hideOrShow();
    }

    @Override
    public void onDateSet(Date date) {
        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDate();

        Log.i(TAG,"onDateSet:"+year+","+month+","+day);
        String text = year + "-" + month + "-" + day;
        et_campaign_date.setText(text);   //          1998/12/21
    }

    //选择时间
    public void chooseTimeDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //初始化自定义布局参数
        LayoutInflater layoutInflater = getLayoutInflater();
        final View customLayout = layoutInflater.inflate(R.layout.dialog_pick_time, (ViewGroup)findViewById(R.id.customDialog));
        Button bt_pick_cancel = (Button) customLayout.findViewById(R.id.bt_pick_cancel);
        Button bt_pick_ok = (Button) customLayout.findViewById(R.id.bt_pick_ok);

        //为对话框设置视图
        builder.setView(customLayout);
        PickerView pv_time_start = (PickerView)customLayout.findViewById(R.id.pv_time_start);
        PickerView pv_time_end = (PickerView)customLayout.findViewById(R.id.pv_time_end);
        //定义滚动选择器的数据项
        final ArrayList<String> timeData = new ArrayList<>();
        for(int i=0;i<24;i++){
            timeData.add(i+":00");
        }

        mStartTime = mEndTime = timeData.get(timeData.size()/2);
        //省份的数据
        pv_time_start.setData(timeData);
        pv_time_end.setData(timeData);

        pv_time_start.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.i(TAG,"选择了"+timeData.get(position));
                mStartTime = timeData.get(position);
            }
        });

        pv_time_end.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.i(TAG,"选择了"+timeData.get(position));
                mEndTime = timeData.get(position);
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
                mTime = mStartTime+"-"+mEndTime;
                et_campaign_time.setText(mTime);
            }
        });
    }
}
