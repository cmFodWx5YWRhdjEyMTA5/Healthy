package com.amsu.healthy.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.amsu.healthy.utils.MyUtil;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.receiver
 * @time 11/23/2017 2:18 PM
 * @describe
 */
public class SmsReceiver extends BroadcastReceiver {
    public static final String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    public static final String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        //mTextView01.setText(intent.getAction().toString());
        if (intent.getAction().equals(SMS_SEND_ACTIOIN)) {
            try {
                    /* android.content.BroadcastReceiver.getResultCode()方法 */
                //Retrieve the current result code, as set by the previous receiver.
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                            /* 发送短信成功 */
                        //mTextView01.setText(R.string.str_sms_sent_success);
                        MyUtil.showToask(context, "发送短信成功");
                        MyUtil.showDialog("发送短信成功", context);
                        MyUtil.hideDialog(context);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            /* 发送短信失败 */
                        //mTextView01.setText(R.string.str_sms_sent_failed);
                        MyUtil.showToask(context, "发送短信失败 ");
                        MyUtil.showDialog("发送短信失败", context);
                        MyUtil.hideDialog(context);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        break;
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        } else if (intent.getAction().equals(SMS_DELIVERED_ACTION)) {
            try {
                    /* android.content.BroadcastReceiver.getResultCode()方法 */
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                            /* 短信 */
                        //mTextView01.setText(R.string.str_sms_sent_success);
                        //MyUtil.showToask(HealthyDataActivity.this,"短信");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            /* 短信未送达 */
                        //mTextView01.setText(R.string.str_sms_sent_failed);
                        //MyUtil.showToask(HealthyDataActivity.this,"短信未送达");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        break;
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

}