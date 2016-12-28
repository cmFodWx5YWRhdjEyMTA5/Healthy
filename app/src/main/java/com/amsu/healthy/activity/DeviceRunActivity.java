package com.amsu.healthy.activity;

import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.PickerView;

import java.util.ArrayList;

public class DeviceRunActivity extends BaseActivity {

    private static final String TAG = "DeviceRunActivity";
    private ImageView iv_devicerun_switvh;
    private boolean isOpen = false;
    private TextView tv_devicerun_space;
    private int autoSpaceText;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_run);
        initView();

    }

    private void initView() {
        initHeadView();
        setCenterText("设备运行");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_devicerun_switvh = (ImageView) findViewById(R.id.iv_devicerun_switvh);
        iv_devicerun_switvh.setImageResource(R.drawable.switch_of);
        tv_devicerun_space = (TextView) findViewById(R.id.tv_devicerun_space);

       /* ImageView iv_devicerun_switvh = (ImageView) findViewById(R.id.iv_devicerun_switvh);
        RelativeLayout rl_devicerun_period = (RelativeLayout) findViewById(R.id.rl_devicerun_period);
        RelativeLayout rl_devicerun_space = (RelativeLayout) findViewById(R.id.rl_devicerun_space);
*/
        int stringValueFromSP = MyUtil.getIntValueFromSP("autoSpace");
        if (stringValueFromSP!=-1){
            tv_devicerun_space.setText(stringValueFromSP+"小时");
        }

    }

    //切换自动分析状态
    public void switchState(View view) {
        if (!isOpen){
            iv_devicerun_switvh.setImageResource(R.drawable.switch_on);
            isOpen = true;
            MyUtil.putBooleanValueFromSP("isAutoOpen",true);
        }
        else {
            iv_devicerun_switvh.setImageResource(R.drawable.switch_of);
            isOpen = false;
            MyUtil.putBooleanValueFromSP("isAutoOpen",false);
        }
    }

    public void setSpace(View view) {
        bottomSheetDialog = new BottomSheetDialog(DeviceRunActivity.this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_picknumber, null);

        bottomSheetDialog.setContentView(inflate);
        Window window = bottomSheetDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.mystyle);  //添加动画
        bottomSheetDialog.show();

        NumberPicker np_pick = (NumberPicker) inflate.findViewById(R.id.np_pick);
        Button bt_pick_ok = (Button) inflate.findViewById(R.id.bt_pick_ok);
        Button bt_pick_cancel = (Button) inflate.findViewById(R.id.bt_pick_cancel);

        MyOnClickListener myOnClickListener = new MyOnClickListener();

        bt_pick_ok.setOnClickListener(myOnClickListener);
        bt_pick_cancel.setOnClickListener(myOnClickListener);

        np_pick.setMaxValue(48);
        np_pick.setMinValue(0);
        np_pick.setValue(1);

        np_pick.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.i(TAG,"onValueChange===oldVal:"+oldVal+",newVal:"+newVal);
                autoSpaceText = newVal;
            }
        });


    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            bottomSheetDialog.dismiss();
            switch (v.getId()){
                case R.id.bt_pick_ok:
                    tv_devicerun_space.setText(autoSpaceText+"小时");
                    MyUtil.putIntValueFromSP("autoSpace",autoSpaceText);
                    break;
                case R.id.bt_pick_cancel:

                    break;

            }
        }
    }


}
