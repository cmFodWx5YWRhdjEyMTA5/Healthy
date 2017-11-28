package com.amsu.healthy.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import com.amsu.healthy.activity.SosActivity;
import com.amsu.healthy.receiver.SmsReceiver;

import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils
 * @time 11/23/2017 2:23 PM
 * @describe
 */
public class SosSendUtil {


    private static final String TAG = "SosSendUtil";

    public static void startSoS(Context context) {
        SmsManager smsManager = SmsManager.getDefault();
        List<SosActivity.SosNumber> sosNumberList = MyUtil.getSosNumberList();
        String sosinfo = MyUtil.getStringValueFromSP(Constant.sosinfo);

        try {
            //建立自定义Action常数的Intent(给PendingIntent参数之用)
            Intent itSend = new Intent(SmsReceiver.SMS_SEND_ACTIOIN);
            Intent itDeliver = new Intent(SmsReceiver.SMS_DELIVERED_ACTION);

            //sentIntent参数为传送后接受的广播信息PendingIntent
            PendingIntent mSendPI = PendingIntent.getBroadcast(context, 0, itSend, 0);

            //deliveryIntent参数为送达后接受的广播信息PendingIntent
            PendingIntent mDeliverPI = PendingIntent.getBroadcast(context, 0, itDeliver, 0);

            //发送SMS短信，注意倒数的两个PendingIntent参数

            if (sosNumberList==null  || sosNumberList.size()==0){
                context.startActivity(new Intent(context,SosActivity.class));
                return;
            }

            for (SosActivity.SosNumber sosNumber:sosNumberList){
                smsManager.sendTextMessage(sosNumber.phone, null, sosinfo, mSendPI, mDeliverPI);
            }
            MyUtil.showDialog("正在发送",context);
            Log.i(TAG,"sendTextMessage");
        }
        catch(Exception e) {
            if (e instanceof SecurityException){//没有权限，调到短信发送界面
                if (sosNumberList!=null  && sosNumberList.size()>0){
                    String phones = "";
                    for (SosActivity.SosNumber sosNumber:sosNumberList){
                        phones += sosNumber.phone+";";
                    }
                    Uri smsToUri = Uri.parse("smsto:"+phones);
                    Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                    //短信内容
                    intent.putExtra("sms_body", sosinfo);
                    context.startActivity(intent);
                }
            }
            Log.e(TAG,"e:"+e);
        }
    }
}
