package com.amsu.healthy.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.amsu.healthy.R;

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
                .setPositiveButton(context.getResources().getString(R.string.exit_confirm), onClickListener)
                .setNegativeButton(context.getResources().getString(R.string.exit_cancel),null)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static void showTipDialog(Context context,String message){
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle(message)
                    .setPositiveButton(context.getResources().getString(R.string.exit_confirm), null)
                    .create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }catch (Exception e){
            Log.e("MyDeviceActivity","e:"+e);
        }
    }

}
