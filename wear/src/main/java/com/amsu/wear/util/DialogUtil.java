package com.amsu.wear.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

/**
 * @anthor haijun
 * @project name: Shop
 * @class name：com.haijun.shop.util
 * @time 2018-02-02 2:15 PM
 * @describe
 */
public class DialogUtil {
    private static final String TAG = "DialogUtil";
    private static ProgressDialog dialog;

    public static void showDialog(String message,Context context){
        try {
            if (dialog!=null && dialog.isShowing()){
                dialog.setMessage(message);
                Log.i(TAG,"dialog.pudate msssage;");
            }
            else {
                dialog = new ProgressDialog(context);
                dialog.setCanceledOnTouchOutside(false);
                //dialog.setProgressStyle(R.style.progresStyle);
                dialog.setMessage(message);
                dialog.show();
                Log.i(TAG,"dialog.show();");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 在其他线程调用dialog会报错
        }
        Log.i(TAG,"showDialog:"+dialog.isShowing());
    }

    public static void setDialogNull(){
        dialog = null;
    }

    public static void hideDialog(Context context) {
        if (context == null) return;
        try{
            if (dialog != null && dialog.isShowing()){
                dialog.dismiss();
                Log.i(TAG,"hideDialog:"+dialog.isShowing());
                dialog = null;
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

}
