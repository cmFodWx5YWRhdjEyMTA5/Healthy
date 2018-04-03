package com.amsu.wear.util;

/**
 * Created by haijun on 2018/2/3.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;


/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.healthy.utils
 * @time 2018-01-11 8:43 PM
 * @describe
 */
public class ShowToaskDialogUtil {
    public static void showTipDialog(Context context, String message, DialogInterface.OnClickListener onClickListener){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(message)
                .setPositiveButton("确定", onClickListener)
                .setNegativeButton("取消",null)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static void showTipDialog(Context context,String message){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(message)
                .setPositiveButton("确定", null)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}