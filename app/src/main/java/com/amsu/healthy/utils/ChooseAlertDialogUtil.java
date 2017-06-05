package com.amsu.healthy.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.HealthyDataActivity;
import com.amsu.healthy.activity.MoveStateActivity;

/**
 * Created by HP on 2017/4/5.
 */

//自定义AlertDialog选择框

public class ChooseAlertDialogUtil {
    Context context;

    public ChooseAlertDialogUtil(Context context) {
        this.context = context;
    }

    public void setAlertDialogText(String content){
        setAlertDialogText(null,content,null,null);
    }

    public void setAlertDialogText(String title,String content){
        setAlertDialogText(null,content,null,null);
    }

    public void setAlertDialogText(String content,String confirmString,String cancelString){
        setAlertDialogText(null,content,confirmString,cancelString);
    }

    public void setAlertDialogTextHaveTitle(String title,String confirmString,String cancelString){
        setAlertDialogText(title,null,confirmString,cancelString);
    }

    public void setAlertDialogText(String title,String content,String confirmString,String cancelString){
        View inflate = View.inflate(context, R.layout.view_dialog_showchoose, null);
        TextView tv_choose_title = (TextView) inflate.findViewById(R.id.tv_choose_title);
        TextView tv_choose_tip = (TextView) inflate.findViewById(R.id.tv_choose_tip);
        TextView bt_choose_cancel = (TextView) inflate.findViewById(R.id.bt_choose_cancel);
        TextView bt_choose_ok = (TextView) inflate.findViewById(R.id.bt_choose_ok);

        if (!MyUtil.isEmpty(title)){
            tv_choose_title.setText(title);
        }
        if (!MyUtil.isEmpty(content)){
            tv_choose_tip.setText(content);
        }else {
            tv_choose_tip.setVisibility(View.GONE);
        }

        if (!MyUtil.isEmpty(confirmString)){
            bt_choose_ok.setText(confirmString);
        }
        if (!MyUtil.isEmpty(cancelString)){
            bt_choose_cancel.setText(cancelString);
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.myCorDialog).setView(inflate).create();
        alertDialog.show();
        float width = context.getResources().getDimension(R.dimen.x800);
        float height = context.getResources().getDimension(R.dimen.x500);


        alertDialog.getWindow().setLayout(new Float(width).intValue(),new Float(height).intValue());

        bt_choose_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (onCancelClickListener!=null){
                    onCancelClickListener.onCancelClick();
                }

            }
        });

        bt_choose_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (onConfirmClickListener!=null){
                    onConfirmClickListener.onConfirmClick();
                }

            }
        });
    }

    private void setStyleAndOcClick(){

    }

    public interface OnConfirmClickListener{
        void onConfirmClick();
    }
    public interface OnCancelClickListener{
        void onCancelClick();
    }

    OnConfirmClickListener onConfirmClickListener;
    OnCancelClickListener onCancelClickListener;

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }

    public void setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }
}
