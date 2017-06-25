package com.amsu.healthy.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/4/5.
 */

//自定义AlertDialog选择框

public class InputTextAlertDialogUtil {
    Context context;

    public InputTextAlertDialogUtil(Context context) {
        this.context = context;
    }

    public void setAlertDialogText(String content){
        setAlertDialogText(null,null,null);
    }

    public void setAlertDialogText(String title,String content){
        setAlertDialogText(null,content,null);
    }

    public void setAlertDialogText(String title,String confirmString,String cancelString){
        View inflate = View.inflate(context, R.layout.view_dialog_input, null);
        TextView tv_choose_title = (TextView) inflate.findViewById(R.id.tv_choose_title);
        final EditText tv_input_name = (EditText) inflate.findViewById(R.id.tv_input_name);
        TextView bt_choose_cancel = (TextView) inflate.findViewById(R.id.bt_choose_cancel);
        TextView bt_choose_ok = (TextView) inflate.findViewById(R.id.bt_choose_ok);

        if (!MyUtil.isEmpty(title)){
            tv_choose_title.setText(title);
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
                    onCancelClickListener.onCancelClick(null);
                }

            }
        });

        bt_choose_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (onConfirmClickListener!=null){
                    String  inputText= tv_input_name.getText().toString();
                    onConfirmClickListener.onConfirmClick(inputText);
                }

            }
        });
    }

    public interface OnConfirmClickListener{
        void onConfirmClick(String inputText);
    }
    public interface OnCancelClickListener{
        void onCancelClick(String inputText);
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
