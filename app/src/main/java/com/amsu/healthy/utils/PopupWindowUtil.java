package com.amsu.healthy.utils;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.appication.MyApplication;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils
 * @time 8/22/2017 3:06 PM
 * @describe
 */
public class PopupWindowUtil {
    private static final String TAG = "MyUtil";
    private static ProgressDialog dialog;


    //显示PopWindow，提示连接状态，connectType是连接状态（1:连接,0:断开）   msg提示信息
    public void showDeviceConnectedChangePopWindow(final int connectType, final String msg) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                //Log.i(TAG,"showDeviceConnectedChangePopWindow:"+activity.getClass());
                final BaseActivity activity = MyApplication.mCurrApplicationActivity;
                Log.i(TAG,"MyApplication.mCurrApplicationActivity:"+MyApplication.mCurrApplicationActivity);
                //Log.i(TAG,"activity:"+activity.getClass());

                final boolean isConnectedSuccess;

                if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;
                View popupView = View.inflate(activity, R.layout.layout_popupwindow_onoffline, null);
                ImageView iv_pop_icon = (ImageView) popupView.findViewById(R.id.iv_pop_icon);
                TextView tv_pop_text = (TextView) popupView.findViewById(R.id.tv_pop_text);

                tv_pop_text.setText(msg);
                if (connectType == 0) {
                    //断开
                    iv_pop_icon.setImageResource(R.drawable.duankai);
                    isConnectedSuccess = false;

                } else {
                    //连接
                    iv_pop_icon.setImageResource(R.drawable.yilianjie);
                    isConnectedSuccess = true;
                }


                final PopupWindow mPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.setTouchable(true);
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), (Bitmap) null));


                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //showToask(finalActivity,"设备连接或断开");
                        //mPopupWindow.showAsDropDown(activity.getTv_base_rightText());

                        if (activity.isFinishing() || activity.isDestroyed() || activity != MyApplication.mCurrApplicationActivity) return;
                        if (!mPopupWindow.isShowing()) {
                            mPopupWindow.showAtLocation(activity.getTv_base_rightText(), Gravity.TOP, 0, 0);
                            Log.i(TAG, "PopupWindow.showAtLocation:");
                        }
                    }
                });

                try {
                    Thread.sleep(2000);
                    if (activity.isFinishing() || activity.isDestroyed() || activity != MyApplication.mCurrApplicationActivity) return;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mPopupWindow.isShowing()) {
                                mPopupWindow.dismiss();
                            }
                        }
                    });
                    /*if (MyApplication.mCurrApplicationActivity!=null && !MyApplication.mCurrApplicationActivity.isFinishing() && !activity.isDestroyed() ){
                        MyApplication.mCurrApplicationActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mPopupWindow!=null && mPopupWindow.isShowing()) {
                                    mPopupWindow.dismiss();
                                }
                            }
                        });
                    }*/
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
